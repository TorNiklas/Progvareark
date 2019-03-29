package com.mygdx.game.states;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.game.BTInterface;
import com.mygdx.game.TankGame;
import com.mygdx.game.network.SpriteJSON;
import com.mygdx.game.sprites.Background;
import com.mygdx.game.sprites.GUI;
import com.mygdx.game.sprites.GameSprite;
import com.mygdx.game.sprites.Ground;
import com.mygdx.game.sprites.Projectile;
import com.mygdx.game.sprites.Tank;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.cos;
import static java.lang.StrictMath.sin;

public class PlayState extends State {
    private static final AtomicInteger idCounter = new AtomicInteger(); //Highest id

    private Background bg;
    private static ArrayList<GameSprite> gameSprites;
    private static World world;
    private Box2DDebugRenderer debugRenderer;
    private Ground ground;
    private Sprite shadow;
    private GUI gui;
    private Sound explosionSound;

    // active projectiles
    private final Array<Projectile> activeProjectiles = new Array<Projectile>();

    // projectile pool
    private final Pool<Projectile> projectilePool = new Pool<Projectile>() {
        @Override
        protected Projectile newObject() {
            return new Projectile();
        }
    };

    // particle effect
    private ParticleEffect explosionEffect;
    private ParticleEffectPool particleEffectPool;
    private Array<ParticleEffectPool.PooledEffect> effects;

    private int seed;

    private int level;

    public PlayState(int level, int seed) {
        super();
        this.level = level;
        this.seed = seed;
        System.out.println("Level: " + level);
        System.out.println("Seed: " + seed);
        cam.setToOrtho(false, TankGame.WIDTH, TankGame.HEIGHT);

        // add shadow to give more depth to terrain
        shadow = new Sprite(new Texture("fade.png"));
        shadow.setSize(TankGame.WIDTH, 550);

        // load sound effects
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("sounds/explosion-7.mp3"));

        // init box2d world
        Box2D.init();
        world = new World(new Vector2(0, -50f), true);
        debugRenderer = new Box2DDebugRenderer();

        // Hit detection listener
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Body bodyA = contact.getFixtureA().getBody();
                Body bodyB = contact.getFixtureB().getBody();

                // ground hit
                if(bodyB.isBullet() && bodyA.getType() == BodyDef.BodyType.StaticBody ){
                    // explosion effect
                    Vector2 hitPos = bodyB.getPosition();
                    explodeEffect(hitPos.x, hitPos.y, 0.3f, 100);

                    // explosion sound effect
                    if(!TankGame.isMuted) {
                        explosionSound.play();
                    }

                    // delete bullet
                    bodyB.setAwake(false);
                }

                // enemy tank hit
                if(bodyB.isBullet() && bodyA == (gameSprites.get(1)).getBody() && (gameSprites.get(1)) instanceof Tank){
                    System.out.println("Enemy tank hit!" + bodyB.getPosition());
                    System.out.println("Enemy tank health was: " + ((Tank)gameSprites.get(1)).getHealth());

                    ((Tank)gameSprites.get(1)).setHealth(((Tank)gameSprites.get(1)).getHealth()-25f);

                    System.out.println("Enemy tank health now: " + ((Tank)gameSprites.get(1)).getHealth());

                    // explosion effect
                    Vector2 hitPos = bodyB.getPosition();
                    explodeEffect(hitPos.x, hitPos.y, 0.3f, 100);

                    // explosion sound effect
                    if(!TankGame.isMuted) {
                        explosionSound.play();
                    }

                    // vibrate on tank hit, maybe only if own tank is hit?
                    //Gdx.input.vibrate(500);

                    // delete bullet
                    bodyB.setAwake(false);
                }
            }

            @Override
            public void endContact(Contact contact) {
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });

        gameSprites = new ArrayList<GameSprite>();

        int guiHeight = 225;

        int spawnHeight = 100 + guiHeight;

        // send this to client
        //long seed = MathUtils.random(1000);

        // eeh way to do this, but
        switch (level) {
            // forest level
            case 1:
                spawnHeight = 100 + guiHeight;
                ground = new Ground(world, seed, 10, 30 + guiHeight, 100 + guiHeight, 10, Color.FOREST);

                // dynamic background
                bg = new Background(TankGame.WIDTH, TankGame.HEIGHT, 0, 150, 0.15f, "backgrounds/forest/");
                break;

            // snow level
            case 2:
                spawnHeight = 200 + guiHeight;
                ground = new Ground(world, seed, 10, 30 + guiHeight, 200 + guiHeight, 10, Color.WHITE);

                // dynamic background
                bg = new Background(TankGame.WIDTH, TankGame.HEIGHT, 0, 90, 0.15f, "backgrounds/forest/");
                break;

            // desert level
            case 3:
                spawnHeight = 70 + guiHeight;
                ground = new Ground(world, seed, 10, 30 + guiHeight, 70 + guiHeight, 10, Color.GOLDENROD);

                // dynamic background
                bg = new Background(TankGame.WIDTH, TankGame.HEIGHT, 0, 120, 0.15f, "backgrounds/desert/");
                break;

            // default to forest
            default:
                ground = new Ground(world, seed, 10, 30 + 250, 100 + guiHeight, 10, Color.FOREST);
                // dynamic background
                bg = new Background(TankGame.WIDTH, TankGame.HEIGHT, 0, 150, 0.15f, "backgrounds/forest/");

        }

//        int pos = 600;

        //Left tank has ID -1, right tank has id -2
        //Host is on the left
        //First added tank is local player
        if (TankGame.host) {
            gameSprites.add(new Tank(world, this, 500, spawnHeight, true, -1));
            gameSprites.add(new Tank(world, this, 600, spawnHeight, false, -2));
        }
        else {
            gameSprites.add(new Tank(world, this, 600, spawnHeight, true, -2));
            gameSprites.add(new Tank(world, this, 500, spawnHeight, false, -1));
        }
/*
        gameSprites.add(new Tank(world, this, pos, spawnHeight));
        gameSprites.add(new Tank(world, this, 600, spawnHeight));*/

        // test simple gui
        gui = new GUI(this, guiHeight);

        // particle effect
        explosionEffect = new ParticleEffect();
        explosionEffect.load(Gdx.files.internal("effects/explosion.p"), Gdx.files.internal("effects"));
        particleEffectPool = new ParticleEffectPool(explosionEffect, 0, 10);
        effects = new Array<ParticleEffectPool.PooledEffect>();

        /*Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                readNetSprites();
            }
        }, 100, 100, TimeUnit.MILLISECONDS);*/
    }

    public void fireFromPool(Projectile.AmmoType type, Vector2 pos, Vector2 force, boolean local) {
        System.out.println("FIRING " + pos.x + " - " + pos.y);
        System.out.println("FORCE " + force.x + " - " + force.y);
        System.out.println("Local " + local);

        // TODO: add more types
        switch (type) {
            case STANDARD:
                Projectile p = projectilePool.obtain();
                p.setId(idCounter.getAndIncrement());
                p.setLocal(local);
                p.init(world, type, pos, force);
                activeProjectiles.add(p);
                gameSprites.add(p);
                break;

            case SPREAD:
                Array<Projectile> spreadProjectiles = new Array<Projectile>();

                // amount of projectiles in spread, use odd numbers
                int projectileAmt = 5;
                for(int i = 0; i < projectileAmt; i++) {
                    spreadProjectiles.add(projectilePool.obtain());
                }

                float rotStep = 15f;
                float rotRange = (projectileAmt-1) * rotStep - 2*rotStep;
                for(Projectile sp : spreadProjectiles) {
                    if(spreadProjectiles.first().equals(sp)) {
                        force.rotate(rotRange);
                    } else {
                        force.rotate(-rotStep);
                    }
                    sp.setId(idCounter.getAndIncrement());
                    sp.setLocal(local);
                    sp.init(world, type, pos, force);
                    activeProjectiles.add(sp);
                    gameSprites.add(sp);
                }
                break;
            case LASER:
                Projectile l = projectilePool.obtain();
                l.setId(idCounter.getAndIncrement());
                l.setLocal(local);
                l.init(world, type, pos, force);
                activeProjectiles.add(l);
                gameSprites.add(l);
                break;
            case AIRSTRIKE:
                Projectile as = projectilePool.obtain();
                as.setId(idCounter.getAndIncrement());
                as.setLocal(local);
                as.init(world, type, pos, force);
                activeProjectiles.add(as);
                gameSprites.add(as);
                break;
        }
    }

    public void explodeEffect(float x, float y, float scale, int duration) {
        // create effect
        ParticleEffectPool.PooledEffect effect = particleEffectPool.obtain();
        effect.setPosition(x, y);
        effect.scaleEffect(scale);
        effect.setDuration(duration);
        effect.start();
        effects.add(effect);
    }

    public void fireFromPool(Projectile.AmmoType type, Vector2 pos, Vector2 force) {
        fireFromPool(type, pos, force, true);
    }

    public static World getWorld() {
        return world;
    }

    public int getLevel() {
        return level;
    }

    @Override
    protected void handleInput() {
        if(Gdx.input.justTouched()) {
            //explodeEffect(Gdx.input.getX(), Gdx.input.getY());
        }
        /*
        // for testing purposes
        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            GameStateManager.getGsm().push(new MenuState(*//*gsm*//*));
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            ((Tank)gameSprites.get(0)).drive(new Vector2(-50f, -5f));
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            ((Tank)gameSprites.get(0)).drive(new Vector2(50f, -5f));
        }*/

    }

    public static ArrayList<SpriteJSON> getJSON() {
        ArrayList<SpriteJSON> ret = new ArrayList<SpriteJSON>();
//        System.out.println(gameSprites);

        for (GameSprite g : gameSprites) {
            if (g.isLocal()) {
                ret.add(g.getJSON());
                if (g instanceof Tank) {
                    ret.add(((Tank) g).getBarrelJSON());
                }
            }
        }

        return ret;
    }

    //Projectiles fired locally need new unique id
    //Proj from network need to set id to received one and increment idCounter to same
    private void readJSON() {
        while (!TankGame.getBluetooth().getSprites().isEmpty()) {
            SpriteJSON j = TankGame.getBluetooth().getSprites().pop();

            switch (j.getType()) {
                case PROJECTILE:
                    boolean exists = false;
                    for (Projectile p : activeProjectiles) {

                        if (p.getId() == j.getID()) {
                            //System.out.println("Found existing proj");
                            exists = true;
                            p.readJSON(j);
                            break;
                        }
                    }
                    if (!exists) {
                        idCounter.set(j.getID());
                        // TODO: check active ammotype
                        fireFromPool(Projectile.AmmoType.STANDARD, j.getPos(), j.getVel(), false);
                    }
                    break;
                case TANK:
                    // Tanks should always exist
                    for (GameSprite g : gameSprites) {
                        if (g.getId() == j.getID()) {
                            g.readJSON(j);
                            break;
                        }
                    }
                    break;
                case BARREL:
                    for (GameSprite g : gameSprites) {
                        if (g.getId() == j.getID()) {
                            ((Tank)g).readBarrelJSON(j);
                            break;
                        }
                    }
                    break;
            }
/*
//            System.out.println(j.toString());
            boolean exists = false;
//            System.out.println(j.getID());
//            System.out.println(j);
            for (GameSprite g : gameSprites) {
//                System.out.println(g.getId());
                System.out.println("Found:");
                System.out.println(j);
                System.out.println(j.getID());
                if (j.getID() == g.getId()) { //Update existing
                    g.readJSON(j);
                    exists = true;
                }
            }
            if (!exists) { //Add new
                idCounter.set(j.getID());
                if (j.getType() == SpriteJSON.Type.PROJECTILE) {
                    // TODO: check active ammotype
                    fireFromPool(Projectile.AmmoType.STANDARD, j.getPos(), new Vector2(), false);
                }
            }*/
        }
    }

    public OrthographicCamera getCamera() {
        return cam;
    }

    @Override
    public void update(float dt) {
        // update gui
        gui.update();

        handleInput();
        // doesn't work on desktop
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            readJSON();
        }

        for (GameSprite gs : gameSprites) {
            gs.update();
        }

        // free dead projectiles
        Projectile p;
        for(int i = activeProjectiles.size; --i >= 0;) {
            p = activeProjectiles.get(i);
            if(!p.isAlive()) {
                activeProjectiles.removeIndex(i);
                projectilePool.free(p);
                gameSprites.remove(p);
            }
        }

        // update bg
        bg.update();
    }

    @Override
    public void render(SpriteBatch sb, PolygonSpriteBatch psb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        /*for (Iterator<GameSprite> it = gameSprites.iterator(); it.hasNext();) {
            it.next().draw(sb);
        }*/

        // draw bg
        bg.draw(sb);
        for (int i = 0; i < gameSprites.size(); i++) {
            GameSprite sprite = gameSprites.get(i);
            if(sprite instanceof  Tank){
                if (((Tank) sprite).getHealth() <= 0){
                    gui.setPlayable(false);
                    boolean won = false;
                    if(sprite.getId() == -2){
                        won = true;
                    }
                    gui.endSplash(won);
                }
            }
            sprite.draw(sb);
        }

        // update and draw effects
        for (int i = effects.size - 1; i >= 0; i--) {
            ParticleEffectPool.PooledEffect effect = effects.get(i);
            effect.draw(sb, Gdx.graphics.getDeltaTime());
            if (effect.isComplete()) {
                effect.free();
                effects.removeIndex(i);
            }
        }

        sb.end();

        // ground terrain
        psb.setProjectionMatrix(cam.combined);
        psb.begin();
        ground.draw(psb);
        psb.end();

        // find another way to do this maybe, gives more depth to terrain
        sb.begin();
        shadow.draw(sb);
        sb.end();

        gui.draw(sb);

        //stage for buttons
        //stage.act(Gdx.graphics.getDeltaTime());
        //stage.draw();

        // box-2d
        debugRenderer.render(world, cam.combined);
        world.step(1/60f, 6, 2);
    }

    public int getSeed() {
        return seed;
    }

    public void setSeed(int seed) {
        this.seed = seed;
    }

    public static int getNewSeed() {
        return MathUtils.random(1000);
    }

    @Override
    public void dispose() {
        bg.dispose();
        world.dispose();
        gui.dispose();
        debugRenderer.dispose();
        explosionSound.dispose();
        /*for (GameSprite gs : gameSprites) {
            gs.dispose();
        }*/
        for (Iterator<GameSprite> it = gameSprites.iterator(); it.hasNext();) {
            it.next().dispose();
        }
    }

    public static ArrayList<GameSprite> getGameSprites() {
        return gameSprites;
    }
}

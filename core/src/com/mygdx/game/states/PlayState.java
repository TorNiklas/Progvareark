package com.mygdx.game.states;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
import com.mygdx.game.network.SpriteSerialize;
import com.mygdx.game.sprites.GUI;
import com.mygdx.game.sprites.GameSprite;
import com.mygdx.game.sprites.Ground;
import com.mygdx.game.sprites.Projectile;
import com.mygdx.game.sprites.Tank;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.cos;
import static java.lang.StrictMath.sin;

public class PlayState extends State {
    private Texture bg;
    private static ArrayList<GameSprite> gameSprites;
    private static World world;
    private Box2DDebugRenderer debugRenderer;
    private Ground ground;
    private BTInterface btInterface;
    private GUI gui;




    // active projectiles
    private final Array<Projectile> activeProjectiles = new Array<Projectile>();

    // projectile pool
    private final Pool<Projectile> projectilePool = new Pool<Projectile>() {
        @Override
        protected Projectile newObject() {
            return new Projectile();
        }
    };


    public PlayState(int level) {
        super();
        cam.setToOrtho(false, TankGame.WIDTH, TankGame.HEIGHT);
        bg = new Texture("bg.png");

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
                if(bodyB.isBullet() && bodyA.getType() == BodyDef.BodyType.StaticBody ){
                    //System.out.println("Bullet hit ground at" + bodyB.getPosition());
                    //Bullet hit ground, should explode?
                }

                if(bodyB.isBullet() && bodyA == (gameSprites.get(1)).getBody() && (gameSprites.get(1)) instanceof Tank){
                    System.out.println("tank hit!" + bodyB.getPosition());
                    System.out.println("Tank energy: " + ((Tank)gameSprites.get(1)) .getEnergy());

                    //Just for testing
                    //healthBar.setValue(healthBar.getValue() - 10f);
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
        long seed = MathUtils.random(1000);

        // eeh way to do this, but
        switch (level) {
            // forest level
            case 1:
                spawnHeight = 100 + guiHeight;
                ground = new Ground(world, seed,10, 30 + guiHeight, 100 + guiHeight, 10, Color.FOREST);
                break;

            // snow level
            case 2:
                spawnHeight = 200 + guiHeight;
                ground = new Ground(world, seed, 10, 30 + guiHeight, 200 + guiHeight, 10, Color.WHITE);
                break;

            // desert level
            case 3:
                spawnHeight = 70 + guiHeight;
                ground = new Ground(world, seed, 10, 30 + guiHeight, 70 + guiHeight, 10, Color.GOLDENROD);
                break;

            // default to forest
            default:
                ground = new Ground(world, seed, 10, 30 + 250, 100 + guiHeight, 10, Color.FOREST);
        }

        gameSprites.add(new Tank(world, this, 500, spawnHeight));
        //gameSprites.add(new Tank(world, 800, 100));
        // test simple gui
        gui = new GUI((Tank)gameSprites.get(0), cam, guiHeight);

        /*Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                readNetSprites();
            }
        }, 100, 100, TimeUnit.MILLISECONDS);*/
    }

    private ProgressBar generateProgressBar(int x, int y, int width, int height, Color bgColor, Color barColor) {
        // energy background bar pixmap
        Pixmap bgPixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        bgPixmap.setColor(bgColor);
        bgPixmap.fill();

        // full bar pixmap
        Pixmap fullPixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        fullPixmap.setColor(barColor);
        fullPixmap.fill();

        // empty pixmap
        Pixmap emptyPixmap = new Pixmap(0, height, Pixmap.Format.RGBA8888);
        emptyPixmap.setColor(barColor);
        emptyPixmap.fill();

        // texture region drawables
        TextureRegionDrawable bgDrawable = new TextureRegionDrawable(new TextureRegion(new Texture(bgPixmap)));
        TextureRegionDrawable fullDrawable = new TextureRegionDrawable(new TextureRegion(new Texture(fullPixmap)));
        TextureRegionDrawable emptyDrawable = new TextureRegionDrawable(new TextureRegion(new Texture(emptyPixmap)));
        bgPixmap.dispose();
        fullPixmap.dispose();
        emptyPixmap.dispose();

        // set up style
        ProgressBar.ProgressBarStyle progressBarStyle = new ProgressBar.ProgressBarStyle();
        progressBarStyle.background = bgDrawable;
        progressBarStyle.knobBefore = fullDrawable;
        progressBarStyle.knob = emptyDrawable;

        // create energy bar
        ProgressBar progressBar = new ProgressBar(0.0f, 100.0f, 0.1f, false, progressBarStyle);
        progressBar.setValue(100f);
        progressBar.setAnimateDuration(0.05f);
        progressBar.setBounds(x, y, width, height);

        return progressBar;
    }


//    public static void fire(float x, float y) {
//        System.out.println("FIRING " + x + " - " + y);
//        gameSprites.add(((Tank)gameSprites.get(0)).fireProjectile(world, x, y, power));
//    }

    public void fireFromPool(Vector2 pos, Vector2 force) {
        System.out.println("FIRING " + pos.x + " - " + pos.y);
        System.out.println("FORCE " + force.x + " - " + force.y);
        Projectile p = projectilePool.obtain();
        p.init(world, pos, force);
        activeProjectiles.add(p);
        gameSprites.add(p);
    }

    public static World getWorld() {
        return world;
    }

    @Override
    protected void handleInput() {
        if(Gdx.input.justTouched()) {
            //System.out.println("FIRE!");
            //gameSprites.add(((Tank)gameSprites.get(0)).fireProjectile(world, Gdx.input.getX(), Gdx.input.getY()));

            int x = Gdx.input.getX();
            int y = Gdx.input.getY();
            //fire(x,y);
            /*Integer[] send = { x, y };
            TankGame.getBluetooth().writeObject(send);*/
            //TankGame.getBluetooth().writeObject("FIRE");
        }
        // for testing purposes
        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            GameStateManager.getGsm().push(new MenuState(/*gsm*/));
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            ((Tank)gameSprites.get(0)).drive(new Vector2(-50f, -5f));
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            ((Tank)gameSprites.get(0)).drive(new Vector2(50f, -5f));
        }

    }

    public static ArrayList<SpriteSerialize> getNetSprites() {
        ArrayList<SpriteSerialize> spriteSerializes = new ArrayList<SpriteSerialize>();
        for (GameSprite g : gameSprites) {
            if (g.isLocal()) {
                spriteSerializes.add(g.getSerialize());
            }
        }
        return spriteSerializes;
    }

    private void readNetSprites() {
        ArrayList<SpriteSerialize> sprites = TankGame.getBluetooth().getSprites();
        //System.out.println(sprites);
        for (SpriteSerialize s : sprites) {
            boolean exists = false;
            for (GameSprite g : gameSprites) {
                if (s.getId() == g.getId()) {
                    g.readSerialize(s);
                    exists = true;
                }
            }
            if (!exists && s.getType() == SpriteSerialize.Type.PROJECTILE) {
                gameSprites.add(new Projectile(world, s.getId(), s.getPos().x, s.getPos().y, s.getLinVel()));
            }
        }
    }

    @Override
    public void update(float dt) {
        // update gui
        gui.update();

        handleInput();
        // doesn't work on desktop
        if(Gdx.app.getType() == Application.ApplicationType.Android) {
            readNetSprites();
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
    }

    @Override
    public void render(SpriteBatch sb, PolygonSpriteBatch psb) {
		((Tank)gameSprites.get(0)).powerTick();
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        //gui.draw(sb);
        //sb.draw(bg, 0,0, 1280, 720);
        sb.draw(bg, 0,0, 1280, 720);
        /*for (Iterator<GameSprite> it = gameSprites.iterator(); it.hasNext();) {
            it.next().draw(sb);
        }*/

        for (int i = 0; i < gameSprites.size(); i++) {
            gameSprites.get(i).draw(sb);
        }
        sb.end();

        // ground terrain
        psb.setProjectionMatrix(cam.combined);
        psb.begin();
        ground.draw(psb);
        psb.end();

        gui.draw(sb);

        //stage for buttons
        //stage.act(Gdx.graphics.getDeltaTime());
        //stage.draw();
        // box-2d
        debugRenderer.render(world, cam.combined);
        world.step(1/60f, 6, 2);
    }

    @Override
    public void dispose() {
        bg.dispose();
        world.dispose();
        gui.dispose();
        debugRenderer.dispose();
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

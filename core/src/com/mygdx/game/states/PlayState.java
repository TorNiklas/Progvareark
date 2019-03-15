package com.mygdx.game.states;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
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
import com.mygdx.game.TankGame;
import com.mygdx.game.network.SpriteSerialize;
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

    private Drawable drawable;
    private Stage stage;

    private ImageButton leftBtn;
    private ImageButton rightBtn;

    Skin skin;
    private int aimedX;
    private int aimedY;
    private TextButton fireButton;
    private TextButton increaseElevation;
    private TextButton decreaseElevation;
    private int deg = 0;
    private int aimRate = 5;
    private boolean isIncreasing;
    private boolean isDecreasing;

    // active projectiles
    private final Array<Projectile> activeProjectiles = new Array<Projectile>();

    // projectile pool
    private final Pool<Projectile> projectilePool = new Pool<Projectile>() {
        @Override
        protected Projectile newObject() {
            return new Projectile();
        }
    };
    private ProgressBar healthBar;
    private ProgressBar energyBar;

    public PlayState(int level) {
        super();
        cam.setToOrtho(false, TankGame.WIDTH, TankGame.HEIGHT);
        bg = new Texture("bg.png");

        Texture buttons = new Texture("buttons.png");
        Drawable rightBtnDrawable = new TextureRegionDrawable(new TextureRegion(buttons, 100,0,100,100));
        Drawable leftBtnDrawable = new TextureRegionDrawable(new TextureRegion(buttons, 0,0,100,100));
        leftBtn = new ImageButton(leftBtnDrawable);
        rightBtn = new ImageButton(rightBtnDrawable);
        rightBtn.setPosition(150, 0);

        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        fireButton = new TextButton("Fire!", skin);
        fireButton.setSize(200,100);
        fireButton.setPosition(0, 300);

        increaseElevation = new TextButton("+", skin);
        increaseElevation.setSize(100,100);
        increaseElevation.setPosition(100, 200);

        decreaseElevation = new TextButton("-", skin);
        decreaseElevation.setSize(100,100);
        decreaseElevation.setPosition(0, 200);

        // create energy bar
        energyBar = generateProgressBar(300, 20, 100, 20, Color.DARK_GRAY, Color.GOLD);

        // create health bar
        healthBar = generateProgressBar(450, 20, 100, 20, Color.FIREBRICK, Color.GREEN);
        healthBar.setValue(75f);

        stage = new Stage(new StretchViewport(1280, 720, cam));
        stage.addActor(leftBtn);
        stage.addActor(rightBtn);
        stage.addActor(fireButton);
        stage.addActor(increaseElevation);
        stage.addActor(decreaseElevation);
        stage.addActor(energyBar);
        stage.addActor(healthBar);

        Gdx.input.setInputProcessor(stage);

        //Button event handlers, should probably not be here
        leftBtn.addListener(new ClickListener() {
            /*@Override
            public boolean handle(Event event) {
                System.out.println("Pressed left button");
                //((Tank)gameSprites.get(0)).drive(new Vector2(-50f, -5f));
                return true;
            }*/

            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("touch down - left");
                ((Tank)gameSprites.get(0)).setMoveLeft(true);
                return true;
            }

            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("touch up - left");
                ((Tank)gameSprites.get(0)).setMoveLeft(false);
            }

        });

        rightBtn.addListener(new ClickListener() {
            /*@Override
            public boolean handle(Event event) {
                System.out.println("Pressed right button");
                ((Tank)gameSprites.get(0)).drive(new Vector2(50f, -5f));
                return true;
            }*/

            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("touch down - right");
                ((Tank)gameSprites.get(0)).setMoveRight(true);
                return true;
            }

            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("touch up - right");
                ((Tank)gameSprites.get(0)).setMoveRight(false);
            }
        });

        fireButton.addListener(new ClickListener() {
            public void clicked(InputEvent e, float x, float y) {
                System.out.println("Firebutton has been pressed!");

                float vectorY = (float)sin(Math.toRadians(deg));
                float vectorX = (float)cos(Math.toRadians(deg));
                //fire(vectorX,vectorY);

                // use object pooling
                // start pos
                Vector2 pos = ((Tank)gameSprites.get(0)).getBarrelPosition();

                // exit velocity
                Vector2 velocity = new Vector2(vectorX * 1000f, vectorY * 1000f);
                fireFromPool(pos, velocity);

                // Integer[] send = { x, y };
                // TankGame.getBluetooth().writeObject(send);
            }
        });

        increaseElevation.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                isIncreasing = true;
                return true;
            }
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                isIncreasing = false;
            }
        });

        decreaseElevation.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                isDecreasing = true;
                return true;
            }
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                isDecreasing = false;
            }
        });

        // init box2d world
        Box2D.init();
        world = new World(new Vector2(0, -50f), true);
        debugRenderer = new Box2DDebugRenderer();

        // Hit detection listener
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();
                if(fixtureB.getBody().isBullet() && fixtureA.getBody().getType() == BodyDef.BodyType.StaticBody ){
                    System.out.println("Bullet hit ground at" + fixtureB.getBody().getPosition());
                }
                if(fixtureB.getBody().isBullet() && fixtureA.getBody() == (gameSprites.get(1)).getBody()){
                    System.out.println("tank hit!" + fixtureB.getBody().getPosition());
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
        int spawnHeight = 100;

        // send this to client
        long seed = MathUtils.random(1000);

        // eeh way to do this, but
        switch (level) {
            // forest level
            case 1:
                spawnHeight = 100;
                ground = new Ground(world, seed,10, 30, 100, 10, Color.FOREST);
                break;

            // snow level
            case 2:
                spawnHeight = 200;
                ground = new Ground(world, seed, 10, 30, 200, 10, Color.WHITE);
                break;

            // desert level
            case 3:
                spawnHeight = 70;
                ground = new Ground(world, seed, 10, 30, 70, 10, Color.GOLDENROD);
                break;

            // default to forest
            default:
                ground = new Ground(world, seed, 10, 30, 100, 10, Color.FOREST);
        }

        gameSprites.add(new Tank(world, 500, spawnHeight));
        //gameSprites.add(new Tank(world, 800, 100));

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


    public static void fire(float x, float y) {
        System.out.println("FIRING " + x + " - " + y);
        gameSprites.add(((Tank)gameSprites.get(0)).fireProjectile(world, x, y));
    }

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
        // update energy
        energyBar.setValue(((Tank)gameSprites.get(0)).getEnergy());

        handleInput();
        // doesn't work on desktop
        if(Gdx.app.getType() == Application.ApplicationType.Android) {
            readNetSprites();
        }
        for (GameSprite gs : gameSprites) {
            gs.update();
        }

        if(isIncreasing || isDecreasing) {
            if (isIncreasing) {
                deg -= aimRate;
            } else {
                deg += aimRate;
            }
            ((Tank) gameSprites.get(0)).updateBarrel(deg);
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
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
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

        //stage for buttons
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        // box-2d
        debugRenderer.render(world, cam.combined);
        world.step(1/60f, 6, 2);
    }

    @Override
    public void dispose() {
        bg.dispose();
        world.dispose();
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

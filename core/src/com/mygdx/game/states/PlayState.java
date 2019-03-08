package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
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

public class PlayState extends State {
    private Texture bg;
    private static ArrayList<GameSprite> gameSprites;
    private static World world;
    private Box2DDebugRenderer debugRenderer;
    private Ground ground;

    private Stage stage;
    private ImageButton leftBtn;
    private ImageButton rightBtn;

    private ImageButton button;
    private TextButton fireButton;
    Skin skin;
    private int aimedX;
    private int aimedY;

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

        // TODO: fix me
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        fireButton = new TextButton("Fire!", skin);
        fireButton.setSize(Gdx.graphics.getWidth() / 20,Gdx.graphics.getHeight() / 10);
        fireButton.setPosition(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()-200);

        stage = new Stage(new ScreenViewport());
        stage.addActor(leftBtn);
        stage.addActor(rightBtn);
        stage.addActor(button);
        stage.addActor(fireButton);


        Gdx.input.setInputProcessor(stage);

        //Button event handlers, should probably not be here
        leftBtn.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                System.out.println("Pressed left button");
                ((Tank)gameSprites.get(0)).drive(new Vector2(-50f, -5f));
                return true;
            }
        });

        rightBtn.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                System.out.println("Pressed right button");
                ((Tank)gameSprites.get(0)).drive(new Vector2(50f, -5f));
                return true;
            }
        });

        fireButton.addListener(new ClickListener() {
            public void clicked(InputEvent e, float x, float y) {
                System.out.println("Firebutton has been pressed!");
                fire(aimedX,aimedY);
                // Integer[] send = { x, y };
                // TankGame.getBluetooth().writeObject(send);
            }
        });

        // init box2d world
        Box2D.init();
        world = new World(new Vector2(0, -50f), true);
        debugRenderer = new Box2DDebugRenderer();

        gameSprites = new ArrayList<GameSprite>();
        int spawnHeight = 100;

        // eeh way to do this, but
        switch (level) {
            // forest level
            case 1:
                spawnHeight = 100;
                ground = new Ground(world, 10, 30, 100, 10, Color.FOREST);
                break;

            // snow level
            case 2:
                spawnHeight = 200;
                ground = new Ground(world, 10, 30, 200, 10, Color.WHITE);
                break;

            // desert level
            case 3:
                spawnHeight = 70;
                ground = new Ground(world, 10, 30, 70, 10, Color.GOLDENROD);
                break;

            // default to forest
            default:
                ground = new Ground(world, 10, 30, 100, 10, Color.FOREST);
        }

        gameSprites.add(new Tank(world, 500, spawnHeight));


        /*Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                readNetSprites();
            }
        }, 100, 100, TimeUnit.MILLISECONDS);*/
    }

    public static void fire(int x, int y) {
        //System.out.println("FIRING " + x + "-" + y);
        gameSprites.add(((Tank)gameSprites.get(0)).fireProjectile(world, x, y));
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
            fire(x,y);
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
        handleInput();
        readNetSprites();
        for (GameSprite gs : gameSprites) {
            gs.update();
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
        psb.begin();
        ground.draw(psb);
        psb.end();

        //stage for buttons
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        // box-2d
        //debugRenderer.render(world, cam.combined);
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

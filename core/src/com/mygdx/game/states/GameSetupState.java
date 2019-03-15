package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.game.TankGame;

import java.util.Random;

public class GameSetupState extends State {
    private Texture bg;
    private Image forestMapBtn;
    private Image snowMapBtn;
    private Image desertMapBtn;
    private Image backBtn;
    private BitmapFont gameCode;
    private String gameCodeString;
    private Stage stage;

    private static PlayState selectedState;

    private boolean connected = false;

    private Runnable onConnect = new Runnable() {
        @Override
        public void run() {
            System.out.println("Connected!");
            //while (selectedState == null) {}
            connected = true;
            if (selectedState != null) {
                GameStateManager.getGsm().set(selectedState);
            }
        }
    };

    private Runnable onDisconnect = new Runnable() {
        @Override
        public void run() {
            System.out.println("Disconnected :(");
        }
    };

    public GameSetupState() {
        super();
        cam.setToOrtho(false, TankGame.WIDTH, TankGame.HEIGHT);
        bg = new Texture("bg.png");

        // init game code text
        gameCodeString = generateGameCode();
        final int seed = PlayState.getNewSeed();

        TankGame.getBluetooth().startHostConnection(gameCodeString, onConnect, onDisconnect);


        // TODO: fix better scaling without pixelating the text
        //gameCode.getData().setScale(3f, 3f);

        // init buttons
        forestMapBtn = new Image(new Texture("forest_level.png"));
        snowMapBtn = new Image(new Texture("snow_level.png"));
        desertMapBtn = new Image(new Texture("desert_level.png"));
        backBtn = new Image(new Texture("back.png"));

        // set pos and size
        float center = TankGame.WIDTH/2 - snowMapBtn.getWidth()/2;
        forestMapBtn.setPosition(center - 250, 175);
        snowMapBtn.setPosition(center, 175);
        desertMapBtn.setPosition(center + 250, 175);
        backBtn.setPosition(center, 50);

        // create stage and add maps as actors
        stage = new Stage(new StretchViewport(TankGame.WIDTH, TankGame.HEIGHT, cam));
        stage.addActor(forestMapBtn);
        stage.addActor(snowMapBtn);
        stage.addActor(desertMapBtn);
        stage.addActor(backBtn);

        Gdx.input.setInputProcessor(stage);
        // event handlers, should probably not be here
        forestMapBtn.addListener(new InputListener() {
                public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                    System.out.println("forest selected");
                    //GameStateManager.getGsm().set(new PlayState(1));
                    TankGame.getBluetooth().startHostGame(1, seed);
                    selectedState = new PlayState(1, seed);
                    if (connected) {
                        GameStateManager.getGsm().set(selectedState);
                    }
                    return true;
            }
        });

        snowMapBtn.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("snow selected");
                //GameStateManager.getGsm().set(new PlayState(2));
                TankGame.getBluetooth().startHostGame(2, seed);
                selectedState = new PlayState(2, seed);
                if (connected) {
                    GameStateManager.getGsm().set(selectedState);
                }
                return true;
            }
        });

        desertMapBtn.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("desert selected");
                //GameStateManager.getGsm().set(new PlayState(3));
                TankGame.getBluetooth().startHostGame(3, seed);
                selectedState = new PlayState(3, seed);
                if (connected) {
                    GameStateManager.getGsm().set(selectedState);
                }
                return true;
            }
        });

        backBtn.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("back selected");
                GameStateManager.getGsm().set(new MenuState());
                return true;
            }
        });

        // Setup font generator
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/arial.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 30;
        parameter.shadowColor = Color.BLACK;
        parameter.shadowOffsetX = 3;
        parameter.shadowOffsetY = 3;
        gameCode = generator.generateFont(parameter);
        generator.dispose();

    }

    /*public static void setSelectedState(int level) {
        System.out.println("Selected level: " + level);
        selectedState = new PlayState(level);
    }*/

    public static PlayState getSelectedState() {
        return selectedState;
    }

    private String generateGameCode() {
        return String.format("%04d", new Random().nextInt(10000));
    }

    @Override
    public void handleInput() {

    }

    public static void onDisconnect() {
        System.out.println("Disconnected!");
    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb, PolygonSpriteBatch psb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();

        // background
        sb.draw(bg, 0,0, 1280, 720);

        // game code
        gameCode.draw(sb, "Game Code: " + gameCodeString, TankGame.WIDTH/2 - gameCode.getRegion().getRegionWidth()/8, 500);

        sb.end();

        // draw stage actors
        stage.act();
        stage.draw();
    }

    @Override
    public void dispose() {
        bg.dispose();
    }
}

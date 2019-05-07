package com.mygdx.game.states;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.game.AssetHandler;
import com.mygdx.game.TankGame;

import java.util.Random;

public class GameSetupState extends State {
    private Image bg;
    private Image forestMapBtn;
    private Image snowMapBtn;
    private Image desertMapBtn;
    private Image backBtn;
    private BitmapFont gameCode;
    private String gameCodeString;
    private Stage stage;
    private Map selectedMap;
    private enum Map {
        FOREST,
        SNOW,
        DESERT
    }

    private AssetHandler assetHandler;

    private static PlayState selectedState;

    private boolean connected = false;

    private Runnable onConnect = new Runnable() {
        @Override
        public void run() {
            System.out.println("Connected!");
            System.out.println(GameSetupState.this);
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
            connected = false;
            System.out.println("Disconnected :(");
            selectedState = null;
        }
    };

    public GameSetupState() {
        super();

        // set asset handler
        assetHandler = ((TankGame)Gdx.app.getApplicationListener()).assetHandler;

        bg = new Image((Texture) assetHandler.manager.get(assetHandler.bgPath));

        // init game code text
        gameCodeString = generateGameCode();
        final int seed = PlayState.getNewSeed();

        TankGame.getBluetooth().startHostConnection(gameCodeString, onConnect, onDisconnect);

        // init buttons
        forestMapBtn = new Image((Texture) assetHandler.manager.get(assetHandler.forestLevelPath));
        snowMapBtn = new Image((Texture) assetHandler.manager.get(assetHandler.snowLevelPath));
        desertMapBtn = new Image((Texture) assetHandler.manager.get(assetHandler.desertLevelPath));
        backBtn = new Image((Texture) assetHandler.manager.get(assetHandler.backPath));

        // set pos and size
        float center = TankGame.WIDTH/2 - snowMapBtn.getWidth()/2;
        forestMapBtn.setPosition(center - 250, 175);
        snowMapBtn.setPosition(center, 175);
        desertMapBtn.setPosition(center + 250, 175);
        backBtn.setPosition(center, 50);

        gameCode = assetHandler.manager.get(assetHandler.fontPath);

        // create stage and add maps as actors
        stage = new Stage(new StretchViewport(TankGame.WIDTH, TankGame.HEIGHT, cam));
        stage.addActor(bg);
        stage.addActor(forestMapBtn);
        stage.addActor(snowMapBtn);
        stage.addActor(desertMapBtn);
        stage.addActor(backBtn);

        Gdx.input.setInputProcessor(stage);
        // event handlers, should probably not be here
        forestMapBtn.addListener(new InputListener() {
                public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                    System.out.println("forest selected");
                    System.out.println(GameSetupState.this);

                    // set selected
                    if(selectedMap != Map.FOREST) {
                        setSelectedMap(Map.FOREST);
                    }

                    // desktop, for testing
                    if(Gdx.app.getType() == Application.ApplicationType.Desktop) {
                        GameStateManager.getGsm().set(new PlayState(1, seed));
                        return true;
                    }
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
                System.out.println(GameSetupState.this);


                // set selected
                if(selectedMap != Map.SNOW) {
                    setSelectedMap(Map.SNOW);
                }

                // desktop, for testing
                if(Gdx.app.getType() == Application.ApplicationType.Desktop) {
                    GameStateManager.getGsm().set(new PlayState(2, seed));
                    return true;
                }
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
                System.out.println(GameSetupState.this);


                // set selected
                if(selectedMap != Map.DESERT) {
                    setSelectedMap(Map.DESERT);
                }
                // desktop, for testing
                if(Gdx.app.getType() == Application.ApplicationType.Desktop) {
                    GameStateManager.getGsm().set(new PlayState(3, seed));
                    return true;
                }
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

    public void setSelectedMap(Map map) {
        selectedMap = map;
        switch (map) {
            case FOREST:
                forestMapBtn.setColor(Color.DARK_GRAY);
                snowMapBtn.setColor(Color.WHITE);
                desertMapBtn.setColor(Color.WHITE);
                break;

            case SNOW:
                snowMapBtn.setColor(Color.DARK_GRAY);
                forestMapBtn.setColor(Color.WHITE);
                desertMapBtn.setColor(Color.WHITE);
                break;

            case DESERT:
                desertMapBtn.setColor(Color.DARK_GRAY);
                forestMapBtn.setColor(Color.WHITE);
                snowMapBtn.setColor(Color.WHITE);
                break;
        }
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
        // draw stage actors
        stage.act();
        stage.draw();

        sb.setProjectionMatrix(cam.combined);
        sb.begin();

        // background
        //sb.draw(bg, 0,0, 1280, 720);

        // game code
        gameCode.draw(sb, "Game Code: " + gameCodeString, TankGame.WIDTH/2, 500, 0f, 1, false);


        sb.end();
    }

    @Override
    public void dispose() {
        //bg.dispose();
    }

    @Override
    public void onLoad() {
        selectedMap = null;
        selectedState = null;
        connected = false;
    }

    @Override
    public String toString() {
        return "GameSetupState{" +
                "stage=" + stage +
                ", selectedMap=" + selectedMap +
                ", selectedState=" + selectedState +
                ", connected=" + connected +
                '}';
    }
}

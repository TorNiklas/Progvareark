package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.game.TankGame;

import javax.swing.text.View;

public class MenuState extends State{
    private Texture bg;
    private Image hostBtn;
    private Image connectBtn;
    private Image optionBtn;
    private Stage stage;

    private Runnable onConnected = new Runnable() {
        @Override
        public void run() {
            System.out.println("Connected!");
        }
    };

    private Runnable onDisconnect = new Runnable() {
        @Override
        public void run() {
            System.out.println("Disconnected");
        }
    };

    public MenuState() {
        super();
        cam.setToOrtho(false, TankGame.WIDTH, TankGame.HEIGHT);
        bg = new Texture("bg.png");

        hostBtn = new Image(new Texture("host.png"));
        connectBtn = new Image(new Texture("connect.png"));
        optionBtn = new Image(new Texture("options.png"));

        // set pos and size
        float center = TankGame.WIDTH/2 - hostBtn.getWidth()/2;
        hostBtn.setPosition(center, 400);
        connectBtn.setPosition(center, 300);
        optionBtn.setPosition(center, 200);

        // create stage and add maps as actors
        stage = new Stage(new StretchViewport(TankGame.WIDTH, TankGame.HEIGHT, cam));
        stage.addActor(hostBtn);
        stage.addActor(connectBtn);
        stage.addActor(optionBtn);

        // move l8r
        Gdx.input.setInputProcessor(stage);
        // event handlers, should probably not be here
        hostBtn.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("host selected");
                GameStateManager.getGsm().set(new GameSetupState());
                return true;
            }
        });

        connectBtn.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("connect selected");
                //GameStateManager.getGsm().set(new ConnectState());
                Input.TextInputListener textInputListener = new Input.TextInputListener() {
                    @Override
                    public void input(String text) {
                        // handle input
                        System.out.println(text);
                        TankGame.getBluetooth().startClientConnection(text, onConnected, onDisconnect);

                    }

                    @Override
                    public void canceled() {
                        // handle cancel
                        System.out.println("canceled");
                    }
                };

                Gdx.input.getTextInput(textInputListener, "Enter game code", "", "Enter a valid game code");
                return true;
            }
        });

        optionBtn.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("option selected");
                GameStateManager.getGsm().set(new OptionState(true));
                return true;
            }
        });

    }

    @Override
    public void handleInput() {
        /*if(Gdx.input.justTouched()){
            //TankGame.getBluetooth().startHost();
            if (Gdx.input.getX() > Gdx.graphics.getWidth() / 2 && Gdx.input.getY() > Gdx.graphics.getHeight() / 2) {
                System.out.println("HOST");
                TankGame.getBluetooth().startHost();
            }
            else if (Gdx.input.getX() < Gdx.graphics.getWidth() / 2 && Gdx.input.getY() > Gdx.graphics.getHeight() / 2) {
                System.out.println("CLIENT");
                TankGame.getBluetooth().startClient();
            }
            else {

                GameStateManager.getGsm().set(new GameSetupState(*//*gsm*//*));
            }
            // go to game setup always, for testing
            GameStateManager.getGsm().set(new GameSetupState(*//*gsm*//*));
        }*/
    }

    @Override
    public void update(float dt) {
        //handleInput();
    }

    @Override
    public void render(SpriteBatch sb, PolygonSpriteBatch psb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();


        // background
        sb.draw(bg, 0,0, 1280, 720);

        // menu buttons
        hostBtn.draw(sb, 1f);
        connectBtn.draw(sb, 1f);
        optionBtn.draw(sb, 1f);
        sb.end();
    }

    @Override
    public void dispose() {
        bg.dispose();
    }
}

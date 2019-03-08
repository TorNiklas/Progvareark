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
import com.mygdx.game.TankGame;

public class MenuState extends State {
    private Texture bg;
    private Texture playBtn;
    private Image hostBtn;
    private Image connectBtn;
    private Stage stage;

    public MenuState(/*GameStateManager gsm*/) {
        super(/*gsm*/);
        cam.setToOrtho(false, TankGame.WIDTH, TankGame.HEIGHT);
        bg = new Texture("bg.png");
        //playBtn = new Texture("play.png");
        hostBtn = new Image(new Texture("host.png"));
        connectBtn = new Image(new Texture("connect.png"));

        // set pos and size
        float center = TankGame.WIDTH/2 - hostBtn.getWidth()/2;
        hostBtn.setPosition(center, 400);
        connectBtn.setPosition(center, 300);

        // create stage and add maps as actors
        stage = new Stage(new ScreenViewport());
        stage.addActor(hostBtn);
        stage.addActor(connectBtn);

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
                        System.out.println(text);
                    }

                    @Override
                    public void canceled() {
                        System.out.println("canceled");
                    }
                };

                Gdx.input.getTextInput(textInputListener, "Enter game code", "", "Enter a valid game code");
                return true;
            }
        });

    }

    @Override
    public void handleInput() {
        if(Gdx.input.justTouched()){
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
                GameStateManager.getGsm().set(new GameSetupState(/*gsm*/));
            }
            // go to game setup always, for testing
            GameStateManager.getGsm().set(new GameSetupState(/*gsm*/));
        }
    }

    public static void onConnected(boolean isHost) {
        System.out.println("Connected!");
        //GameStateManager.getGsm().set(new PlayState(/*gsm*/));
    }

    public static void onDisconnect() {
        System.out.println("Disconnected!");
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

        sb.end();
    }

    @Override
    public void dispose() {
        bg.dispose();
    }
}

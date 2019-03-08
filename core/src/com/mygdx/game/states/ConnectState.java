package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.TankGame;

public class ConnectState extends State {
    private Texture bg;
    private Image connectBtn;
    private TextField textField;
    private Stage stage;

    public ConnectState(/*GameStateManager gsm*/) {
        super(/*gsm*/);
        cam.setToOrtho(false, TankGame.WIDTH, TankGame.HEIGHT);
        bg = new Texture("bg.png");

        // init button and text field
        connectBtn = new Image(new Texture("connect.png"));


        // set pos and size
        float center = TankGame.WIDTH/2 - connectBtn.getWidth()/2;
        connectBtn.setPosition(center, 300);

        // create stage and add maps as actors
        stage = new Stage(new ScreenViewport());
        stage.addActor(connectBtn);

        Gdx.input.setInputProcessor(stage);
        // event handlers, should probably not be here
        connectBtn.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("connect");

                return true;
            }
        });
    }

    @Override
    public void handleInput() {

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
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb, PolygonSpriteBatch psb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();

        // background
        sb.draw(bg, 0,0, 1280, 720);

        // button
        connectBtn.draw(sb, 1f);

        sb.end();
    }

    @Override
    public void dispose() {
        bg.dispose();
    }
}

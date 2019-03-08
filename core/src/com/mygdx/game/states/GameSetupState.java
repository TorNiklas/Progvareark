package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.TankGame;

public class GameSetupState extends State {
    private Texture bg;
    private Texture playBtn;

    public GameSetupState(/*GameStateManager gsm*/) {
        super(/*gsm*/);
        cam.setToOrtho(false, TankGame.WIDTH, TankGame.HEIGHT);
        bg = new Texture("bg.png");
        playBtn = new Texture("play.png");
    }

    @Override
    public void handleInput() {
        
    }

    public static void onConnected(boolean isHost) {
        System.out.println("Connected!");
        GameStateManager.getGsm().set(new PlayState(/*gsm*/));
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
        sb.draw(bg, 0,0, 1280, 720);
        sb.draw(playBtn, cam.position.x - playBtn.getWidth()/2, cam.position.y - playBtn.getHeight()/2);
        sb.end();
    }

    @Override
    public void dispose() {
        bg.dispose();
        playBtn.dispose();
    }
}

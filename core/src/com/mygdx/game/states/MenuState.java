package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.TankGame;

import javax.swing.text.View;

public class MenuState extends State{
    private Texture bg;
    private Texture playBtn;
    private Texture optionBtn;

    public MenuState(/*GameStateManager gsm*/) {
        super(/*gsm*/);
        cam.setToOrtho(false, TankGame.WIDTH, TankGame.HEIGHT);
        bg = new Texture("bg.png");
        playBtn = new Texture("play.png");
        optionBtn = new Texture("option.png");
        System.out.println("hei");
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
                //GameStateManager.getGsm().set(new PlayState(/*gsm*/));
            }
            GameStateManager.getGsm().set(new OptionState(/*gsm*/));

        }
    }

    public static void onConnected(boolean isHost) {
        System.out.println("Connected!");
        //GameStateManager.getGsm().set(new PlayState(/*gsm*/));
        //GameStateManager.getGsm().set(new OptionState(/*gsm*/));
    }

    public static void onDisconnect() {
        System.out.println("Disconnected!");
    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        //sb.draw(bg, 0,0, 1280, 720);
        sb.draw(playBtn, cam.position.x - playBtn.getWidth()/2, cam.position.y - playBtn.getHeight()/2);
        sb.draw(optionBtn, cam.position.x+100, cam.position.y);
        sb.end();
    }

    @Override
    public void dispose() {
        bg.dispose();
        playBtn.dispose();
        optionBtn.dispose();
    }
}

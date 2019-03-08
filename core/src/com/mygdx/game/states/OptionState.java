package com.mygdx.game.states;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.TankGame;


public class OptionState extends State {
    private Texture volumeBar;
    private Texture volumeBall;

    public OptionState(GameStateManager gsm) {
        super(gsm);
        cam.setToOrtho(false, TankGame.WIDTH, TankGame.HEIGHT);
        volumeBar = new Texture("volumeBar.png");
        volumeBall = new Texture("volumeBall.png");
    }

    @Override
    protected void handleInput() {

    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.end();
    }


    @Override
    public void dispose() {

    }
}

package com.mygdx.game.states;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.TankGame;

public abstract class State {
    protected OrthographicCamera cam;
    protected Vector3 mouse;
    //protected static GameStateManager gsm;

    public State(/*GameStateManager gsm*/){
        //this.gsm = gsm;
        cam = new OrthographicCamera();
        cam.setToOrtho(false, TankGame.WIDTH, TankGame.HEIGHT);
        mouse = new Vector3();
    }

    protected abstract void handleInput();
    public abstract void update(float dt);
    public abstract void render(SpriteBatch sb, PolygonSpriteBatch psb);
    public abstract void dispose();
    public abstract void onLoad();
}

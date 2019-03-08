package com.mygdx.game.states;

import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.Stack;

public class GameStateManager {
    private Stack<State> states;

    private static GameStateManager gsm = new GameStateManager();

    private GameStateManager(){
        states = new Stack<State>();
    }

    //SINGLETON
    public static GameStateManager getGsm() {
        return gsm;
    }

    public void push(State state){
        states.push(state);
    }

    public void pop(){
        states.pop().dispose();
    }

    public void set(State state){
        states.pop().dispose();
        states.push(state);
    }

    public void update(float dt){
        states.peek().update(dt);
    }

    public void render(SpriteBatch sb, PolygonSpriteBatch psb){
        states.peek().render(sb, psb);
    }
}

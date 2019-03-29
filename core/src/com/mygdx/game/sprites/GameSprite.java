package com.mygdx.game.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.network.SpriteJSON;


public abstract class GameSprite {
    Body body;
    int id;
    boolean local;

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public abstract void update();
    public abstract void draw(SpriteBatch batch);
    public abstract void dispose();
    public abstract Vector2 getPosition();
    public abstract Sprite getSprite();
    /*SpriteSerialize getSerialize();
    void readSerialize(SpriteSerialize sprite);*/

    public abstract SpriteJSON getJSON();

    public abstract void readJSON(SpriteJSON obj);

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Body getBody() {
        return body;
    }
}

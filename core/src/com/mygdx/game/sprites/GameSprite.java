package com.mygdx.game.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.network.SpriteSerialize;

public interface GameSprite {
    boolean isLocal();

    void update();
    void draw(SpriteBatch batch);
    void dispose();
    Vector2 getPosition();
    Sprite getSprite();
    SpriteSerialize getSerialize();
    void readSerialize(SpriteSerialize sprite);
    int getId();
    Body getBody();
}

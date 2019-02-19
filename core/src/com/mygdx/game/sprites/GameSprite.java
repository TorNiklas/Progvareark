package com.mygdx.game.sprites;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface GameSprite {
    void update();
    void draw(SpriteBatch batch);
    void dispose();
}

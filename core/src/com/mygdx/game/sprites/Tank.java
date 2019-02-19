package com.mygdx.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.TankGame;

public class Tank implements GameSprite {
    private Vector3 position;
    private Rectangle bounds;
    private Texture texture;

    public Tank() {
        texture = new Texture("tank.png");
        position = new Vector3(TankGame.WIDTH/2 - texture.getWidth()/2, TankGame.HEIGHT/2 - texture.getHeight()/2, 0);
        bounds = new Rectangle(TankGame.WIDTH/2 - texture.getWidth()/2, TankGame.HEIGHT/2 - texture.getHeight()/2, texture.getWidth(), texture.getHeight());
    }

    public void update(float dt){

    }

    public Vector3 getPosition() {
        return position;
    }

    public Texture getTexture() {
        return texture;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    @Override
    public void dispose(){
        texture.dispose();
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y);
    }
}

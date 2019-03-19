package com.mygdx.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.TankGame;

import java.util.Random;

public class Background {
    int width;
    int height;
    int x;
    int y;
    float scrollSpeed;
    private Array<Sprite> staticLayers;
    private Array<Sprite> dynamicLayers;

    public Background(int width, int height, int x, int y, float scrollSpeed) {
        // init input parameters
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.scrollSpeed = scrollSpeed;

        // init static and dynamic layer arrays
        staticLayers = new Array<Sprite>();
        dynamicLayers = new Array<Sprite>();

        // load static layers
        Sprite sky = new Sprite(new Texture("backgrounds/bg1/sky.png"));
        Sprite rocks1 = new Sprite(new Texture("backgrounds/bg1/rocks_1.png"));
        Sprite rocks2 = new Sprite(new Texture("backgrounds/bg1/rocks_2.png"));

        // load dynamic layers
        Sprite clouds1_1 = new Sprite(new Texture("backgrounds/bg1/clouds_1.png"));
        Sprite clouds1_2 = new Sprite(new Texture("backgrounds/bg1/clouds_1.png"));
        Sprite clouds2_1 = new Sprite(new Texture("backgrounds/bg1/clouds_2.png"));
        Sprite clouds2_2 = new Sprite(new Texture("backgrounds/bg1/clouds_2.png"));
        Sprite clouds3_1 = new Sprite(new Texture("backgrounds/bg1/clouds_3.png"));
        Sprite clouds3_2 = new Sprite(new Texture("backgrounds/bg1/clouds_3.png"));
        Sprite clouds4_1 = new Sprite(new Texture("backgrounds/bg1/clouds_4.png"));
        Sprite clouds4_2 = new Sprite(new Texture("backgrounds/bg1/clouds_4.png"));

        // add statics to static array
        staticLayers.add(sky);
        staticLayers.add(rocks1);
        staticLayers.add(rocks2);

        // add dynamics to dynamic array
        dynamicLayers.add(clouds1_1);
        dynamicLayers.add(clouds1_2);
        dynamicLayers.add(clouds2_1);
        dynamicLayers.add(clouds2_2);
        dynamicLayers.add(clouds3_1);
        dynamicLayers.add(clouds3_2);
        dynamicLayers.add(clouds4_1);
        dynamicLayers.add(clouds4_2);

        // set pos and size of all layers
        for(Sprite sLayer : staticLayers) {
            sLayer.setPosition(x, y);
            sLayer.setSize(width, height);
        }

        int i = 0;
        for(Sprite dLayer : dynamicLayers) {
            // start every other dynamic image outside, ready to scroll
            if(i % 2 == 0) {
                dLayer.setPosition(x, y);
            } else {
                dLayer.setPosition(-width, y);
            }
            dLayer.setSize(width, height);
            i++;
        }
    }

    public void update() {
        for(Sprite dLayer : dynamicLayers) {
            float xPos = dLayer.getX() + scrollSpeed;
            if(xPos >= width) {
                xPos = -width;
            }
            dLayer.setPosition(xPos, y);
        }
    }

    public void draw(SpriteBatch batch) {
        for(Sprite sLayer : staticLayers) {
            sLayer.draw(batch);
        }

        for(Sprite dLayer : dynamicLayers) {
            dLayer.draw(batch);
        }
    }

    public void dispose() {
        for(Sprite sLayer : staticLayers) {
           sLayer.getTexture().dispose();
        }

        for(Sprite dLayer : dynamicLayers) {
            dLayer.getTexture().dispose();
        }
    }
}

package com.mygdx.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.AssetHandler;
import com.mygdx.game.TankGame;

public class Background {
    private int width;
    private int height;
    private int x;
    private int y;
    private String path;
    private float scrollSpeed;
    private Array<Sprite> staticLayers;
    private Array<Sprite> dynamicLayers;
    private AssetHandler assetHandler;

    public Background(int width, int height, int x, int y, float scrollSpeed, String path) {
        // init input parameters
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.scrollSpeed = scrollSpeed;
        this.path = path;

        assetHandler = ((TankGame) Gdx.app.getApplicationListener()).assetHandler;

        // init static and dynamic layer arrays
        staticLayers = new Array<Sprite>();
        dynamicLayers = new Array<Sprite>();

        // load static layers
        Sprite sky = new Sprite((Texture) assetHandler.manager.get(path + "sky.png"));
        Sprite rocks1 = new Sprite((Texture) assetHandler.manager.get(path + "rocks_1.png"));
        Sprite rocks2 = new Sprite((Texture) assetHandler.manager.get(path + "rocks_2.png"));

        // load dynamic layers
        Sprite clouds1_1 = new Sprite((Texture) assetHandler.manager.get(path + "clouds_1.png"));
        Sprite clouds1_2 = new Sprite((Texture) assetHandler.manager.get(path + "clouds_1.png"));
        Sprite clouds2_1 = new Sprite((Texture) assetHandler.manager.get(path + "clouds_2.png"));
        Sprite clouds2_2 = new Sprite((Texture) assetHandler.manager.get(path + "clouds_2.png"));
        Sprite clouds3_1 = new Sprite((Texture) assetHandler.manager.get(path + "clouds_3.png"));
        Sprite clouds3_2 = new Sprite((Texture) assetHandler.manager.get(path + "clouds_3.png"));

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

    public void dispose() {}
}

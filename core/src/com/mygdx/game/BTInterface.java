package com.mygdx.game;

import com.mygdx.game.network.SpriteSerialize;
import com.mygdx.game.sprites.GameSprite;

import java.io.Serializable;
import java.util.ArrayList;

public interface BTInterface {
    public void startHost();

    public void startClient();

    public void writeObject(Serializable object);

    public void writeSprites(ArrayList<GameSprite> sprites);

    public void disconnect();

    public ArrayList<SpriteSerialize> getSprites();

}

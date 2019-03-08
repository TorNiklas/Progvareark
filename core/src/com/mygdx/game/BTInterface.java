package com.mygdx.game;

import com.mygdx.game.network.SpriteSerialize;
import com.mygdx.game.sprites.GameSprite;

import java.io.Serializable;
import java.util.ArrayList;

public interface BTInterface {
    public void startHost(String code, Runnable onConnected, Runnable onDisconnect);

    public void startClient(String code, Runnable onConnected, Runnable onDisconnect);

    public void writeObject(Serializable object);

    public void disconnect();

    public ArrayList<SpriteSerialize> getSprites();

}

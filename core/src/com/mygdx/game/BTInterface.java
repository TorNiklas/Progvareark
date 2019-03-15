package com.mygdx.game;

import com.mygdx.game.network.SpriteSerialize;
import com.mygdx.game.sprites.GameSprite;

import java.io.Serializable;
import java.util.ArrayList;

public interface BTInterface {
    public void startHostConnection(String code, Runnable onConnected, Runnable onDisconnect);

    public void startHostGame(int level, int seed);

    public void startClientConnection(String code, Runnable onConnected, Runnable onDisconnect);

    //public void startClientGame();

    public void disconnect();

    public ArrayList<SpriteSerialize> getSprites();
}

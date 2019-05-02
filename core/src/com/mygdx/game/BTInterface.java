package com.mygdx.game;

import com.mygdx.game.network.SpriteJSON;
import java.util.Stack;

public interface BTInterface {
    public void startHostConnection(String code, Runnable onConnected, Runnable onDisconnect);

    public void startHostGame(int level, int seed);

    public void startClientConnection(String code, Runnable onConnected, Runnable onDisconnect);

    //public void startClientGame();

    public void disconnect();

    public Stack<SpriteJSON> getSprites();
}

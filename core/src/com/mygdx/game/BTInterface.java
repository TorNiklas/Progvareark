package com.mygdx.game;

import com.mygdx.game.network.SpriteJSON;
import java.util.Stack;

public interface BTInterface {
    void startHostConnection(String code, Runnable onConnected, Runnable onDisconnect);

    void startHostGame(int level, int seed);

    void startClientConnection(String code, Runnable onConnected, Runnable onDisconnect);

    void disconnect();

    void setOnDisconnect(Runnable onDisconnect);

    Stack<SpriteJSON> getSprites();
}

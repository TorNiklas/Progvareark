package com.mygdx.game.desktop;

import com.mygdx.game.BTInterface;
import com.mygdx.game.network.SpriteJSON;

import java.util.ArrayList;
import java.util.Stack;

public class BT implements BTInterface {
    @Override
    public void startHostConnection(String code, Runnable onConnected, Runnable onDisconnect) {

    }

    @Override
    public void startHostGame(int level, int seed) {

    }

    @Override
    public void startClientConnection(String code, Runnable onConnected, Runnable onDisconnect) {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public void setOnDisconnect(Runnable onDisconnect) {

    }

    @Override
    public Stack<SpriteJSON> getSprites() {
        return null;
    }
}

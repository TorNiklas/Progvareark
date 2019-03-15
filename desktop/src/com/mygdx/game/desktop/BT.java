package com.mygdx.game.desktop;

import com.mygdx.game.BTInterface;
import com.mygdx.game.network.SpriteSerialize;
import com.mygdx.game.sprites.GameSprite;
import com.mygdx.game.states.MenuState;

import java.io.Serializable;
import java.util.ArrayList;

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
    public ArrayList<SpriteSerialize> getSprites() {
        return null;
    }
}

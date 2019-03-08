package com.mygdx.game.desktop;

import com.mygdx.game.BTInterface;
import com.mygdx.game.network.SpriteSerialize;
import com.mygdx.game.sprites.GameSprite;
import com.mygdx.game.states.MenuState;

import java.io.Serializable;
import java.util.ArrayList;

public class BT implements BTInterface {
    @Override
    public void startHost() {
        MenuState.onConnected(true);
    }

    @Override
    public void startClient() {
        MenuState.onConnected(false);
    }

    @Override
    public void writeObject(Serializable object) {

    }

    @Override
    public void writeSprites(ArrayList<GameSprite> sprites) {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public ArrayList<SpriteSerialize> getSprites() {
        return null;
    }
}

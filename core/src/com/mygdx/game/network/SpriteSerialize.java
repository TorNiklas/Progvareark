package com.mygdx.game.network;

import com.badlogic.gdx.math.Vector2;

import java.io.Serializable;

public class SpriteSerialize implements Serializable {
    private int id;
    private Type type;
    private Vector2 pos;
    private Vector2 linVel;

    public enum Type {
        TANK, PROJECTILE
    }

    public SpriteSerialize(int id, Type type, Vector2 pos, Vector2 linVel) {
        this.id = id;
        this.type = type;
        this.pos = pos;
        this.linVel = linVel;
    }

    public int getId() {
        return id;
    }

    public Vector2 getPos() {
        return pos;
    }

    public Type getType() {
        return type;
    }

    public Vector2 getLinVel() {
        return linVel;
    }

    @Override
    public String toString() {
        return type + " - " + pos;
    }
}

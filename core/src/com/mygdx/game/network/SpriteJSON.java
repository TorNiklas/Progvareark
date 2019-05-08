package com.mygdx.game.network;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.sprites.Projectile;

import org.json.JSONObject;

import java.util.Locale;

public class SpriteJSON extends JSONObject {

    public enum Type {
        TANK, BARREL, ENERGY, TIME, STATE,
        STANDARD,
        SPREAD,
        LASER,
        AIRSTRIKE
    }

    public SpriteJSON(String in) {
        super(in);
    }

    public SpriteJSON(int id, Type type, Vector2 pos, Vector2 vel, float angle) {
        this.put("ID", padNum(id));
        this.put("TYPE", padNum(type.ordinal()));
        this.put("POSX", padNum((int)pos.x));
        this.put("POSY", padNum((int)pos.y));
        this.put("VELX", padNum((int)vel.x));
        this.put("VELY", padNum((int)vel.y));
        this.put("ANGLE", padNum((int)angle));
    }

    public SpriteJSON(Type type, int ammo, float energy, float health) {
        this.put("ID", padNum(ammo));
        this.put("TYPE", padNum(type.ordinal()));
        this.put("POSX", padNum((int)health));
        this.put("POSY", padNum((int)energy));
        this.put("VELX", padNum(0));
        this.put("VELY", padNum(0));
        this.put("ANGLE", padNum(0));
    }

    public SpriteJSON(Type type, int player, float time) {
        this.put("ID", padNum(player));
        this.put("TYPE", padNum(type.ordinal()));
        this.put("POSX", padNum((int)time));
        this.put("POSY", padNum(0));
        this.put("VELX", padNum(0));
        this.put("VELY", padNum(0));
        this.put("ANGLE", padNum(0));
    }

    public static String padNum(int num) {
        return String.format(new Locale("us"),"%05d", num);
    }

    public float getEnergy() {
        if (getType() == Type.ENERGY) {
            return this.getPosY();
        }
        return 0;
    }

    public float getHealth() {
        if (getType() == Type.ENERGY) {
            return this.getPosX();
        }
        return 0;
    }

    public int getTime() {
        return getPosX();
    }

    public int getPlayer() {
        return getID();
    }

    public int getAmmo() {
        return getID();
    }

    public int getID() {
        return this.getInt("ID");
    }

    private int getPosX() {
        return this.getInt("POSX");
    }

    private int getPosY() {
        return this.getInt("POSY");
    }

    public Type getType() {
        return Type.values()[this.getInt("TYPE")];
    }

    public Projectile.AmmoType getAmmoType() {
        switch (getType()) {
            case STANDARD: return Projectile.AmmoType.STANDARD;
            case LASER: return Projectile.AmmoType.LASER;
            case SPREAD: return Projectile.AmmoType.STANDARD;
            case AIRSTRIKE: return Projectile.AmmoType.AIRSTRIKE;
        }
        return null;
    }

    public Vector2 getPos() {
        return new Vector2(this.getInt("POSX"), this.getInt("POSY"));
    }

    public Vector2 getVel() {
        return new Vector2(this.getInt("VELX"), this.getInt("VELY"));
    }

    public int getAngle() {
        return this.getInt("ANGLE");
    }

}

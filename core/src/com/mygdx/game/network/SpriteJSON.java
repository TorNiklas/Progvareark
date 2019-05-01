package com.mygdx.game.network;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.sprites.GameSprite;
import com.mygdx.game.sprites.Projectile;

import org.json.JSONObject;

import java.util.Locale;

public class SpriteJSON extends JSONObject {

    public enum Type {
        TANK, BARREL,
        STANDARD,
        SPREAD,
        LASER,
        AIRSTRIKE // todo Ammo types exist both here and in Projectile
    }

    public SpriteJSON(String in) {
        super(in);
//        System.out.println(in);
    }

    public SpriteJSON(int id, Type type, Vector2 pos, Vector2 vel, float angle) {
//        super(4);
        this.put("ID", padNum(id));
        this.put("TYPE", padNum(type.ordinal()));
        this.put("POSX", padNum((int)pos.x));
        this.put("POSY", padNum((int)pos.y));
        this.put("VELX", padNum((int)vel.x));
        this.put("VELY", padNum((int)vel.y));
        this.put("ANGLE", padNum((int)angle));
    }

    public static String padNum(int num) {
        return String.format(new Locale("us"),"%05d", num);
    }

    public int getID() {
//        System.out.println(this.toString());
        return this.getInt("ID");
    }

    public Type getType() {
        return Type.values()[this.getInt("TYPE")];
    }

    public Projectile.AmmoType getAmmoType() {
        switch (getType()) {
            case STANDARD: return Projectile.AmmoType.STANDARD;
            case LASER: return Projectile.AmmoType.LASER;
            case SPREAD: return Projectile.AmmoType.SPREAD;
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

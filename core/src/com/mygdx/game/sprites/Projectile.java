package com.mygdx.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.TankGame;
import com.mygdx.game.network.SpriteSerialize;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public class Projectile implements GameSprite {
    private static final AtomicInteger idCounter = new AtomicInteger(); //Highest id
    private boolean local;
    private int id;
    private Vector3 position;
    private Vector3 velocity;
    private Rectangle bounds;
    private Sprite sprite;
    private Body body;

    public Projectile(World world, float x, float y, Vector2 force) {
        id = idCounter.incrementAndGet();
        local = true;
        System.out.println("New projectile local: " + id);
        sprite = new Sprite(new Texture("bullet.png"));
        sprite.setPosition(x, y);
        sprite.setOriginCenter();

        generateProjectile(world, new Vector2(sprite.getX(), sprite.getY()));
        Vector2 impulse = new Vector2(force.x*2, force.y*2);
        body.applyLinearImpulse(impulse, new Vector2(x, y), true);
    }

    public Projectile(World world, int id, float x, float y, Vector2 linVel) {
        System.out.println("New projectile from network: " + id);
        local = false;
        idCounter.set(id);
        this.id = id;

        sprite = new Sprite(new Texture("bullet.png"));
        sprite.setPosition(x, y);
        sprite.setOriginCenter();
        generateProjectile(world, new Vector2(sprite.getX(), sprite.getY()));
        body.setLinearVelocity(linVel);
    }

    private void generateProjectile(World world, Vector2 pos) {
        // body definition
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(pos.x, pos.y);
        bodyDef.bullet = true;

        // create shapes
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(sprite.getWidth()/2, sprite.getHeight()/2);

        // create fixtures
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.3f;

        // add body to world
        body = world.createBody(bodyDef);

        // attach fixtures
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef);

        // clean up
        shape.dispose();
    }

    @Override
    public void update(){
        sprite.setPosition(body.getPosition().x - sprite.getWidth()/2, body.getPosition().y - sprite.getHeight()/2);
    }

    public void setVelocity(Vector3 velocity) {
        this.velocity = velocity;
    }

    @Override
    public Vector2 getPosition() {
        return new Vector2(sprite.getX(), sprite.getY());
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public boolean isLocal() {
        return local;
    }

    @Override
    public SpriteSerialize getSerialize() {
        return new SpriteSerialize(id, SpriteSerialize.Type.PROJECTILE, getPosition(), body.getLinearVelocity());
    }

    @Override
    public void readSerialize(SpriteSerialize sprite) {
        //body.setLinearVelocity(getPosition().lerp(sprite.getPos(), 0.5f));
        //body.setLinearVelocity(0, 0);
        //body.setTransform(sprite.getPos(), 0);
        //body.applyForce(getPosition().lerp(sprite.getPos(), 0.5f), sprite.getPos(), true);

        body.setTransform(sprite.getPos(), 0);
        //body.applyForce(sprite.getLinVel(), sprite.getPos(), true);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    @Override
    public void dispose(){

    }

    @Override
    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }
}

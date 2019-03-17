package com.mygdx.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.TankGame;
import com.mygdx.game.network.SpriteSerialize;

import java.util.concurrent.atomic.AtomicInteger;

public class Projectile implements GameSprite, Pool.Poolable {
    private boolean local;
    private int id;
    private Vector3 position;
    private Vector3 velocity;
    private Rectangle bounds;
    private Sprite sprite;
    private Body body;
    private boolean alive;

    // particle effect
    private ParticleEffect trailEffect;

    public Projectile() {
        this.alive = false;
        sprite = new Sprite(new Texture("bullet.png"));
        sprite.setPosition(-10, -10);
        sprite.setOriginCenter();
    }

    public Projectile(World world, float x, float y, Vector2 force) {
        //id = idCounter.incrementAndGet();
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
        //idCounter.set(id);
        this.id = id;

        sprite = new Sprite(new Texture("bullet.png"));
        sprite.setPosition(x, y);
        sprite.setOriginCenter();
        generateProjectile(world, new Vector2(sprite.getX(), sprite.getY()));
        body.setLinearVelocity(linVel);
    }

    public void init(World world, Vector2 pos, Vector2 velocity) {
        generateProjectile(world, new Vector2(pos.x, pos.y));
        body.setLinearVelocity(velocity);
        alive = true;

        // particle effect
        trailEffect(pos.x, pos.y, 0.2f, 2000);
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
        fixtureDef.friction = 0.7f;
        fixtureDef.restitution = 0.1f;

        // add body to world
        body = world.createBody(bodyDef);

        // attach fixtures
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef);

        // clean up
        shape.dispose();
    }

    public boolean outOfBounds() {
        boolean insideX = (sprite.getX() - 10 < TankGame.WIDTH && sprite.getX() + 10 > 0);
        boolean insideY = (sprite.getY() - 10 < TankGame.HEIGHT && sprite.getY() + 10 > 0);
        return !(insideX && insideY);
    }

    public boolean inactiveBody() {
        return !(body.isAwake() && body.getLinearVelocity().len() > 0.05f);
    }

    public void trailEffect(float x, float y, float scale, int duration) {
        // create effect
        trailEffect = new ParticleEffect();
        trailEffect.load(Gdx.files.internal("effects/smoke_trail.p"), Gdx.files.internal("effects"));
        trailEffect.setPosition(x, y);
        trailEffect.scaleEffect(scale);
        trailEffect.setDuration(duration);
        trailEffect.start();
    }

    @Override
    public void update(){
        sprite.setPosition(body.getPosition().x - sprite.getWidth()/2, body.getPosition().y - sprite.getHeight()/2);
        trailEffect.setPosition(body.getPosition().x, body.getPosition().y);
        // set alive false if projectile out of bounds or hit
        if(outOfBounds() || inactiveBody()) {
            alive = false;
        }
    }

    public void setVelocity(Vector3 velocity) {
        this.velocity = velocity;
    }

    public boolean isAlive() {
        return alive;
    }

    @Override
    public Vector2 getPosition() {
        return new Vector2(sprite.getX(), sprite.getY());
//        return body.getWorldCenter();
    }

    @Override
    public Sprite getSprite() {
        return sprite;
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
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
    public Body getBody() {
        return body;
    }

    @Override
    public void dispose(){
        sprite.getTexture().dispose();
    }

    @Override
    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
        trailEffect.draw(batch, Gdx.graphics.getDeltaTime());
    }

    @Override
    public void reset() {
        body.setTransform(new Vector2(-50,-50), 0);
        body.setLinearVelocity(0, 0);
        body.setActive(false);
        alive = false;
    }
}

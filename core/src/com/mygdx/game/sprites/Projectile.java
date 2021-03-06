package com.mygdx.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.AssetHandler;
import com.mygdx.game.TankGame;
import com.mygdx.game.network.SpriteJSON;

public class Projectile extends GameSprite implements Pool.Poolable {
    private Sprite sprite;
    private boolean alive;
    private Texture bulletTexture;
    private AssetHandler assetHandler;
    private AmmoType type;
    private float dmg;

    public enum AmmoType {
        STANDARD,
        SPREAD,
        LASER,
        AIRSTRIKE;

        private static AmmoType[] vals = values();

        public AmmoType getNext() {
            return vals[(this.ordinal() + 1) % vals.length];
        }

        public AmmoType getPrev() {
            int prevIndex = (this.ordinal() - 1) < 0 ? (this.ordinal() + vals.length - 1) : (this.ordinal() - 1);
            System.out.println(vals[prevIndex % vals.length]);
            return vals[prevIndex % vals.length];
        }

        public int getCost() {
            int cost = 1;
            switch (this) {
                case STANDARD:
                    cost = 1;
                    break;

                case SPREAD:
                    cost = 5;
                    break;

                case LASER:
                    cost = 5;
                    break;

                case AIRSTRIKE:
                    cost = 7;
                    break;
            }
            return cost;
        }
    }

    // particle effect
    private ParticleEffect trailEffect;

    public Projectile() {
        // set asset handler
        assetHandler = ((TankGame)Gdx.app.getApplicationListener()).assetHandler;
        this.alive = false;
    }

    public Projectile(World world, float x, float y, Vector2 force) {
        local = true;
        System.out.println("New projectile local: " + id);
        sprite = new Sprite(new Texture("bullet.png"));
        sprite.setPosition(x, y);
        sprite.setOriginCenter();

        generateProjectile(world, new Vector2(sprite.getX(), sprite.getY()));
        Vector2 impulse = new Vector2(force.x * 2, force.y * 2);
        body.applyLinearImpulse(impulse, new Vector2(x, y), true);

    }

    public Projectile(World world, int id, float x, float y, Vector2 linVel) {
        System.out.println("New projectile from network: " + id);
        local = false;
        this.id = id;
        bulletTexture = assetHandler.manager.get(assetHandler.bulletPath);
        sprite = new Sprite(bulletTexture);
        sprite.setPosition(x, y);
        sprite.setOriginCenter();
        generateProjectile(world, new Vector2(sprite.getX(), sprite.getY()));
        body.setLinearVelocity(linVel);
    }

    public void init(World world, AmmoType type, Vector2 pos, Vector2 velocity) {
        this.type = type;

        switch (type) {
            case STANDARD:
                bulletTexture = assetHandler.manager.get(assetHandler.bulletPath);
                dmg = 15.0f;
                break;

            case SPREAD:
                bulletTexture = assetHandler.manager.get(assetHandler.bulletSpreadPath);
                dmg = 7.5f;
                break;

            case LASER:
                bulletTexture = assetHandler.manager.get(assetHandler.bulletLaserPath);
                dmg = 10.0f;
                break;

            case AIRSTRIKE:
                bulletTexture = assetHandler.manager.get(assetHandler.bulletMissilePath);
                dmg = 10.0f;
                break;
        }
        sprite = new Sprite(bulletTexture);
        sprite.setPosition(-10, -10);
        sprite.setOriginCenter();

        // generate projectile and set velocity
        generateProjectile(world, new Vector2(pos.x, pos.y));
        body.setLinearVelocity(velocity);
        switch (type) {
            case LASER:
                body.setGravityScale(0.0f);
                body.setLinearVelocity(velocity.scl(1000));
                break;
            case AIRSTRIKE:
                body.setLinearVelocity(new Vector2(0, -1000));
                break;
        }
        // particle effect
        trailEffect(/*type, */pos.x, pos.y, 0.2f, 2000);
        alive = true;
    }

    private void generateProjectile(World world, Vector2 pos) {
        // body definition
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(pos.x, pos.y);
        bodyDef.bullet = true;

        // create shapes
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(sprite.getWidth() / 2, sprite.getHeight() / 2);

        // create fixtures
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.7f;
        fixtureDef.restitution = 0.1f;

        // add body to world
        body = world.createBody(bodyDef);

        // set group index to disable collision between projectiles
        fixtureDef.filter.groupIndex = -1;

        // attach fixtures
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef);

        // set user data with dmg
        body.setUserData(dmg);

        // clean up
        shape.dispose();
    }

    public boolean outOfBounds() {
        boolean insideX = (sprite.getX() - 10 < TankGame.WIDTH && sprite.getX() + 10 > 0);
        boolean insideY = (sprite.getY() - 10 < TankGame.HEIGHT && sprite.getY() + 10 > 0);
        return !(insideX && insideY);
    }

    public boolean inactiveBody() {
        return !(body.isAwake() && body.getLinearVelocity().len() > 0.001f);
    }

    public void trailEffect(/*AmmoType type, */float x, float y, float scale, int duration) {
        // create effect
        trailEffect = new ParticleEffect();
        switch (type) {
            case LASER:
                trailEffect.load(Gdx.files.internal("effects/laser_p.p"), Gdx.files.internal("effects"));
                break;
            default:
                trailEffect.load(Gdx.files.internal("effects/smoke_trail.p"), Gdx.files.internal("effects"));
                break;
        }
        trailEffect.setPosition(x, y);
        trailEffect.scaleEffect(scale);
        trailEffect.setDuration(duration);
        trailEffect.start();
    }

    @Override
    public void update() {
        sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y - sprite.getHeight() / 2);
        trailEffect.setPosition(body.getPosition().x, body.getPosition().y);
        // set alive false if projectile out of bounds or hit
        if (outOfBounds() || inactiveBody()) {
            alive = false;
        }
    }


    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public AmmoType getType(){
        return type;
    }

    @Override
    public Vector2 getPosition() {
        return new Vector2(sprite.getX(), sprite.getY());
    }

    public Vector2 getBodyPosition() {
        return new Vector2(sprite.getX() + sprite.getWidth() / 2, sprite.getY() + sprite.getHeight() / 2);
    }

    @Override
    public Sprite getSprite() {
        return sprite;
    }

    @Override
    public SpriteJSON getJSON() {
        switch(type) {
            case STANDARD: return new SpriteJSON(id, SpriteJSON.Type.STANDARD, getBodyPosition(), body.getLinearVelocity(), 0);
            case LASER: return new SpriteJSON(id, SpriteJSON.Type.LASER, getBodyPosition(), body.getLinearVelocity(), 0);
            case AIRSTRIKE: return new SpriteJSON(id, SpriteJSON.Type.AIRSTRIKE, getBodyPosition(), body.getLinearVelocity(), 0);
            case SPREAD: return new SpriteJSON(id, SpriteJSON.Type.SPREAD, getBodyPosition(), body.getLinearVelocity(), 0);
        }
        return null;
    }

    @Override
    public void readJSON(SpriteJSON obj) {
        Vector2 target = obj.getPos();
        float diff = getPosition().sub(target).len(); //distance from current to target

        if (diff > 5) { //Tolerance for projectile error, relatively loose
            body.setTransform(obj.getPos(), obj.getAngle());
            body.setLinearVelocity(obj.getVel());
        }
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

    @Override
    public String toString() {
        return "Projectile{" +
                "local=" + local +
                ", id=" + id +
                ", position=" + getPosition() +
                ", alive=" + alive +
                '}';
    }
}

package com.mygdx.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.TankGame;
import com.mygdx.game.network.SpriteSerialize;

public class Tank implements GameSprite {
    private int id = -1;
    private boolean local = true;
    private Vector3 position;
    private Sprite tankSprite;
    private Sprite barrelSprite;
    private Body body;
    //private static final AtomicInteger idCounter = new AtomicInteger();

    public Tank(World world, int x, int y) {
        //id = idCounter.incrementAndGet();

        // tank sprite
        tankSprite = new Sprite(new Texture("tank.png"));
        tankSprite.setPosition(x, y);
        tankSprite.setOriginCenter();

        // barrel sprite
        barrelSprite = new Sprite(new Texture("barrel.png"));
        barrelSprite.setOrigin(0f, barrelSprite.getHeight()/2);

        // create box2d tank
        generateTank(world, new Vector2(tankSprite.getX(), tankSprite.getY()));
    }

    private void generateTank(World world, Vector2 pos) {
        // body definition
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(pos.x, pos.y);

        // create shapes
        PolygonShape tankShape = new PolygonShape();
        tankShape.setAsBox(tankSprite.getWidth()/2, tankSprite.getHeight()/2);

        PolygonShape barrelShape = new PolygonShape();
        barrelShape.setAsBox(barrelSprite.getWidth()/2, barrelSprite.getHeight()/4, new Vector2(8f, 4f), 0f);

        // create fixtures
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 2.5f;
        fixtureDef.friction = 0.7f;
        fixtureDef.restitution = 0.05f;

        // add body to world
        body = world.createBody(bodyDef);

        // attach fixtures
        fixtureDef.shape = tankShape;
        body.createFixture(fixtureDef);

        fixtureDef.shape = barrelShape;
        body.createFixture(fixtureDef);

        // clean up
        tankShape.dispose();
        barrelShape.dispose();
    }

    @Override
    public boolean isLocal() {
        return local;
    }

    @Override
    public void update(){
        // tank
        tankSprite.setPosition(body.getPosition().x - tankSprite.getWidth()/2, body.getPosition().y - tankSprite.getHeight()/2);
        tankSprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);

        // barrel
        barrelSprite.setPosition(body.getPosition().x - barrelSprite.getWidth()/2 + 8f, body.getPosition().y - barrelSprite.getHeight()/2 + 4f);
    }

    @Override
    public Vector2 getPosition() {
        return new Vector2(tankSprite.getX(), tankSprite.getY());
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public SpriteSerialize getSerialize() {
        return new SpriteSerialize(id, SpriteSerialize.Type.TANK, getPosition(), body.getLinearVelocity());
    }

    @Override
    public void readSerialize(SpriteSerialize sprite) {
        if (id == sprite.getId()) {
            this.tankSprite.setX(sprite.getPos().x);
            this.tankSprite.setY(sprite.getPos().y);
            body.setLinearVelocity(sprite.getLinVel());
        }
        else {
            System.out.println("Wrong ID!");
        }
    }

    public void updateBarrel(int deg){
        //int pointerX = x;
        //int pointerY = -(y - TankGame.HEIGHT);
        //float deg = (float) Math.atan2(pointerY - body.getPosition().y, pointerX - body.getPosition().x) * MathUtils.radiansToDegrees;
        barrelSprite.setRotation(deg);
    }

    private void rotateBarrel() {


    }

    public GameSprite fireProjectile(World world, float pointerX, float pointerY) {
        //float forceX = pointerX - body.getPosition().x;
        //float forceY = -(pointerY - TankGame.HEIGHT) - body.getPosition().y;

        float forceX = pointerX * 1000;
        float forceY = pointerY * 1000;

        System.out.println(forceX);
        System.out.println(forceY);

        return new Projectile(world, body.getPosition().x, body.getPosition().y + tankSprite.getHeight()/2, new Vector2(forceX, forceY));
    }

    public void drive(Vector2 force) {
        body.setLinearVelocity(force);
    }

    @Override
    public Sprite getSprite() {
        return tankSprite;
    }

    @Override
    public void dispose(){
        tankSprite.getTexture().dispose();
        barrelSprite.getTexture().dispose();
    }

    @Override
    public void draw(SpriteBatch batch) {
        barrelSprite.draw(batch);
        tankSprite.draw(batch);
    }
}

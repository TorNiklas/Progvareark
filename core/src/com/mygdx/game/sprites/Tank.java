package com.mygdx.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.WheelJoint;
import com.badlogic.gdx.physics.box2d.joints.WheelJointDef;
import com.mygdx.game.TankGame;
import com.mygdx.game.network.SpriteSerialize;
import com.mygdx.game.states.PlayState;

import static java.lang.Math.cos;
import static java.lang.StrictMath.sin;

public class Tank implements GameSprite {
    private int id = -1;
    private boolean local = true;
    private Vector3 position;
    private Sprite tankSprite;
    private Sprite barrelSprite;
    private Body body;

    private boolean moveLeft;
    private boolean moveRight;
    private boolean increase;
    private boolean decrease;
    private boolean isPoweringUp;
    private boolean gainPower;

    private int firePower;
    private int maxFirePower;

    private int barrelDeg;
    private int aimRate;

    private float energy;
    private float health;

    PlayState state;
    //private static final AtomicInteger idCounter = new AtomicInteger();

    public Tank(World world, PlayState state, int x, int y) {
        // tank sprite
        tankSprite = new Sprite(new Texture("tank.png"));
        tankSprite.setPosition(x, y);
        tankSprite.setOriginCenter();

        // barrel sprite
        barrelSprite = new Sprite(new Texture("barrel.png"));
        barrelSprite.setOrigin(0f, barrelSprite.getHeight()/2);

        // create box2d tank
        generateTank(world, new Vector2(tankSprite.getX(), tankSprite.getY()));

        // movement
        moveLeft = false;
        moveRight = false;

        // barrel angle
        decrease = false;
        increase = false;

        // barrel rotation settings
        barrelDeg = 0;
        aimRate = 2;

        // fire power
        isPoweringUp = false;
        gainPower = true;
        firePower = 1;
        maxFirePower = 150;

        // stats
        energy = 100.0f;
        health = 100.0f;

        this.state = state;
    }

    private void generateTank(World world, Vector2 pos) {
        // body definition
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(pos.x, pos.y);

        // create shapes
        PolygonShape tankShape = new PolygonShape();
        tankShape.setAsBox(tankSprite.getWidth()/2, tankSprite.getHeight()/2);

        // create fixtures
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 2.5f;
        fixtureDef.friction = 0.7f;
        fixtureDef.restitution = 0f;

        // add body to world
        body = world.createBody(bodyDef);

        // attach fixture
        fixtureDef.shape = tankShape;
        body.createFixture(fixtureDef);
        
        body.setAngularDamping(2f);

        // clean up
        tankShape.dispose();
    }

    @Override
    public boolean isLocal() {
        return local;
    }

    @Override
    public void update(){
        // handle movement
        move();

        // handle barrel rotation
        updateBarrel();

        // handle fire power
        powerUp();

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

    public Vector2 getBarrelPosition() {
        float[] vertices = barrelSprite.getVertices();
        float barrelX = (vertices[SpriteBatch.X3] + vertices[SpriteBatch.X4])/2;
        float barrelY = (vertices[SpriteBatch.Y3] + vertices[SpriteBatch.Y4])/2;
        return new Vector2(barrelX, barrelY);
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

    public int getFirePower() {
        return firePower;
    }

    public void setPoweringUp(boolean isPoweringUp) {
        this.isPoweringUp = isPoweringUp;
    }

	public void powerUp() {
		if(isPoweringUp) {
		    if(firePower > maxFirePower || firePower < 1) {
                gainPower = !gainPower;
            }

            if(gainPower) {
                firePower += 1;
            } else {
                firePower -= 1;
            }
		}
	}

    public void fireProjectile() {
        float vectorY = (float) sin(Math.toRadians(barrelDeg));
        float vectorX = (float) cos(Math.toRadians(barrelDeg));

        // start pos
        Vector2 pos = getBarrelPosition();
        // exit velocity
        Vector2 velocity = new Vector2(vectorX * firePower, vectorY * firePower);
        // use object pooling
        state.fireFromPool(pos, velocity);

        // reset power
        firePower = 1;
	}

    public void move() {
        if(moveLeft && energy > 0) {
            energy -= 0.25;
            body.setLinearVelocity(new Vector2(-30f, body.getLinearVelocity().y));
        } else if(moveRight && energy > 0) {
            energy -= 0.25;
            body.setLinearVelocity(new Vector2(30f, body.getLinearVelocity().y));
        } else {
            body.setLinearVelocity(new Vector2(0f, body.getLinearVelocity().y));
        }
    }

    public void updateBarrel() {
        if(increase) {
            barrelDeg -= aimRate;
        }
        if(decrease) {
            barrelDeg += aimRate;
        }
        barrelSprite.setRotation(barrelDeg);
    }

    public void setMoveLeft(boolean moveLeft) {
        this.moveLeft = moveLeft;
    }

    public void setMoveRight(boolean moveRight) {
        this.moveRight = moveRight;
    }

    public void setIncrease(boolean increase) {
        this.increase = increase;
    }

    public void setDecrease(boolean decrease) {
        this.decrease = decrease;
    }

    public float getEnergy() {
        return energy;
    }

    public float getHealth() {
        return health;
    }

    public void setEnergy(float energy) {
        this.energy = energy;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    @Override
    public Body getBody() {
        return body;
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

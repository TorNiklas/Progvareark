package com.mygdx.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.AssetHandler;
import com.mygdx.game.TankGame;
import com.mygdx.game.network.SpriteJSON;
import com.mygdx.game.states.PlayState;

import static java.lang.Math.cos;
import static java.lang.StrictMath.sin;

public class Tank extends GameSprite {
    private Vector3 position;
    private Sprite tankSprite;
    private Sprite barrelSprite;
    private Sprite airStrike;

    private boolean moveLeft;
    private boolean moveRight;
    private boolean increase;
    private boolean decrease;
    private boolean airStrikeIncrease;
    private boolean airStrikeDecrease;
    private boolean isPoweringUp;
    private boolean gainPower;

    private int firePower;
    private int maxFirePower;
    private int step;

    private float barrelDeg;
    private int airStrikePos;
    private int aimRate;

    private float energy;
    private float health;

    private int ammo;
    private int maxAmmo;

    private Projectile.AmmoType activeAmmoType;

    private AssetHandler assetHandler;

    PlayState state;

	public Tank(World world, PlayState state, int x, int y, boolean local, int id) {
	    this(world, state, x, y);
	    this.local = local;
	    this.id = id;
    }

    public Tank(World world, PlayState state, int x, int y) {
        // set asset handler
        assetHandler = ((TankGame) Gdx.app.getApplicationListener()).assetHandler;

        // tank sprite
        tankSprite = new Sprite((Texture) assetHandler.manager.get(assetHandler.tankPath)); //new Sprite(new Texture("tank.png"));
        tankSprite.setPosition(x, y);
        tankSprite.setOriginCenter();

        airStrike = new Sprite((Texture) assetHandler.manager.get(assetHandler.airstrikePath)); //new Sprite(new Texture("airstrike.png"));
        airStrike.setPosition(x, y);
        airStrike.setOriginCenter();

        // barrel sprite
        barrelSprite = new Sprite((Texture) assetHandler.manager.get(assetHandler.barrelPath)); //new Sprite(new Texture("barrel.png"));
        barrelSprite.setScale(1.3f, 1f);
        barrelSprite.setOrigin(0, barrelSprite.getHeight()/2f);

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
        maxFirePower = 300;
        step = 2;

        // airstrike
        airStrikePos = x;

        // stats
        energy = 100.0f;
        health = 100.0f;

        // set ammo
        maxAmmo = 10;
        ammo = maxAmmo;
        activeAmmoType = Projectile.AmmoType.STANDARD;

        this.state = state;
    }



    private void generateTank(World world, Vector2 pos) {
        // body definition
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(pos.x, pos.y);

        // create fixtures
        FixtureDef fixtureDef = new FixtureDef();

        bodyDef.type = BodyDef.BodyType.DynamicBody;
        fixtureDef.density = 2.5f;
        fixtureDef.friction = 0.7f;
        fixtureDef.restitution = 0f;

        // create shapes
        PolygonShape tankShape = new PolygonShape();
        tankShape.setAsBox(tankSprite.getWidth() / 2, tankSprite.getHeight() / 2);

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
    public void update(){
        // handle movement
        move();

        // handle fire power
        powerUp();

        //Airstrike
        if (activeAmmoType == Projectile.AmmoType.AIRSTRIKE) {updateAirStrike();}

        // tank
        tankSprite.setPosition(body.getPosition().x - tankSprite.getWidth()/2, body.getPosition().y - tankSprite.getHeight()/2);
        float currTank = tankSprite.getRotation();
        tankSprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);

        // barrel
        barrelSprite.setPosition(body.getPosition().x - barrelSprite.getWidth()/2 + 8f, body.getPosition().y - barrelSprite.getHeight()/2 + 4f);

        barrelDeg = barrelSprite.getRotation() - (currTank-tankSprite.getRotation());
        updateBarrel();
    }

    @Override
    public Vector2 getPosition() {
        return new Vector2(tankSprite.getX() + (tankSprite.getWidth() / 2),
                tankSprite.getY() + (tankSprite.getHeight() / 2));
    }

    public Vector2 getBarrelPosition() {
        float[] vertices = barrelSprite.getVertices();
        float barrelX = (vertices[SpriteBatch.X3] + vertices[SpriteBatch.X4])/2;
        float barrelY = (vertices[SpriteBatch.Y3] + vertices[SpriteBatch.Y4])/2;
        return new Vector2(barrelX, barrelY);
    }

    public Projectile.AmmoType getNextAmmo() {
        return activeAmmoType.getNext();
    }

    public Projectile.AmmoType getPrevAmmo() {
        return activeAmmoType.getPrev();
    }

    public Projectile.AmmoType getActiveAmmoType() {
        return activeAmmoType;
    }

    public void setActiveAmmoType(Projectile.AmmoType activeAmmoType) {
        this.activeAmmoType = activeAmmoType;
    }

    @Override
    public SpriteJSON getJSON() {
	    return new SpriteJSON(id, SpriteJSON.Type.TANK, getPosition(), body.getLinearVelocity(), body.getAngle());
    }

    public SpriteJSON getBarrelJSON() {
	    return new SpriteJSON(id, SpriteJSON.Type.BARREL, getBarrelPosition(), body.getLinearVelocity(), barrelDeg);
    }

    @Override
    public void readJSON(SpriteJSON obj) {
        float posDiff = obj.getPos().x - getPosition().x;
        setEnergy(100);
        if (posDiff > 1) {
            moveRight = true;
            moveLeft = false;
        }
        else if (posDiff < -1) {
            moveRight = false;
            moveLeft = true;
        }
        else {
            moveRight = false;
            moveLeft = false;
        }
    }

    public void readBarrelJSON(SpriteJSON json) {
	    float diff = json.getAngle() - barrelDeg;
	    if (diff > 5) {
	        increase = false;
	        decrease = true;
            System.out.println("Decr");
        }
        else if (diff < -5) {
            increase = true;
            decrease = false;
            System.out.println("Incr");
        }
        else {
            increase = false;
            decrease = false;
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
                firePower += step;
            } else {
                firePower -= step;
            }
		}
	}

    public void fireProjectile(Projectile.AmmoType type) {
        float vectorY = (float) sin(Math.toRadians(barrelDeg));
        float vectorX = (float) cos(Math.toRadians(barrelDeg));

        // start pos
        Vector2 pos;
        switch (activeAmmoType){
            case AIRSTRIKE:
                pos = new Vector2(airStrikePos + (airStrike.getWidth() / 2), TankGame.HEIGHT);
                break;
            default:
                pos = getBarrelPosition();
                break;
        }
        // exit velocity
        Vector2 velocity = new Vector2(vectorX * firePower, vectorY * firePower);
        // use object pooling
        state.fireFromPool(type, pos, velocity);

        // reset power
        firePower = 1;

        // decrease ammo count
        ammo -= getActiveAmmoType().getCost();
	}

    public void move() {
        if(moveLeft && energy > 0) {
            energy -= 0.25;
            body.setLinearVelocity(new Vector2( body.getPosition().x > (tankSprite.getWidth()/2) ? -30f : 0f, body.getLinearVelocity().y));
        } else if(moveRight && energy > 0) {
            energy -= 0.25;
            body.setLinearVelocity(new Vector2(body.getPosition().x < TankGame.WIDTH - (tankSprite.getWidth() / 2) ? 30f : 0f, body.getLinearVelocity().y));
        } else {
            body.setLinearVelocity(new Vector2(0f, body.getLinearVelocity().y));
        }
    }

    public void updateBarrel() {
        if(increase && barrelDeg+10 > Math.toDegrees(body.getAngle())) {
            barrelDeg -= aimRate;
        }
        if(decrease && barrelDeg-10 < 180+Math.toDegrees(body.getAngle())) {
            barrelDeg += aimRate;
        }
        barrelSprite.setRotation(barrelDeg);
    }

    public void updateAirStrike() {
        if(airStrikeIncrease && airStrikePos < TankGame.WIDTH-airStrike.getWidth()) {
            airStrikePos += 5;
        }
        if(airStrikeDecrease && airStrikePos > 0) {
            airStrikePos -= 5;
        }
        airStrike.setPosition(airStrikePos, airStrike.getY());
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
    public void setIncreaseAirStrike(boolean increase) {this.airStrikeIncrease = increase;}
    public void setDecreaseAirStrike(boolean decrease) {this.airStrikeDecrease = decrease;}
    public float getEnergy() {
        return energy;
    }

    public float getHealth() {
        return health;
    }

    public void reduceHealth(float health) {
	    this.health -= health;
    }

    public int getAmmo() {
        return ammo;
    }

    public void setAmmo(int ammo) {
        this.ammo = ammo;
    }

    public void addAmmo(int ammo) {
	    this.ammo += ammo;
    }

    public int getMaxAmmo() {
        return maxAmmo;
    }

    public void setEnergy(float energy) {
        this.energy = energy;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    @Override
    public Sprite getSprite() {
        return tankSprite;
    }

    @Override
    public void dispose(){
    }

    @Override
    public void draw(SpriteBatch batch) {
        barrelSprite.draw(batch);
        tankSprite.draw(batch);
        switch (activeAmmoType){
            case AIRSTRIKE:
                airStrike.draw(batch);
                break;
        }
    }

    @Override
    public String toString() {
        return "Tank{" +
                "energy=" + energy +
                ", health=" + health +
                ", ammo=" + ammo +
                ", maxAmmo=" + maxAmmo +
                ", activeAmmoType=" + activeAmmoType +
                ", id=" + id +
                ", local=" + local +
                '}';
    }
}

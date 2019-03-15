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
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.TankGame;
import com.mygdx.game.states.PlayState;

public class Tank implements GameSprite {
    private Vector3 position;
    private Sprite tankSprite;
    private Sprite barrelSprite;
    private Body body;

	private ShapeRenderer shapeRenderer;
	static private boolean projectionMatrixSet;

    public Tank(World world, int x, int y) {
		shapeRenderer = new ShapeRenderer();
		projectionMatrixSet = false;
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
        fixtureDef.restitution = 0.2f;

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
    public void update(){
        // tank
        tankSprite.setPosition(body.getPosition().x - tankSprite.getWidth()/2, body.getPosition().y - tankSprite.getHeight()/2);
        tankSprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);

        // barrel
        barrelSprite.setPosition(body.getPosition().x - barrelSprite.getWidth()/2 + 8f, body.getPosition().y - barrelSprite.getHeight()/2 + 4f);
    }

    public Vector3 getPosition() {
        return position;
    }

    public void updateBarrel(int deg){
        //int pointerX = x;
        //int pointerY = -(y - TankGame.HEIGHT);
        //float deg = (float) Math.atan2(pointerY - body.getPosition().y, pointerX - body.getPosition().x) * MathUtils.radiansToDegrees;
        barrelSprite.setRotation(deg);
    }

    private void rotateBarrel() {


    }
	boolean poweringUp = false;
	public int power = 1;
	int maxPower = 1000;
	public boolean powerUp() {
    	if(poweringUp) {
			poweringUp = !poweringUp;
			return false;
		} else {
    		poweringUp = !poweringUp;
    		return true;
		}
	}
	public void powerTick() {
		if(poweringUp) { power += 10; }// (int)Math.ceil(power*1.5); }
		if(power > maxPower) { power = maxPower; }
	}
    public GameSprite fireProjectile(World world, float pointerX, float pointerY, int force) {
        //float forceX = pointerX - body.getPosition().x;
        //float forceY = -(pointerY - TankGame.HEIGHT) - body.getPosition().y;

//		force = 1000;
//		force = 100000;
        float forceX = pointerX * force;
        float forceY = pointerY * force;

        return new Projectile(world, body.getPosition().x, body.getPosition().y + tankSprite.getHeight()/2, new Vector2(forceX, forceY));
    }

    public void drive(Vector2 force) {
        body.setLinearVelocity(force);
    }

    @Override
    public void dispose(){

    }

    @Override
    public void draw(SpriteBatch batch) {
        barrelSprite.draw(batch);
        tankSprite.draw(batch);

		batch.end();
		if(!projectionMatrixSet){
			shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
		}
		float x = body.getPosition().x-50;
		float y = body.getPosition().y+25;
		float length = 100;
		float height = 20;
		if(poweringUp) {
			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
			shapeRenderer.setColor(Color.WHITE);
			shapeRenderer.rect(x, y, length, height);

			shapeRenderer.setColor(Color.BLACK);
			shapeRenderer.rect(x + 5, y + 5, length - 10, height - 10);

			shapeRenderer.setColor(Color.RED);
			shapeRenderer.rect(x + 5, y + 5, ((float)(power)/(float)(maxPower))*(length-10), height - 10);
			shapeRenderer.end();
		}
		batch.begin();
    }
}

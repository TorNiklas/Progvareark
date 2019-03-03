package com.mygdx.game.sprites;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.utils.Box2DBuild;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.mygdx.game.TankGame;

import java.util.ArrayList;

import static com.badlogic.gdx.math.MathUtils.random;

public class Ground {
    private Vector3 position;
    private Rectangle bounds;
    private Texture texture;

    public Ground(World world) {
        position = new Vector3(0,0, 0);

        // generate random points
        Vector2[] points = generatePoints(75, 100, 170);

        // generate ground
        generateGround(world, points);

    }

    public void generateGround(World world, Vector2[] points) {
        // body definition
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(0, 0);

        // create shape
        ChainShape shape = new ChainShape();
        shape.createChain(points);

        // create fixture
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0f;

        // add body to world and attach fixture
        Body body = world.createBody(bodyDef);
        body.createFixture(fixtureDef);

        // clean up
        shape.dispose();
    }

    public Vector2[] generatePoints(int smoothness, int yMin, int yMax) {
        ArrayList<Vector2> v = new ArrayList<Vector2>();
        for(int i = 0; i < TankGame.WIDTH + smoothness; i+=smoothness) {
            v.add(new Vector2(i, random(yMin, yMax)));
        }
        return v.toArray(new Vector2[0]);
    }

    public void update(float dt){

    }

    public Vector3 getPosition() {
        return position;
    }

    public Texture getTexture() {
        return texture;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void dispose(){
        texture.dispose();
    }
}

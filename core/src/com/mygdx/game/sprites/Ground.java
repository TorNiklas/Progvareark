package com.mygdx.game.sprites;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ShortArray;
import com.mygdx.game.TankGame;

import java.util.ArrayList;
import static com.badlogic.gdx.math.MathUtils.random;

public class Ground {
    private Vector3 position;
    private Body body;
    private PolygonSprite polygonSprite;

    public Ground(World world, long seed, int xoff, int yMin, int yMax, int smoothing, Color color) {
        position = new Vector3(0,0, 0);

        // generate random points
        Vector2[] points = generatePoints(seed, xoff, yMin, yMax);
        smoothPoints(smoothing, points);
        float[] floatPoints = v2ToFloat(points);

        // create pixmap
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();

        // create tex region
        TextureRegion textureRegion = new TextureRegion(new Texture(pixmap));
        EarClippingTriangulator ear = new EarClippingTriangulator();
        ShortArray triangles = ear.computeTriangles(floatPoints);

        // create poly region
        PolygonRegion polygonRegion = new PolygonRegion(textureRegion, floatPoints, triangles.toArray());
        polygonSprite = new PolygonSprite(polygonRegion);

        // generate ground
        generateGround(world, points);

    }

    private void generateGround(World world, Vector2[] points) {
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
        body = world.createBody(bodyDef);
        body.createFixture(fixtureDef);

        // clean up
        shape.dispose();
    }

    private Vector2[] generatePoints(long seed, int xoff, int yMin, int yMax) {
        ArrayList<Vector2> v = new ArrayList<Vector2>();
        RandomXS128 r = new RandomXS128(seed);
        for(int i = 0; i < TankGame.WIDTH+xoff; i+=xoff) {
            int x = i;
            int y = r.nextInt(yMax-yMin)+yMin;//random(yMin, yMax);
            v.add(new Vector2(x, y));
        }
        return v.toArray(new Vector2[0]);
    }

    private void smoothPoints(int passes, Vector2[] points) {
        for(int i = 0; i < passes; i++) {
            for (int x = 1; x < points.length - 1; x++) {
                float prev = points[x-1].y;
                float y = points[x].y;
                float next = points[x+1].y;
                float yAvg = (next + prev) / 2f;
                points[x].y = (y + yAvg) / 2f;
            }
        }
    }

    private float[] v2ToFloat(Vector2[] p) {
        float[] res = new float[p.length*2 + 4];

        // start and end point outside
        res[0] = -10;
        res[1] = -10;
        res[p.length*2 + 2] = TankGame.WIDTH + 10;
        res[p.length*2 + 3] = -10;

        // convert points in list
        int counter = 2;
        for(Vector2 v : p) {
            res[counter] = v.x;
            res[counter+1] = v.y;
            counter += 2;
        }
        return res;
    }

    public Vector3 getPosition() {
        return position;
    }

    public void draw(PolygonSpriteBatch psb) {
        polygonSprite.draw(psb);
    }

    public void dispose(){

    }
}

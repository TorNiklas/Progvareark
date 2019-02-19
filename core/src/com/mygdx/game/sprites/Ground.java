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
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.utils.Box2DBuild;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.mygdx.game.TankGame;

import java.util.ArrayList;

import static com.badlogic.gdx.math.MathUtils.random;

public class Ground {
    private Vector2[] points;
    private Vector3 position;
    private Rectangle bounds;
    private Texture texture;
    private int smoothness;
    private World world;
    private Body body;

    public Ground(World world) {
        this.world = world;

        //bounds = new Rectangle(com.mygdx.game.TankGame.WIDTH/2 - texture.getWidth()/2, com.mygdx.game.TankGame.HEIGHT/2 - texture.getHeight()/2, texture.getWidth(), texture.getHeight());

        points = generatePoints(100, 10, 100);

        Pixmap pixmap = new Pixmap(1280, 720, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.FOREST);

        for(int i = 0; i < points.length; i++) {
            Vector2 v0 = points[i];
            Vector2 v1 = (i+1 < points.length) ? points[i+1] : v0;

            pixmap.drawLine((int)v0.x, (int)v0.y, (int)v1.x, (int)v1.y);

            //System.out.println(v0.x + ", " + v0.y);
            //System.out.println(v1.x + ", " + v1.y);
        }

        texture = new Texture(pixmap);
        position = new Vector3(0,10, 0); //Terrain.WIDTH/2 - texture.getWidth()/2

        makeBody();

        pixmap.dispose();


    }

    public void makeBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.x = 0;
        bodyDef.position.y = 0;


        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = new PolygonShape();
        //((PolygonShape) fixtureDef.shape).set(points);

        body = world.createBody(bodyDef);
        body.createFixture(fixtureDef);
    }

    public Pixmap makePixmap() {
        Pixmap pixmap = new Pixmap(10, 10, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.FOREST);
        return pixmap;

    }

    public void drawTerrain(Pixmap pixmap, Vector2[] vectors) {
        for(Vector2 v : vectors) {
            pixmap.drawPixel((int)v.x, (int)v.y);
        }
    }

    public Vector2[] generatePoints(int smoothness, int yMin, int yMax) {
        ArrayList<Vector2> v = new ArrayList<Vector2>();
        for(int i = 0; i < TankGame.WIDTH + smoothness; i+=smoothness) {
            v.add(new Vector2(i, TankGame.HEIGHT - random(yMin, yMax)));
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

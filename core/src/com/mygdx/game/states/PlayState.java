package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.BTInterface;
import com.mygdx.game.TankGame;
import com.mygdx.game.sprites.GameSprite;
import com.mygdx.game.sprites.Ground;
import com.mygdx.game.sprites.Projectile;
import com.mygdx.game.sprites.Tank;

import java.util.ArrayList;
import java.util.Iterator;

public class PlayState extends State {
    private Texture bg;
    private static ArrayList<GameSprite> gameSprites;
    private static World world;
    private Box2DDebugRenderer debugRenderer;
    private Ground ground;

    public PlayState(/*GameStateManager gsm*/) {
        super(/*gsm*/);
        cam.setToOrtho(false, TankGame.WIDTH, TankGame.HEIGHT);
        bg = new Texture("bg.png");

        // init box2d world
        Box2D.init();
        world = new World(new Vector2(0, -50f), true);
        debugRenderer = new Box2DDebugRenderer();

        gameSprites = new ArrayList<GameSprite>();
        gameSprites.add(new Tank(world, 500, 150));

        ground = new Ground(world, 10, 30, 150, 10);
    }

    public static void fire(int x, int y) {
        System.out.println("FIRING " + x + "-" + y);
        gameSprites.add(((Tank)gameSprites.get(0)).fireProjectile(world, x, y));
    }

    @Override
    protected void handleInput() {
        if(Gdx.input.justTouched()) {
            System.out.println("FIRE!");
            //gameSprites.add(((Tank)gameSprites.get(0)).fireProjectile(world, Gdx.input.getX(), Gdx.input.getY()));
            int x = Gdx.input.getX();
            int y = Gdx.input.getY();
            fire(x,y);
            Integer[] send = { x, y };
            TankGame.getBluetooth().writeObject(send);
            //TankGame.getBluetooth().writeObject("FIRE");
        }

        // for testing purposes
        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            GameStateManager.getGsm().push(new MenuState(/*gsm*/));
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            ((Tank)gameSprites.get(0)).drive(new Vector2(-50f, -5f));
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            ((Tank)gameSprites.get(0)).drive(new Vector2(50f, -5f));
        }

    }

    @Override
    public void update(float dt) {
        handleInput();
        /*for (GameSprite gs : gameSprites) {
            gs.update();
        }*/
        /*for (Iterator<GameSprite> it = gameSprites.iterator(); it.hasNext();) {
            it.next().update();
        }*/
        for (int i = 0; i < gameSprites.size(); i++) {
            gameSprites.get(i).update();
        }
    }

    @Override
    public void render(SpriteBatch sb, PolygonSpriteBatch psb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        //sb.draw(bg, 0,0, 1280, 720);
        /*for (Iterator<GameSprite> it = gameSprites.iterator(); it.hasNext();) {
            it.next().draw(sb);
        }*/
        for (int i = 0; i < gameSprites.size(); i++) {
            gameSprites.get(i).draw(sb);
        }
        /*for (GameSprite gs : gameSprites) {
            gs.draw(sb);
        }*/
        sb.end();

        psb.begin();
        ground.draw(psb);
        psb.end();

        // box-2d
        //debugRenderer.render(world, cam.combined);
        world.step(1/60f, 6, 2);
    }

    @Override
    public void dispose() {
        bg.dispose();
        world.dispose();
        debugRenderer.dispose();
        /*for (GameSprite gs : gameSprites) {
            gs.dispose();
        }*/
        for (Iterator<GameSprite> it = gameSprites.iterator(); it.hasNext();) {
            it.next().dispose();
        }
    }
}

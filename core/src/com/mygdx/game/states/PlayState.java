package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.BTInterface;
import com.mygdx.game.TankGame;
import com.mygdx.game.sprites.GUI;
import com.mygdx.game.sprites.GameSprite;
import com.mygdx.game.sprites.Ground;
import com.mygdx.game.sprites.Projectile;
import com.mygdx.game.sprites.Tank;

import java.util.ArrayList;

public class PlayState extends State {
    private Texture bg;
    private ArrayList<GameSprite> gameSprites;
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private Ground ground;
    private BTInterface btInterface;
    private GUI gui;

    public PlayState(GameStateManager gsm) {
        super(gsm);
        cam.setToOrtho(false, TankGame.WIDTH, TankGame.HEIGHT);
        bg = new Texture("bg.png");

        // init box2d world
        Box2D.init();
        world = new World(new Vector2(0, -50f), true);
        debugRenderer = new Box2DDebugRenderer();

        gameSprites = new ArrayList<GameSprite>();
        gameSprites.add(new Tank(world, 500, 170));

        ground = new Ground(world);


        // test simple gui
        gui = new GUI((Tank)gameSprites.get(0));

    }

    public void onConnected(boolean isHost) {
        System.out.println("Connected!");
    }

    public void onDisconnect() {
        System.out.println("Disconnected!");
    }

    public void setBluetoothInterface(BTInterface btInterface) {
        this.btInterface = btInterface;
    }

    @Override
    protected void handleInput() {
        /*
        if(Gdx.input.justTouched()) {
            System.out.println("FIRE!");
            gameSprites.add(((Tank)gameSprites.get(0)).fireProjectile(world, Gdx.input.getX(), Gdx.input.getY()));
        }

        // for testing purposes
        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            gsm.push(new MenuState(gsm));
        }
        */
    }

    @Override
    public void update(float dt) {
        handleInput();
        for (GameSprite gs : gameSprites) {
            gs.update();
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        gui.draw(sb);
        //sb.draw(bg, 0,0, 1280, 720);
        for (GameSprite gs : gameSprites) {
            gs.draw(sb);
        }
        sb.end();

        // box-2d
        debugRenderer.render(world, cam.combined);
        world.step(1/60f, 6, 2);
    }

    @Override
    public void dispose() {
        bg.dispose();
        world.dispose();
        gui.dispose();
        debugRenderer.dispose();
        for (GameSprite gs : gameSprites) {
            gs.dispose();
        }
    }
}

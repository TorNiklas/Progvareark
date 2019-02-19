package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.TankGame;
import com.mygdx.game.Terrain;
import com.mygdx.game.sprites.GameSprite;
import com.mygdx.game.sprites.Ground;
import com.mygdx.game.sprites.Tank;

import static com.badlogic.gdx.math.MathUtils.random;


public class PlayState extends State {
    private Texture bg;
    private GameSprite[] objects = new GameSprite[1];
    //private Tank tank;
    private World world;
    Box2DDebugRenderer debugRenderer;
    private Ground ground;


    public PlayState(GameStateManager gsm) {
        super(gsm);
        cam.setToOrtho(false, TankGame.WIDTH, TankGame.HEIGHT);
        bg = new Texture("bg.png");

        objects[0] = new Tank();
        //tank = new Tank();

        world = new World(new Vector2(0, -10), true);
        debugRenderer = new Box2DDebugRenderer();

        ground = new Ground(world);

    }

    @Override
    protected void handleInput() {
        if(Gdx.input.justTouched()) {

        }
    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        //sb.draw(bg, 0,0, 1280, 720);
        for (GameSprite gs : objects) {
            gs.draw(sb);
        }
        //sb.draw(tank.getTexture(), tank.getPosition().x, tank.getPosition().y);
        sb.draw(ground.getTexture(), ground.getPosition().x, ground.getPosition().y);
        sb.end();

        //debugRenderer.render(world, cam.combined);
        world.step(Gdx.graphics.getDeltaTime(), 6, 2);

    }

    @Override
    public void dispose() {
        bg.dispose();
        for (GameSprite gs : objects) {
            gs.dispose();
        }
    }
}

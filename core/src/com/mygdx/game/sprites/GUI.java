package com.mygdx.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.game.MyInputListener;
import com.mygdx.game.TankGame;

public class GUI {
    // sprites
    private Sprite statusBar;

    // buttons
    private Image moveLeft;
    private Image moveRight;
    private Image fire;
    private Stage stage;
    private Tank tank;

    public GUI(Tank tank) {
        statusBar = new Sprite(new Texture("statusBar.png"));
        statusBar.setSize(TankGame.WIDTH, 75);

        // mby clean this up
        moveLeft = new Image(new Texture("moveLeft.png"));
        moveRight = new Image(new Texture("moveRight.png"));
        fire = new Image(new Texture("fire.png"));

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        stage.addActor(moveLeft);
        stage.addActor(moveRight);
        stage.addActor(fire);

        // set pos
        moveLeft.setPosition(TankGame.WIDTH/2 - fire.getWidth()/2 - 200, 0);
        moveRight.setPosition(TankGame.WIDTH/2 - fire.getWidth()/2 + 200, 0);
        fire.setPosition(TankGame.WIDTH/2 - fire.getWidth()/2, 0);

        this.tank = tank;
        handleInput();
    }

    // move this elsewhere mby
    public void handleInput() {
        moveLeft.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("move left");
                tank.moveLeft();
                return true;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("stop");
                tank.stop();
            }
        });

        moveRight.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("move right");
                tank.moveRight();
                return true;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("stop");
                tank.stop();
            }
        });

        fire.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("Fire");

                return true;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
            }
        });
    }

    public void draw(SpriteBatch batch) {
        statusBar.draw(batch);
        moveLeft.draw(batch, 1f);
        moveRight.draw(batch, 1f);
        fire.draw(batch, 1f);
    }

    public void dispose() {
        statusBar.getTexture().dispose();
    }
}

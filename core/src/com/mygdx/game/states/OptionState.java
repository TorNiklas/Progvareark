package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.TankGame;
import com.mygdx.game.sprites.Tank;


public class OptionState extends State {
    private Texture bg;
    private Texture optionTitle;
    private Image soundBtn;
    private Image surrenderBtn;
    private Image homeBtn;
    private Image returnToGameBtn;
    private Stage stage;

    public OptionState(boolean fromMenuState) {
        super();
        cam.setToOrtho(false, TankGame.WIDTH, TankGame.HEIGHT);
        bg = new Texture("bg.png");

        optionTitle = new Texture("optionTitle.png");

        soundBtn = new Image(new Texture("volumeBtn.png"));
        soundBtn.setPosition(cam.position.x - soundBtn.getWidth()/2, 400);


        stage = new Stage(new ScreenViewport());
        stage.addActor(soundBtn);

        if(fromMenuState){
            homeBtn = new Image(new Texture("homeBtn.png"));
            homeBtn.setSize(homeBtn.getWidth(), homeBtn.getHeight());
            homeBtn.setPosition(cam.position.x - homeBtn.getWidth()/2,300);
            stage.addActor(homeBtn);
        }

        if(!fromMenuState){
            surrenderBtn = new Image(new Texture("surrenderBtn.png"));
            surrenderBtn.setPosition(cam.position.x - surrenderBtn.getWidth()/2,  300);
            stage.addActor(surrenderBtn);

            returnToGameBtn = new Image(new Texture("return.png"));
            returnToGameBtn.setSize(returnToGameBtn.getWidth(), returnToGameBtn.getHeight());
            returnToGameBtn.setPosition(cam.position.x - returnToGameBtn.getWidth()/2, 200);
            stage.addActor(returnToGameBtn);
        }

        Gdx.input.setInputProcessor(stage);


        soundBtn.addListener(new InputListener(){
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("Volume changed");
                return false;
            }


        });

        if(fromMenuState){
            homeBtn.addListener(new InputListener() {
                public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                    System.out.println("Home");
                    GameStateManager.getGsm().set(new MenuState());
                    return true;
                }
            });
        }
        if(!fromMenuState){
            returnToGameBtn.addListener(new InputListener() {
                public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                    System.out.println("Home");
                    GameStateManager.getGsm().set(new MenuState());
                    return true;
                }
            });
            surrenderBtn.addListener(new InputListener() {
                public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                    System.out.println("Surrender");
                    return false;
                }
            });
        }

    }

    @Override
    public void handleInput() {

    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb, PolygonSpriteBatch psb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(bg, 0,0, 1280, 720);
        sb.draw(optionTitle, cam.position.x - optionTitle.getWidth()/2, cam.position.y + optionTitle.getHeight()*1.5f);
        sb.end();

        // draw stage actors
        stage.act();
        stage.draw();
    }


    @Override
    public void dispose() {
        bg.dispose();
        optionTitle.dispose();
    }
}

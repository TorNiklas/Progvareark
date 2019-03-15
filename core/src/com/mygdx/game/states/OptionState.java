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
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.game.TankGame;
import com.mygdx.game.sprites.Tank;


public class OptionState extends State {
    private Texture bg;
    private Texture optionTitle;
    private Image soundBtn;
    private Image surrenderBtn;
    private Image homeBtn;
    private Stage stage;

    public OptionState(boolean fromMenuState) {
        super();
        cam.setToOrtho(false, TankGame.WIDTH, TankGame.HEIGHT);
        bg = new Texture("bg.png");

        optionTitle = new Texture("optionTitle.png");

        soundBtn = new Image(new Texture("volumeBtn.png"));
        surrenderBtn = new Image(new Texture("surrenderBtn.png"));

        soundBtn.setSize(surrenderBtn.getWidth()/4, surrenderBtn.getHeight()/4);
        soundBtn.setPosition(cam.position.x - soundBtn.getWidth()/2, cam.position.y);
        surrenderBtn.setSize(surrenderBtn.getWidth()/4, surrenderBtn.getHeight()/4);
        surrenderBtn.setPosition(cam.position.x - surrenderBtn.getWidth()/2, cam.position.y - surrenderBtn.getHeight());


        stage = new Stage(new StretchViewport(TankGame.WIDTH, TankGame.HEIGHT));
        stage.addActor(soundBtn);
        stage.addActor(surrenderBtn);

        if(fromMenuState){
            homeBtn = new Image(new Texture("homeBtn.png"));
            homeBtn.setSize(homeBtn.getWidth()/5, homeBtn.getHeight()/5);
            homeBtn.setPosition(TankGame.WIDTH - homeBtn.getWidth()*1.5f,TankGame.HEIGHT - homeBtn.getHeight()*1.5f);
            stage.addActor(homeBtn);
        }

        Gdx.input.setInputProcessor(stage);


        soundBtn.addListener(new InputListener(){
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("Volume changed");
                return false;
            }


        });

        surrenderBtn.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("Surrender");
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
        sb.draw(optionTitle, cam.position.x - optionTitle.getWidth()/2, cam.position.y + optionTitle.getHeight());
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

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


public class OptionState extends State {
    private Texture bg;
    private Texture soundTexture;
    private Texture surrenderTexture;
    private Texture homeTexture;
    private Texture optionTitle;
    private Image soundBtn;
    private Image surrenderBtn;
    private Image homeBtn;
    private Stage stage;

    public OptionState(/*GameStateManager gsm*/) {
        super();
        cam.setToOrtho(false, TankGame.WIDTH, TankGame.HEIGHT);

        bg = new Texture("bg.png");

        optionTitle = new Texture("optionTitle.png");

        soundBtn = new Image(new Texture("volumeBtn.png"));
        surrenderBtn = new Image(new Texture("surrenderBtn.png"));
        homeBtn = new Image(new Texture("homeBtn.png"));

        soundBtn.setSize(surrenderBtn.getWidth()/4, surrenderBtn.getHeight()/4);
        soundBtn.setPosition(cam.position.x - soundBtn.getWidth()/2, cam.position.y);
        surrenderBtn.setSize(surrenderBtn.getWidth()/4, surrenderBtn.getHeight()/4);
        surrenderBtn.setPosition(cam.position.x - surrenderBtn.getWidth()/2, cam.position.y - surrenderBtn.getHeight());
        //homeBtn.setSize(homeBtn.getWidth()/5, homeBtn.getHeight()/5);
        homeBtn.setPosition(50,500);

        stage = new Stage(new ScreenViewport());

        stage.addActor(soundBtn);
        stage.addActor(surrenderBtn);
        stage.addActor(homeBtn);

        final GameStateManager gsm = GameStateManager.getGsm();

        Gdx.input.setInputProcessor(stage);
        homeBtn.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("Home");
                gsm.set(new MenuState());
                return true;
            }
        });

        /*
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
        */

    }

    @Override
    protected void handleInput() {
        if(Gdx.input.justTouched()) {
            System.out.println("yayett");
            GameStateManager.getGsm().set(new MenuState());
        }
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
        //sb.draw(optionTitle, cam.position.x - optionTitle.getWidth()/2, cam.position.y + optionTitle.getHeight());

        // buttons
        //surrenderBtn.draw(sb, 1f);
        //soundBtn.draw(sb, 1f);
        homeBtn.draw(sb, 1f);
        sb.end();

        //stage.act();
        //stage.draw();
    }


    @Override
    public void dispose() {
        bg.dispose();
        optionTitle.dispose();

    }
}

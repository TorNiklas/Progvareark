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
import com.mygdx.game.AssetHandler;
import com.mygdx.game.TankGame;
import com.mygdx.game.sprites.Tank;


public class OptionState extends State {
    private Image bg;
    private Image optionTitle;
    private Image volumeOn;
    private Image volumeOff;
    private Image surrenderBtn;
    private Image homeBtn;
    private Image returnToGameBtn;
    private Stage stage;
    private AssetHandler assetHandler;

    public OptionState(boolean fromMenuState) {
        super();
        cam.setToOrtho(false, TankGame.WIDTH, TankGame.HEIGHT);

        // set asset handler
        assetHandler = ((TankGame)Gdx.app.getApplicationListener()).assetHandler;

        bg = new Image((Texture) assetHandler.manager.get(assetHandler.bgPath));
//        soundBtn.setPosition(cam.position.x - soundBtn.getWidth()/2, 400);

        optionTitle = new Image((Texture) assetHandler.manager.get(assetHandler.optionsTitlePath));
        optionTitle.setSize(optionTitle.getWidth(), optionTitle.getHeight());
        optionTitle.setPosition(cam.position.x - optionTitle.getWidth()/2, 500+optionTitle.getHeight()/4);

        volumeOn = new Image((Texture) assetHandler.manager.get(assetHandler.volumeOnBtnPath));
        volumeOn.setSize(volumeOn.getWidth(), volumeOn.getHeight());
        volumeOn.setPosition(cam.position.x - volumeOn.getWidth()/2, 400);

        volumeOff = new Image((Texture) assetHandler.manager.get(assetHandler.volumeOffBtnPath));
        volumeOff.setSize(volumeOff.getWidth(), volumeOff.getHeight());
        volumeOff.setPosition(cam.position.x - volumeOff.getWidth()/2, 400);

        stage = new Stage(new StretchViewport(TankGame.WIDTH, TankGame.HEIGHT, cam));
        stage.addActor(bg);
        stage.addActor(optionTitle);
        stage.addActor(volumeOff);
        stage.addActor(volumeOn);

        //Volume is on by default
        if(TankGame.music_level1.getVolume() > 0){
            volumeOn.setVisible(true);
            volumeOff.setVisible(false);
        }
        //Volume is on by default
        if(TankGame.music_level1.getVolume() == 0f){
            volumeOn.setVisible(false);
            volumeOff.setVisible(true);
        }



        if(fromMenuState){
            homeBtn = new Image((Texture) assetHandler.manager.get(assetHandler.homeBtnPath));
            homeBtn.setSize(homeBtn.getWidth(), homeBtn.getHeight());
            homeBtn.setPosition(cam.position.x - homeBtn.getWidth()/2,300);
            stage.addActor(homeBtn);
        }

        // not used?
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

        volumeOn.addListener(new InputListener(){
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("Mute");
                volumeOn.setVisible(false);
                volumeOff.setVisible(true);
                TankGame.music_level1.setVolume(0f);
                TankGame.isMuted = true;
                return false;
            }
        });

        volumeOff.addListener(new InputListener(){
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("Unmute");
                volumeOn.setVisible(true);
                volumeOff.setVisible(false);
                TankGame.music_level1.setVolume(TankGame.volume);
                TankGame.isMuted = false;
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
        /*if(!fromMenuState){
            returnToGameBtn.addListener(new InputListener() {
                public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                    System.out.println("Return to game");
                    State prevState = GameStateManager.getGsm().peek();

                    GameStateManager.getGsm().push(prevState); //set(new MenuState());
                    return true;
                }
            });
            surrenderBtn.addListener(new InputListener() {
                public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                    System.out.println("Surrender");
                    return false;
                }
            });
        }*/

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
        //sb.draw(bg, 0,0, 1280, 720);
        //sb.draw(optionTitle, cam.position.x - optionTitle.getWidth()/2, 500+optionTitle.getHeight()/4);
        sb.end();

        // draw stage actors
        stage.act();
        stage.draw();
    }


    @Override
    public void dispose() {
        //bg.dispose();
        //optionTitle.dispose();
    }
}

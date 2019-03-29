package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
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



public class TutorialState extends State {
    private Texture bg;
    //private Texture optionTitle;
    private Image homeBtn;
    private Stage stage;
    private BitmapFont tutorialText;
    private String lorem;

    public TutorialState(boolean fromMenuState) {
        super();
        cam.setToOrtho(false, TankGame.WIDTH, TankGame.HEIGHT);
        bg = new Texture("bg.png");

        //optionTitle = new Texture("optionTitle.png");
        stage = new Stage(new ScreenViewport());

        if(fromMenuState){
            homeBtn = new Image(new Texture("homeBtn.png"));
            homeBtn.setSize(homeBtn.getWidth(), homeBtn.getHeight());
            homeBtn.setPosition(cam.position.x - homeBtn.getWidth(),TankGame.HEIGHT - homeBtn.getHeight());
            stage.addActor(homeBtn);
        }

        Gdx.input.setInputProcessor(stage);

        if(fromMenuState){
            homeBtn.addListener(new InputListener() {
                public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                    System.out.println("Home");
                    GameStateManager.getGsm().set(new MenuState());
                    return true;
                }
            });
        }

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/arial.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 18;
        tutorialText = generator.generateFont(parameter);
        generator.dispose();

        lorem = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur pretium eu diam in dictum. Phasellus non pellentesque magna, interdum laoreet purus. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi consectetur velit id varius accumsan. Nam lobortis ligula in dolor scelerisque, in molestie elit tempus. Aliquam viverra eget neque et dapibus. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Suspendisse placerat ut nisl nec tristique. Curabitur pretium ante eu orci volutpat, quis commodo ex hendrerit. Ut vel tortor dignissim, vestibulum mi non, vulputate tellus. Ut vel turpis urna. Nullam sagittis molestie cursus.";
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
        //sb.draw(optionTitle, cam.position.x - optionTitle.getWidth()/2, 500+optionTitle.getHeight()/4);
        tutorialText.draw(sb, lorem, 0, 650, TankGame.WIDTH * 0.8f , 8, true);
        sb.end();

        // draw stage actors
        stage.act();
        stage.draw();


    }


    @Override
    public void dispose() {
        bg.dispose();
        //optionTitle.dispose();
    }
}

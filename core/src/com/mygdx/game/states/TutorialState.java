package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.game.TankGame;

public class TutorialState extends State {
    private Texture bg;
    private Texture tutorialGuiHelp;
    private Image homeBtn;
    private Stage stage;
    private BitmapFont tutorialFont;
    private String tutorialContent;

    public TutorialState(boolean fromMenuState) {
        super();
        bg = new Texture("bg.png");

        tutorialGuiHelp = new Texture("tutorial_bottom_gui.PNG");

        stage = new Stage(new StretchViewport(TankGame.WIDTH, TankGame.HEIGHT, cam));

        if(fromMenuState){
            homeBtn = new Image(new Texture("homeBtn.png"));
            homeBtn.setSize(homeBtn.getWidth() *0.8f, homeBtn.getHeight() * 0.8f);
            homeBtn.setPosition(TankGame.WIDTH * 0.1f,TankGame.HEIGHT - (homeBtn.getHeight() + 10 ));
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
        tutorialFont = generator.generateFont(parameter);
        generator.dispose();

        FileHandle file = Gdx.files.internal("tutorialText.txt");
        tutorialContent = file.readString();
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
        sb.draw(tutorialGuiHelp, TankGame.WIDTH*0.2f, 10);
        tutorialFont.draw(sb,
                tutorialContent,
                TankGame.WIDTH * 0.1f,
                640,
                TankGame.WIDTH * 0.8f ,
                8,
                true);

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

    @Override
    public void onLoad() {

    }
}

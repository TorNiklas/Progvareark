package com.mygdx.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.game.MyInputListener;
import com.mygdx.game.TankGame;
import com.mygdx.game.states.PlayState;
import com.mygdx.game.states.State;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;


import static java.lang.Math.cos;
import static java.lang.StrictMath.sin;

public class GUI {
    // sprites
    private Image statusBar;

    // buttons
    private Stage stage;
    private Tank tank;

    Skin skin;
    private TextButton fireButton;
    private TextButton increaseElevation;
    private TextButton decreaseElevation;

    private TextButton leftBtn;
    private TextButton rightBtn;

    private ProgressBar healthBar;
    private ProgressBar energyBar;

    private long timer;
    private BitmapFont font;

    public GUI(Tank tank, OrthographicCamera cam, int height) {
        statusBar = new Image(new Texture("statusBar.png"));
        statusBar.setSize(TankGame.WIDTH, height);

        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        leftBtn = new TextButton("<--", skin);
        leftBtn.setSize(200, height - 75);
        leftBtn.setPosition(10, 10);

        rightBtn = new TextButton("-->", skin);
        rightBtn.setSize(200, height - 75);
        rightBtn.setPosition(leftBtn.getWidth() + 20, 10);

        fireButton = new TextButton("Fire!", skin);
        fireButton.setSize(200,height - 75);
        fireButton.setPosition(950, 10);

        increaseElevation = new TextButton("+", skin);
        increaseElevation.setSize(100,height - 75);
        increaseElevation.setPosition(fireButton.getX()+fireButton.getWidth()+10, 10);

        decreaseElevation = new TextButton("-", skin);
        decreaseElevation.setSize(100,height - 75);
        decreaseElevation.setPosition(fireButton.getX()-fireButton.getWidth()/2-10, 10);

        // create energy bar
        energyBar = generateProgressBar(20, height-58, 390, 30, Color.DARK_GRAY, Color.GOLD);

        // create health bar
        healthBar = generateProgressBar(855, height-58, 390, 30, Color.FIREBRICK, Color.GREEN);
        healthBar.setValue(75f);

        timer = System.currentTimeMillis();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/arial.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 40;
        font = generator.generateFont(parameter);
        generator.dispose();

        stage = new Stage(new StretchViewport(1280, 720, cam));
        stage.addActor(statusBar);
        stage.addActor(leftBtn);
        stage.addActor(rightBtn);
        stage.addActor(fireButton);
        stage.addActor(increaseElevation);
        stage.addActor(decreaseElevation);
        stage.addActor(energyBar);
        stage.addActor(healthBar);

        Gdx.input.setInputProcessor(stage);

        this.tank = tank;
        handleInput();
    }

    // move this elsewhere mby
    public void handleInput() {
        //Button event handlers, should probably not be here
        leftBtn.addListener(new ClickListener() {
            /*@Override
            public boolean handle(Event event) {
                System.out.println("Pressed left button");
                //((Tank)gameSprites.get(0)).drive(new Vector2(-50f, -5f));
                return true;
            }*/

            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("touch down - left");
                tank.setMoveLeft(true);
                return true;
            }

            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("touch up - left");
                tank.setMoveLeft(false);
            }

        });

        rightBtn.addListener(new ClickListener() {
            /*@Override
            public boolean handle(Event event) {
                System.out.println("Pressed right button");
                ((Tank)gameSprites.get(0)).drive(new Vector2(50f, -5f));
                return true;
            }*/

            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("touch down - right");
                tank.setMoveRight(true);
                return true;
            }

            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("touch up - right");
                tank.setMoveRight(false);
            }
        });

        fireButton.addListener(new ClickListener() {
            public void clicked(InputEvent e, float x, float y) {
                tank.fireProjectile();//fireFromPool(pos, velocity);

                // Integer[] send = { x, y };
                // TankGame.getBluetooth().writeObject(send);
            }
        });

        increaseElevation.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                tank.setIncrease(true);
                return true;
            }
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                tank.setIncrease(false);
            }
        });

        decreaseElevation.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                tank.setDecrease(true);
                return true;
            }
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                tank.setDecrease(false);
            }
        });
    }

    private ProgressBar generateProgressBar(int x, int y, int width, int height, Color bgColor, Color barColor) {
        // background bar pixmap
        Pixmap bgPixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        bgPixmap.setColor(bgColor);
        bgPixmap.fill();

        // full bar pixmap
        Pixmap fullPixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        fullPixmap.setColor(barColor);
        fullPixmap.fill();

        // empty pixmap
        Pixmap emptyPixmap = new Pixmap(0, height, Pixmap.Format.RGBA8888);
        emptyPixmap.setColor(barColor);
        emptyPixmap.fill();

        // texture region drawables
        TextureRegionDrawable bgDrawable = new TextureRegionDrawable(new TextureRegion(new Texture(bgPixmap)));
        TextureRegionDrawable fullDrawable = new TextureRegionDrawable(new TextureRegion(new Texture(fullPixmap)));
        TextureRegionDrawable emptyDrawable = new TextureRegionDrawable(new TextureRegion(new Texture(emptyPixmap)));
        bgPixmap.dispose();
        fullPixmap.dispose();
        emptyPixmap.dispose();

        // set up style
        ProgressBar.ProgressBarStyle progressBarStyle = new ProgressBar.ProgressBarStyle();
        progressBarStyle.background = bgDrawable;
        progressBarStyle.knobBefore = fullDrawable;
        progressBarStyle.knob = emptyDrawable;

        // create energy bar
        ProgressBar progressBar = new ProgressBar(0.0f, 100.0f, 0.1f, false, progressBarStyle);
        progressBar.setValue(100f);
        progressBar.setAnimateDuration(0.05f);
        progressBar.setBounds(x, y, width, height);

        return progressBar;
    }

    private long getTime(){
        long diff = 45 - ((System.currentTimeMillis()-timer)/1000);
        if(diff > 0) {
            return diff;
        }
        return 0;
    }

    private void setPlayable(Boolean bool){
        Array<Actor> stageActors = stage.getActors();
        if(bool){
            for (Actor a: stageActors
            ) {
                a.setTouchable(Touchable.disabled);
            }
        } else {
            for (Actor a: stageActors
            ) {
                a.setTouchable(Touchable.enabled);
            }
        }
    }

    public void update() {
        energyBar.setValue(tank.getEnergy());

        if(getTime() == 0) {
            setPlayable(true);
        }
    }

    public void draw(SpriteBatch batch) {
        stage.act();
        stage.draw();

        batch.begin();
        font.draw(batch, "Time: " + getTime(), TankGame.WIDTH - 175, 700);
        batch.end();

    }

    public void dispose() {
    }
}

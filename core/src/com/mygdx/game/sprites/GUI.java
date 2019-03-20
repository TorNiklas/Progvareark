package com.mygdx.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.game.TankGame;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.mygdx.game.states.PlayState;
import com.mygdx.game.states.GameStateManager;
import com.mygdx.game.states.MenuState;

public class GUI {
    // sprites
    private Image statusBar;

    // buttons
    private Stage stage;
    private PlayState state;
    private Tank tank;
    private Tank enemyTank;

    Skin skin;
    private TextButton fireButton;
    private TextButton increaseElevation;
    private TextButton decreaseElevation;

    private TextButton leftBtn;
    private TextButton rightBtn;

    private TextButton nextAmmoBtn;
    private TextButton prevAmmoBtn;
    private Image ammoImage;

    private ProgressBar healthBar;
    private ProgressBar tankHealthBar;
    private ProgressBar tankFirePower;
    //private ProgressBar tankHealthBar;
    private ProgressBar energyBar;
    private Image volumeOn;
    private Image volumeOff;
    private Image surrender;

    private long timer;
    private BitmapFont font;
    private int height;

    public GUI(PlayState state, int height) {
        this.state = state;
        this.height = height;
        statusBar = new Image(new Texture("statusBar.png"));
        statusBar.setSize(TankGame.WIDTH, height);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/arial.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 40;
        parameter.shadowColor = Color.BLACK;
        parameter.shadowOffsetX = 3;
        parameter.shadowOffsetY = 3;
        font = generator.generateFont(parameter);
        generator.dispose();

        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        leftBtn = new TextButton("<--", skin);
        leftBtn.setSize(200, height - 75);
        leftBtn.setPosition(10, 10);

        rightBtn = new TextButton("-->", skin);
        rightBtn.setSize(200, height - 75);
        rightBtn.setPosition(leftBtn.getWidth() + 20, 10);

        ammoImage = new Image(new Texture("ammo-standard.png"));
        ammoImage.setSize(100, 100);
        ammoImage.setPosition(TankGame.WIDTH/2 - 15, ammoImage.getHeight()/2 + 35, 1);

        nextAmmoBtn = new TextButton("-->", skin);
        nextAmmoBtn.setSize(50, 100);
        nextAmmoBtn.setPosition(ammoImage.getX() + ammoImage.getWidth()/2 + nextAmmoBtn.getWidth() + 10, ammoImage.getY());

        prevAmmoBtn = new TextButton("<--", skin);
        prevAmmoBtn.setSize(50, 100);
        prevAmmoBtn.setPosition(ammoImage.getX() - prevAmmoBtn.getWidth() - 10, ammoImage.getY());

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
        energyBar = generateProgressBar(20, height-58, 390, 30, 100f, 100f, Color.DARK_GRAY, Color.GOLD);

        // create health bar
        healthBar = generateProgressBar(855, height-58, 390, 30, 100f, 100f, Color.FIREBRICK, Color.GREEN);

        // create tank health bar
        tankHealthBar = generateProgressBar(0, 0, 35, 5, 100f, 100f, Color.FIREBRICK, Color.GREEN);
        //tankHealthBar = generateProgressBar(0, 0, 35, 5, Color.FIREBRICK, Color.GREEN);

        // create tank fire power bar
        tankFirePower = generateProgressBar(0, 0, 60, 10, 0f, 150f, Color.BLACK, Color.RED);
        tankFirePower.setVisible(false);

        timer = System.currentTimeMillis();
        //create options menu button
        volumeOn = new Image(new Texture("volumeOnBtn.png"));
        volumeOn.setName("volumeOn");
        volumeOn.setSize(volumeOn.getWidth(), volumeOn.getHeight());
        volumeOn.setPosition(TankGame.WIDTH - volumeOn.getWidth()*2, TankGame.HEIGHT - volumeOn.getHeight()*2.25f);

        volumeOff = new Image(new Texture("volumeOffBtn.png"));
        volumeOff.setName("volumeOff");
        volumeOff.setSize(volumeOff.getWidth(), volumeOff.getHeight());
        volumeOff.setPosition(TankGame.WIDTH - volumeOff.getWidth()*2, TankGame.HEIGHT - volumeOff.getHeight()*2.25f);

        surrender = new Image(new Texture("surrenderBtn.png"));
        surrender.setName("surrender");
        surrender.setSize(surrender.getWidth(), surrender.getHeight());
        surrender.setPosition(TankGame.WIDTH - surrender.getWidth()*2, TankGame.HEIGHT - surrender.getHeight()*3.5f);

        stage = new Stage(new StretchViewport(1280, 720, state.getCamera()));
        stage.addActor(statusBar);
        stage.addActor(leftBtn);
        stage.addActor(rightBtn);
        stage.addActor(ammoImage);
        stage.addActor(prevAmmoBtn);
        stage.addActor(nextAmmoBtn);
        stage.addActor(fireButton);
        stage.addActor(increaseElevation);
        stage.addActor(decreaseElevation);
        stage.addActor(energyBar);
        stage.addActor(healthBar);
        stage.addActor(tankHealthBar);
        stage.addActor(tankFirePower);
        stage.addActor(volumeOn);
        stage.addActor(volumeOff);
        stage.addActor(surrender);

        //Volume is on by default
        if(TankGame.music_level1.getVolume() > 0f){
            volumeOn.setVisible(true);
            volumeOff.setVisible(false);
        }
        //Volume is on by default
        if(TankGame.music_level1.getVolume() == 0f){
            volumeOn.setVisible(false);
            volumeOff.setVisible(true);
        }

        Gdx.input.setInputProcessor(stage);

        tank = (Tank)state.getGameSprites().get(0);
        enemyTank = (Tank)state.getGameSprites().get(1);
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

        nextAmmoBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                tank.setActiveAmmoType(tank.getNextAmmo());
                switch (tank.getActiveAmmoType()) {
                    case STANDARD:
                        ammoImage.setDrawable(new SpriteDrawable(new Sprite(new Texture("ammo-standard.png"))));
                        break;
                    case SPREAD:
                        ammoImage.setDrawable(new SpriteDrawable(new Sprite(new Texture("ammo-spread.png"))));
                        break;
                }
            }
        });

        prevAmmoBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                tank.setActiveAmmoType(tank.getPrevAmmo());
                switch (tank.getActiveAmmoType()) {
                    case STANDARD:
                        ammoImage.setDrawable(new SpriteDrawable(new Sprite(new Texture("ammo-standard.png"))));
                        break;
                    case SPREAD:
                        ammoImage.setDrawable(new SpriteDrawable(new Sprite(new Texture("ammo-spread.png"))));
                        break;
                }
            }
        });

        fireButton.addListener(new ClickListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("power up");
                tankFirePower.setVisible(true);
                tank.setPoweringUp(true);
                return true;
            }

            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("fire");
                tankFirePower.setVisible(false);
                tank.setPoweringUp(false);
                tank.fireProjectile(tank.getActiveAmmoType());
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
                TankGame.music_level1.setVolume(0.2f);
                TankGame.isMuted = false;
                return false;
            }
        });

        final Dialog dialog = new Dialog("Warning", skin, "dialog") {
            public void result(Object obj) {
                if(obj.equals(true)){
                    GameStateManager.getGsm().set(new MenuState());

                }
                System.out.println("result "+obj);
            }
        };
        dialog.getBackground().setMinHeight(200);
        dialog.getBackground().setMinWidth(600);
        dialog.text("Are you sure you want to surrender and quit the game?");
        dialog.button("Yes", true); //sends "true" as the result
        dialog.button("No tank u", false); //sends "false" as the result

        surrender.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("Surrender");
                dialog.show(stage);

                return false;
            }
        });
    }

    private ProgressBar generateProgressBar(int x, int y, int width, int height, float startValue, float max, Color bgColor, Color barColor) {
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
        ProgressBar progressBar = new ProgressBar(0.0f, max, 0.1f, false, progressBarStyle);
        progressBar.setValue(startValue);
        progressBar.setAnimateDuration(0.05f);
        progressBar.setBounds(x, y, width, height);

        return progressBar;
    }

    private long getTime(){
        long diff = 60 - ((System.currentTimeMillis()-timer)/1000);
        if(diff > 0) {
            return diff;
        }
        return 0;
    }

    private void setPlayable(Boolean bool){
        Array<Actor> stageActors = stage.getActors();
        for (Actor a: stageActors) {
            if(a.getName() != null) {
                if ((a.getName().equals("surrender") || a.getName().equals("volumeOn") || a.getName().equals("volumeOff"))) {
                    return;
                }
            }
            if(bool) {
                a.setTouchable(Touchable.enabled);
            } else {
                a.setTouchable(Touchable.disabled);
            }
        }
    }

    public void update() {
        energyBar.setValue(tank.getEnergy());
        healthBar.setValue(tank.getHealth());

        Vector2 enemyTankPos = enemyTank.getPosition();
        tankHealthBar.setPosition(enemyTankPos.x - tankHealthBar.getWidth()/2 + enemyTank.getSprite().getWidth()/2, enemyTankPos.y + 20);
        tankHealthBar.setValue(enemyTank.getHealth());
        // TODO: fix rotation?
        //tankHealthBar.setRotation(tank.getSprite().getRotation());

        Vector2 tankPos = tank.getPosition();
        tankFirePower.setPosition(tankPos.x - tankFirePower.getWidth()/2 + tank.getSprite().getWidth()/2, tankPos.y + 30);
        tankFirePower.setValue(tank.getFirePower());

        if(tank.getAmmo() <= 0 && fireButton.getTouchable().equals(Touchable.enabled)) {
            //TODO: fix disabled style for button
            fireButton.setDisabled(true);
            fireButton.setTouchable(Touchable.disabled);
        }

        if(getTime() == 0) {
            setPlayable(false);
        }
    }

    public void draw(SpriteBatch batch) {
        stage.act();
        stage.draw();

        batch.begin();
        font.draw(batch, "Time: " + getTime(), TankGame.WIDTH - 175, 700);
        font.draw(batch, "Ammo: " + tank.getAmmo() + "/" + tank.getMaxAmmo(), TankGame.WIDTH/2 - 15, height-25, 0f, 1, false);
        batch.end();
    }

    public void dispose() {
    }
}

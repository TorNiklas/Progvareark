package com.mygdx.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.game.AssetHandler;
import com.mygdx.game.TankGame;
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
    private ImageButton fireButton;
    private ImageButton increaseElevation;
    private ImageButton decreaseElevation;

    private ImageButton leftBtn;
    private ImageButton rightBtn;

    private ImageButton nextAmmoBtn;
    private ImageButton prevAmmoBtn;

    private Image endGameV;
    private Image endGameD;
    private Image ammoImage;
    private Image enemyTurn;

    private ProgressBar healthBar;
    private ProgressBar tankHealthBar;
    private ProgressBar tankFirePower;
    private ProgressBar energyBar;
    private ImageButton volumeOn;
    private ImageButton volumeOff;
    private ImageButton surrender;

    Texture buttonSheet;
    Texture ammoTexture;

    private long timer;
    private BitmapFont font;
    private int height;

    private AssetHandler assetHandler;

    public GUI(PlayState state, int height) {
        this.state = state;
        this.height = height;

        // set asset handler
        assetHandler = ((TankGame)Gdx.app.getApplicationListener()).assetHandler;

        statusBar = new Image((Texture) assetHandler.manager.get(assetHandler.statusBarPath)); //new Image(new Texture("statusBar.png"));
        statusBar.setSize(TankGame.WIDTH, height);

        font = assetHandler.manager.get(assetHandler.fontPath); //generator.generateFont(parameter);

        skin = assetHandler.manager.get(assetHandler.skinJsonPath); // new Skin(Gdx.files.internal("skin/uiskin.json"));
        skin.get(TextButton.TextButtonStyle.class).font = font;

        buttonSheet = assetHandler.manager.get(assetHandler.guiButtonsPath);

        TextureRegionDrawable leftBtnTr = new TextureRegionDrawable(new TextureRegion(buttonSheet, 276,235, 176 ,185 ));
        TextureRegionDrawable rightBtnTr = new TextureRegionDrawable(new TextureRegion(buttonSheet, 276,20, 176 ,185 ));

        TextureRegionDrawable prevAmmoBtnTr = new TextureRegionDrawable(new TextureRegion(buttonSheet, 699,235, 176 ,187 ));
        TextureRegionDrawable nextAmmoBtnTr = new TextureRegionDrawable(new TextureRegion(buttonSheet, 699,20, 176 ,187 ));

        TextureRegionDrawable decreaseElevationTr = new TextureRegionDrawable(new TextureRegion(buttonSheet, 276,445, 176 ,187 ));
        TextureRegionDrawable increaseElevationTr = new TextureRegionDrawable(new TextureRegion(buttonSheet, 276,662, 176 ,187 ));

        TextureRegionDrawable fireBtnTr = new TextureRegionDrawable(new TextureRegion(buttonSheet, 926,239, 405 ,181 ));

        TextureRegionDrawable volumeOnTr = new TextureRegionDrawable(new TextureRegion(buttonSheet, 1106, 454, 176, 186));
        TextureRegionDrawable volumeOffTr = new TextureRegionDrawable(new TextureRegion(buttonSheet, 912,452, 176,186 ));
        TextureRegionDrawable surrenderTr = new TextureRegionDrawable(new TextureRegion(buttonSheet, 917,666, 176,186 ));

        leftBtn = new ImageButton(leftBtnTr);
        leftBtn.setSize(200, height - 75);
        leftBtn.setPosition(10, 10);

        rightBtn = new ImageButton(rightBtnTr);
        rightBtn.setSize(200, height - 75);
        rightBtn.setPosition(leftBtn.getWidth() + 10, 10);

        ammoTexture = assetHandler.manager.get(assetHandler.ammoStandardPath);
        ammoImage = new Image(ammoTexture);
        ammoImage.setSize(100, 100);
        ammoImage.setPosition(TankGame.WIDTH/2 - 15, ammoImage.getHeight()/2 + 45, 1);

        nextAmmoBtn = new ImageButton(nextAmmoBtnTr);
        nextAmmoBtn.setSize(50, 100);
        nextAmmoBtn.setPosition(ammoImage.getX() + ammoImage.getWidth()/2 + nextAmmoBtn.getWidth() + 10, ammoImage.getY());

        prevAmmoBtn = new ImageButton(prevAmmoBtnTr);
        prevAmmoBtn.setSize(50, 100);
        prevAmmoBtn.setPosition(ammoImage.getX() - prevAmmoBtn.getWidth() - 10, ammoImage.getY());

        fireButton = new ImageButton(fireBtnTr);
        fireButton.setSize(200,height - 75);
        fireButton.setPosition(950, 10);

        increaseElevation = new ImageButton(increaseElevationTr);
        increaseElevation.setSize(100,height - 75);
        increaseElevation.setPosition(fireButton.getX()+fireButton.getWidth()+10, 10);

        decreaseElevation = new ImageButton(decreaseElevationTr);
        decreaseElevation.setSize(100,height - 75);
        decreaseElevation.setPosition(fireButton.getX()-fireButton.getWidth()/2-10, 10);

        endGameV = new Image(new Texture("victory.png"));
        endGameV.setSize(600, 200);
        endGameV.setName("endGameV");
        endGameV.setPosition(TankGame.WIDTH/2-endGameV.getWidth()/2, TankGame.HEIGHT/2);

        endGameD = new Image(new Texture("defeat.png"));
        endGameD.setSize(600, 200);
        endGameV.setName("endGameD");
        endGameD.setPosition(TankGame.WIDTH/2-endGameD.getWidth()/2, TankGame.HEIGHT/2);

        enemyTurn = new Image(new Texture("enemyTurn.png"));
        enemyTurn.setPosition(TankGame.WIDTH/2-enemyTurn.getWidth()/2, TankGame.HEIGHT/1.25f);

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
        volumeOn = new ImageButton(volumeOnTr);
        volumeOn.setName("volumeOn");
        volumeOn.setSize(volumeOn.getWidth()/3, volumeOn.getHeight()/3);
        volumeOn.setPosition(TankGame.WIDTH - volumeOn.getWidth()*2, TankGame.HEIGHT - volumeOn.getHeight()*2.25f);

        volumeOff = new ImageButton(volumeOffTr);
        volumeOff.setName("volumeOff");
        volumeOff.setSize(volumeOff.getWidth()/3, volumeOff.getHeight()/3);
        volumeOff.setPosition(TankGame.WIDTH - volumeOff.getWidth()*2, TankGame.HEIGHT - volumeOff.getHeight()*2.25f);

        surrender = new ImageButton(surrenderTr);
        surrender.setName("surrender");
        surrender.setSize(surrender.getWidth()/3, surrender.getHeight()/3);
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
        stage.addActor(endGameV);
        stage.addActor(endGameD);
        stage.addActor(enemyTurn);
        endGameV.setVisible(false);
        endGameD.setVisible(false);
        enemyTurn.setVisible(false);


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
                leftBtn.getImage().setColor(Color.GRAY);
                tank.setMoveLeft(true);
                return true;
            }

            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("touch up - left");
                leftBtn.getImage().setColor(Color.WHITE);
                tank.setMoveLeft(false);
            }

        });

        rightBtn.addListener(new ClickListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("touch down - right");
                rightBtn.getImage().setColor(Color.GRAY);
                tank.setMoveRight(true);
                return true;
            }

            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("touch up - right");
                rightBtn.getImage().setColor(Color.WHITE);
                tank.setMoveRight(false);
            }
        });

        nextAmmoBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                tank.setActiveAmmoType(tank.getNextAmmo());
                switch (tank.getActiveAmmoType()) {
                    case STANDARD:
                        ammoTexture = assetHandler.manager.get(assetHandler.ammoStandardPath);
                        break;
                    case SPREAD:
                        ammoTexture = assetHandler.manager.get(assetHandler.ammoSpreadPath);
                        break;
                    case LASER:
                        ammoTexture = assetHandler.manager.get(assetHandler.ammoLaserPath);
                        break;
                    case AIRSTRIKE:
                        ammoTexture = assetHandler.manager.get(assetHandler.ammoAirstrikePath);
                        break;
                }
                ammoImage.setDrawable(new SpriteDrawable(new Sprite(ammoTexture)));

            }
        });

        prevAmmoBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                tank.setActiveAmmoType(tank.getPrevAmmo());
                switch (tank.getActiveAmmoType()) {
                    case STANDARD:
                        ammoTexture = assetHandler.manager.get(assetHandler.ammoStandardPath);
                        break;
                    case SPREAD:
                        ammoTexture = assetHandler.manager.get(assetHandler.ammoSpreadPath);
                        break;
                    case LASER:
                        ammoTexture = assetHandler.manager.get(assetHandler.ammoLaserPath);
                        break;
                    case AIRSTRIKE:
                        ammoTexture = assetHandler.manager.get(assetHandler.ammoAirstrikePath);
                        break;
                }
                ammoImage.setDrawable(new SpriteDrawable(new Sprite(ammoTexture)));
            }
        });

        fireButton.addListener(new ClickListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("power up");
                fireButton.getImage().setColor(Color.GRAY);
                tankFirePower.setVisible(true);
                tank.setPoweringUp(true);
                return true;
            }

            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("fire");
                tankFirePower.setVisible(false);
                tank.setPoweringUp(false);
                fireButton.getImage().setColor(Color.WHITE);
                tank.fireProjectile(tank.getActiveAmmoType());
            }
        });

        increaseElevation.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                switch (tank.getActiveAmmoType()){
                    case AIRSTRIKE:
                        tank.setIncreaseAirStrike(true);
                    default:
                        tank.setIncrease(true);
                }
                increaseElevation.getImage().setColor(Color.GRAY);

                return true;
            }
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                tank.setIncrease(false);
                tank.setIncreaseAirStrike(false);
                increaseElevation.getImage().setColor(Color.WHITE);
            }
        });

        decreaseElevation.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                switch (tank.getActiveAmmoType()){
                    case AIRSTRIKE:
                        tank.setDecreaseAirStrike(true);
                    default:
                        tank.setDecrease(true);

                }
                decreaseElevation.getImage().setColor(Color.GRAY);

                return true;
            }
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                tank.setDecrease(false);
                tank.setDecreaseAirStrike(false);
                decreaseElevation.getImage().setColor(Color.WHITE);
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
                TankGame.music_level1.setVolume(TankGame.volume);
                TankGame.isMuted = false;
                return false;
            }
        });

        ClickListener endListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameStateManager.getGsm().set(new MenuState());
            }
        };

        endGameV.addListener(endListener);
        endGameD.addListener(endListener);

        final Dialog dialog = new Dialog("Warning", skin, "dialog") {
            public void result(Object obj) {
                if(obj.equals(true)){
                    TankGame.getBluetooth().disconnect();
                    GameStateManager.getGsm().set(new MenuState());
                }
                System.out.println("result "+obj);
            }
        };
        dialog.getBackground().setMinHeight(200);
        dialog.getBackground().setMinWidth(600);
        dialog.text("Do you want to surrender game and go to main menu?");
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

    public long getTimer() {
        return timer;
    }

    public void setTimer(long timer) {
        this.timer = timer;
    }

    public void resetTimer() {
        timer = System.currentTimeMillis();
        timeLeft = 30;
    }

    private int timeLeft = 30;

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }

    public long getTime(){
        if (!TankGame.host) {
//            System.out.println(timeLeft);
            return timeLeft;
        }
        long diff = 30 - ((System.currentTimeMillis()-timer)/1000);
        if(diff > 0) {
            return diff;
        }
        return 0;
    }

    public void endSplash(boolean winner){
        if(winner){
            endGameV.setVisible(true);
        } else {
            endGameD.setVisible(true);
        }
    }

    private boolean playable = true;

    public void setPlayable(Boolean bool){
        if (bool != playable) {
            playable = bool;
            stage.cancelTouchFocus();
            showTurnGraphic();
            Array<Actor> stageActors = stage.getActors();
            for (Actor a : stageActors) {
                if (a.getName() != null) {
                    if (a.getName().equals("surrender") || a.getName().equals("volumeOn") || a.getName().equals("volumeOff") || a.getName().equals("endGameV") || a.getName().equals("endGameD")) {
                        return;
                    }
                }
                if (bool) {
                    a.setTouchable(Touchable.enabled);
                } else {
                    a.setTouchable(Touchable.disabled);
                }
            }
        }
    }

    private void showTurnGraphic() {
        enemyTurn.setVisible(!playable);
        if(!playable){
            increaseElevation.getImage().setColor(Color.GRAY);
            decreaseElevation.getImage().setColor(Color.GRAY);
            fireButton.getImage().setColor(Color.GRAY);
            leftBtn.getImage().setColor(Color.GRAY);
            rightBtn.getImage().setColor(Color.GRAY);
            nextAmmoBtn.getImage().setColor(Color.GRAY);
            prevAmmoBtn.getImage().setColor(Color.GRAY);
        } else {
            increaseElevation.getImage().setColor(Color.WHITE);
            decreaseElevation.getImage().setColor(Color.WHITE);
            fireButton.getImage().setColor(Color.WHITE);
            leftBtn.getImage().setColor(Color.WHITE);
            rightBtn.getImage().setColor(Color.WHITE);
            nextAmmoBtn.getImage().setColor(Color.WHITE);
            prevAmmoBtn.getImage().setColor(Color.WHITE);
        }
    }

    /*public void togglePlayable() {
        System.out.println("TOGGLING PLAYABILITY: " + !playable);
        setPlayable(!playable);


    }*/

    public void update() {
        energyBar.setValue(tank.getEnergy());
        healthBar.setValue(tank.getHealth());

        Vector2 enemyTankPos = enemyTank.getPosition();
        tankHealthBar.setPosition(enemyTankPos.x - tankHealthBar.getWidth()/2, enemyTankPos.y + 20);
        tankHealthBar.setValue(enemyTank.getHealth());
        // TODO: fix rotation?
        //tankHealthBar.setRotation(tank.getSprite().getRotation());

        Vector2 tankPos = tank.getPosition();
        tankFirePower.setPosition(tankPos.x - tankFirePower.getWidth()/2, tankPos.y + 20);
        tankFirePower.setValue(tank.getFirePower());

        if(tank.getAmmo() < tank.getActiveAmmoType().getCost() && fireButton.getTouchable().equals(Touchable.enabled)) {
            fireButton.setDisabled(true);
            fireButton.setTouchable(Touchable.disabled);
            fireButton.getImage().setColor(Color.GRAY);
        } else if (playable && tank.getAmmo() >= tank.getActiveAmmoType().getCost() && fireButton.getTouchable().equals(Touchable.disabled)) {
            fireButton.setDisabled(false);
            fireButton.setTouchable(Touchable.enabled);
            fireButton.getImage().setColor(Color.WHITE);
        }

        /*if(getTime() == 0) {
            setPlayable(false);
        }*/
    }

    public void draw(SpriteBatch batch) {
        stage.act();
        stage.draw();

        batch.begin();
        font.draw(batch, "Time: " + getTime(), TankGame.WIDTH - 175, 700);
        font.draw(batch, "Ammo: " + tank.getAmmo() + "/" + tank.getMaxAmmo(), TankGame.WIDTH/2 - 15, height-25, 0f, 1, false);
        font.draw(batch, "Cost: " + tank.getActiveAmmoType().getCost(), TankGame.WIDTH/2 - 15, height-190, 0f, 1, false);
        batch.end();
    }

    public void dispose() {
    }
}

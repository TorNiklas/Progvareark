package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.game.AssetHandler;
import com.mygdx.game.TankGame;

public class LoadingState extends State {

    private AssetHandler assetHandler;
    private ShapeRenderer shapeRenderer;
    private Image tankImg;
    private Stage stage;
    private float progress;

    public LoadingState() {
        // create shape renderer for simple progress bar
        shapeRenderer = new ShapeRenderer();

        // create image and action
        tankImg = new Image(new Texture("splash.png"));
        tankImg.setOrigin(tankImg.getWidth()/2, tankImg.getHeight()/2);
        tankImg.addAction(Actions.sequence(
                Actions.alpha(0),
                Actions.scaleTo(0.8f, 0.8f),
                Actions.parallel(
                        Actions.fadeIn(1f, Interpolation.pow2),
                        Actions.scaleTo(1f, 1f, 2.5f, Interpolation.pow5)
                )
        ));

        // create stage and add actors
        stage = new Stage(new StretchViewport(TankGame.WIDTH, TankGame.HEIGHT, cam));
        stage.addActor(tankImg);

        // load assets
        assetHandler = ((TankGame)Gdx.app.getApplicationListener()).assetHandler;
        assetHandler.loadAssets();
    }

    @Override
    protected void handleInput() {

    }

    @Override
    public void update(float dt) {
        if(assetHandler.manager.update()) {
            // finished loading
            GameStateManager.getGsm().push(new MenuState());
        }

        // loading assets
        progress = assetHandler.manager.getProgress();
        System.out.println(progress);

    }

    @Override
    public void render(SpriteBatch sb, PolygonSpriteBatch psb) {
        // act and draw actors
        stage.act();
        stage.draw();

        // draw loading bar
        shapeRenderer.setProjectionMatrix(cam.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // bg bar
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(100, 75, TankGame.WIDTH - 200, 100);

        // current progress
        shapeRenderer.setColor(Color.FOREST);
        shapeRenderer.rect(105, 80, (TankGame.WIDTH - 210)*progress, 90);

        shapeRenderer.end();
    }

    @Override
    public void dispose() {

    }

    @Override
    public void onLoad() {

    }
}

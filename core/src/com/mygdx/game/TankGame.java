package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.states.GameStateManager;
import com.mygdx.game.states.MenuState;

public class TankGame extends ApplicationAdapter {
	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;
	public static final String TITLE = "Tank_title";

	private static SpriteBatch batch;
	private static BTInterface bluetooth;

	public TankGame(BTInterface bluetoothCon) {
		bluetooth = bluetoothCon;
	}

	public static BTInterface getBluetooth() {
		return bluetooth;
	}

	@Override
	public void create () {
        batch = new SpriteBatch();
		Gdx.gl.glClearColor(0, 0, 0, 1);
		GameStateManager.getGsm().push(new MenuState(/*gsm*/));
	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		GameStateManager.getGsm().update(Gdx.graphics.getDeltaTime());
		GameStateManager.getGsm().render(batch);
	}

	@Override
	public void dispose () {
		super.dispose();
	}
}
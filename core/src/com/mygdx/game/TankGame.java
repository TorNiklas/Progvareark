package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.states.GameStateManager;
import com.mygdx.game.states.MenuState;
import com.mygdx.game.states.OptionState;

public class TankGame extends ApplicationAdapter {
	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;
	public static final String TITLE = "PANZERWAGEN";

	public static Music music_level1;
	public static boolean isMuted = false;

	private static SpriteBatch sb;
	private static PolygonSpriteBatch psb;
	private static BTInterface bluetooth;

	public static boolean host = false;

	public TankGame(BTInterface bluetoothCon) {
		bluetooth = bluetoothCon;
	}

	public static BTInterface getBluetooth() {
		return bluetooth;
	}

	@Override
	public void create () {
		music_level1 = Gdx.audio.newMusic(Gdx.files.internal("sounds/level1.ogg"));
		music_level1.setLooping(true);
		music_level1.play();
		music_level1.setVolume(0.2f);

		sb = new SpriteBatch();
		psb = new PolygonSpriteBatch();
		Gdx.gl.glClearColor(0, 0, 0, 1);
		GameStateManager.getGsm().push(new MenuState(/*gsm*/));
	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		GameStateManager.getGsm().update(Gdx.graphics.getDeltaTime());
		GameStateManager.getGsm().render(sb, psb);
	}

	@Override
	public void dispose () {
		super.dispose();
	}
}

package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.mygdx.game.states.GameStateManager;
import com.mygdx.game.states.LoadingState;

public class TankGame extends ApplicationAdapter {
	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;
	public static final String TITLE = "PANZERWAGEN";

	public AssetHandler assetHandler;
	public static TextureAtlas textureAtlas;

	public static Music music_level1;
	public static float volume = 0.2f;
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
		assetHandler = new AssetHandler();
		GameStateManager.getGsm().push(new LoadingState());

		// music
		music_level1 = Gdx.audio.newMusic(Gdx.files.internal("sounds/level1.ogg"));
		music_level1.setLooping(true);
		music_level1.play();
		music_level1.setVolume(volume);

		sb = new SpriteBatch();
		psb = new PolygonSpriteBatch();
		Gdx.gl.glClearColor(0, 0, 0, 1);
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
		assetHandler.dispose();
	}
}

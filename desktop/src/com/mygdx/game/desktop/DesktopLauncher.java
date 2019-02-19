package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.Terrain;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = Terrain.WIDTH;
		config.height = Terrain.HEIGHT;
		config.title = Terrain.TITLE;
		new LwjglApplication(new Terrain(), config);
	}
}

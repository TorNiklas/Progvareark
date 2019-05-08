package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.TankGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = TankGame.WIDTH;
		config.height = TankGame.HEIGHT;
		config.title = TankGame.TITLE;

		new LwjglApplication(new TankGame(new BT()), config);
	}
}

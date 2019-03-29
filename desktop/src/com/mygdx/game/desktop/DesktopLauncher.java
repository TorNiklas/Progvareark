package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.mygdx.game.TankGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = TankGame.WIDTH;
		config.height = TankGame.HEIGHT;
		config.title = TankGame.TITLE;

		// pack textures
		//TexturePacker.process("C:\\Users\\wizard man\\Documents\\ProgArk\\project\\android\\assets\\input", "C:\\Users\\wizard man\\Documents\\ProgArk\\project\\android\\assets\\output", "game");

		new LwjglApplication(new TankGame(new BT()), config);
	}
}

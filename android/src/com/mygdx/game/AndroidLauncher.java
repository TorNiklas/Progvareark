package com.mygdx.game;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class AndroidLauncher extends AndroidApplication implements BTInterface {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new TankGame(this), config);
	}

	private void initConnection(int reqCode) {
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

		if (adapter == null) {
			// Device doesn't support Bluetooth
		}
		Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		if (!adapter.isEnabled()) {
			startActivityForResult(enableBtIntent, reqCode);
		}
		else {
			onActivityResult(reqCode, RESULT_OK, enableBtIntent);
		}

	}

	@Override
	public void startHost() {
		System.out.println("BLUETOOTH CONNECTION LOL");
		initConnection(0);
	}

	@Override
	public void startClient() {
		System.out.println("BLUETOOTH CONNECTION LOL");
		initConnection(1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			System.out.println("BT FUNKA LOL");
			if (requestCode == 0) {
				//HOST
				System.out.println("HOST");
			}
			else if (requestCode == 1) {
				//CLIENT
				System.out.println("CLIENT");
			}
		}
	}
}

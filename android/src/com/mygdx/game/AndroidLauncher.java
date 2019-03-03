package com.mygdx.game;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.mygdx.game.sprites.GameSprite;
import com.mygdx.game.states.MenuState;
import com.mygdx.game.states.PlayState;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class AndroidLauncher extends AndroidApplication implements BTInterface {
	private static final UUID uuid = UUID.fromString("20a85f51-908e-451f-ba1e-395ced9acdf0");
	private static ConnectedThread connThread;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(receiver, filter);
		IntentFilter filter2 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(receiver, filter2);

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new TankGame(this), config);
	}

	@Override
	public void startHost() {
		System.out.println("Starting host connection...");
		//currentMode = BTMode.GAME_HOST;
		//HOST
		//Makes device discoverable
		Intent discoverableIntent =
				new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 60);
		startActivityForResult(discoverableIntent, 0);
	}

	@Override
	public void startClient() {
		System.out.println("Starting client connection...");
		//currentMode = BTMode.GAME_CLIENT;
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

		if (adapter == null) {
			// Device doesn't support Bluetooth
		}
		else {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			if (!adapter.isEnabled()) {
				startActivityForResult(enableBtIntent, 1);
			}
			else {
				onActivityResult(1, RESULT_OK, enableBtIntent);
			}
		}
	}

	@Override
	public void writeSprites(ArrayList<GameSprite> sprites) {
		/*if (connThread != null) {
			for (GameSprite gs : sprites) {
				*//*System.out.println("Sending...");
				System.out.println(gs.getType());
				System.out.println(gs.getPosition());*//*
				connThread.write(gs.getType());
				connThread.write(gs.getPosition());
			}
			//connThread.write(sprites);
		}
		else {
			System.out.println("NOT CONNECTED!!!!");
		}*/
	}

	@Override
	public void writeObject(Serializable object) {
		if (connThread != null) {
			connThread.write(object);
		}
		else {
			System.out.println("NOT CONNECTED!!!!");
		}
	}

	@Override
	public void disconnect() {
		if (connThread != null) {
			connThread.cancel();
		}
	}

	// Create a BroadcastReceiver for ACTION_FOUND.
	private final BroadcastReceiver receiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Discovery has found a device. Get the BluetoothDevice
				// object and its info from the Intent.
				System.out.println("BT unit found");
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
				try {
					BluetoothSocket socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
					socket.connect();
					connThread = new ConnectedThread(socket, false, AndroidLauncher.this);
					connThread.start();
					MenuState.onConnected(false);
				}
				catch (IOException e) {
					e.printStackTrace();
					//Ikke riktig device?
				}
			}
			else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				System.out.println("DISCOVERY FINISHED");
			}
		}
	};

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, Intent data) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (requestCode == 0) {
					//Host
					if (resultCode == 60) {
                        System.out.println("Host req accepted...");
						try {
							BluetoothServerSocket serverSocket = BluetoothAdapter.getDefaultAdapter().listenUsingInsecureRfcommWithServiceRecord("TANKS", uuid);
							BluetoothSocket socket = serverSocket.accept();
							//Connected
							System.out.println("Connected, stopping server socket");
							serverSocket.close();
							connThread = new ConnectedThread(socket, true, AndroidLauncher.this);
							connThread.start();
							MenuState.onConnected(true);
						}
						catch (Exception e) {
							//TODO: Error handling
							System.out.println("Error accepting sockets");
							e.printStackTrace();
						}
					}
					else {
                        System.out.println("Host req failed?");
                        System.out.println(resultCode);
                    }

				}

				else if (requestCode == 1) {
				    if (resultCode == RESULT_OK) {
                        System.out.println("Client req accepted, start discovery");
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {  // Only ask for these permissions on runtime when running Android 6.0 or higher
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									switch (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
										case PackageManager.PERMISSION_DENIED:
											((TextView) new AlertDialog.Builder(AndroidLauncher.this)
													.setTitle("Runtime Permissions up ahead")
													.setMessage(Html.fromHtml("<p>To find nearby bluetooth devices please click \"Allow\" on the runtime permissions popup.</p>" +
															"<p>For more info see <a href=\"http://developer.android.com/about/versions/marshmallow/android-6.0-changes.html#behavior-hardware-id\">here</a>.</p>"))
													.setNeutralButton("Okay", new DialogInterface.OnClickListener() {
														@Override
														public void onClick(DialogInterface dialog, int which) {
															if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
																ActivityCompat.requestPermissions(AndroidLauncher.this,
																		new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
																		1);
															}
														}
													})
													.show()
													.findViewById(android.R.id.message))
													.setMovementMethod(LinkMovementMethod.getInstance());       // Make the link clickable. Needs to be called after show(), in order to generate hyperlinks
											break;
										case PackageManager.PERMISSION_GRANTED:
											BluetoothAdapter.getDefaultAdapter().startDiscovery();
											break;
									}
								}
							});

						}
                    }
                    else {
                        System.out.println("Client req failed?");
                        System.out.println(resultCode);
                    }

				}
			}
		}).run();
	}
	@Override
	protected void onStop() {
		super.onStop();
		BluetoothAdapter.getDefaultAdapter().disable();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}
}

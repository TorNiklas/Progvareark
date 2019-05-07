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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.game.network.SpriteJSON;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;
import java.util.UUID;

public class AndroidLauncher extends AndroidApplication implements BTInterface {
	Stack<SpriteJSON> sprites = new Stack<>(); //Most recent sprites received over network
	private static final UUID uuid = UUID.fromString("20a85f51-908e-451f-ba1e-395ced9acdf0");
	private String oName = BluetoothAdapter.getDefaultAdapter().getName();
	private static ConnectedThread connThread;
	private boolean continueDiscovery = true;
	private String code;
	Runnable onConnected;
	Runnable onDisconnect;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(receiver, filter);
		IntentFilter filter2 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(receiver, filter2);
		IntentFilter filter3 = new IntentFilter((BluetoothDevice.ACTION_UUID));
		registerReceiver(receiver, filter3);

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useAccelerometer = false;
		config.useCompass = false;
		initialize(new TankGame(this), config);
	}


	@Override
	public void startHostConnection(String codeIn, Runnable onConnected, Runnable onDisconnect) {

	    this.code = codeIn;
	    this.onConnected = onConnected;
	    this.onDisconnect = onDisconnect;

	    new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(50); //Hack, it won't always change to new menu otherwise
				}
				catch (Exception e) {}
				showToast("Starting host connection...");

				BluetoothAdapter.getDefaultAdapter().enable();
				while(!BluetoothAdapter.getDefaultAdapter().isEnabled()) {}
				showToast("BT Enabled");

				String newName = uuid.toString() + code;
				BluetoothAdapter.getDefaultAdapter().setName(newName);
				while(!BluetoothAdapter.getDefaultAdapter().getName().equals(newName)) {}
				showToast("Name changed");

				//currentMode = BTMode.GAME_HOST;
				//HOST
				//Makes device discoverable
				Intent discoverableIntent =
						new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
				discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 60);
				startActivityForResult(discoverableIntent, 0);
			}
		}).start();

	}

	@Override
	public void startHostGame(int level, int seed) {
		System.out.println("Starting host game");
		ConnectedThread.level = level;
		ConnectedThread.seed = seed;

		if (connThread != null) {
			connThread.startGameHost(); //start game if ready
		}
	}

	@Override
	public void startClientConnection(String code, Runnable onConnected, Runnable onDisconnect) {
		this.code = code;
		this.onConnected = onConnected;
		this.onDisconnect = onDisconnect;

		new Thread(new Runnable() {
			@Override
			public void run() {
				showToast("Starting client connection...");
				//currentMode = BTMode.GAME_CLIENT;
				BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

				if (adapter == null) {
					// Device doesn't support Bluetooth
				}
				else {
					Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
					if (!adapter.isEnabled()) {
						startActivityForResult(enableBtIntent, 2);
					}
					else {
						onActivityResult(1, RESULT_OK, enableBtIntent);
						System.out.println("CLIENT CONN");
					}
				}
			}
		}).start();

	}

	@Override
	public void disconnect() {
		sprites = new Stack<>();

		if (connThread != null) {
			connThread.cancel();
			connThread = null;
		}

		else if (onDisconnect != null) {
			onDisconnect.run();
		}
	}



	private final BroadcastReceiver receiver = new BroadcastReceiver() {
		//ArrayList<BluetoothDevice> devices = new ArrayList<>();

		public void onReceive(Context context, final Intent intent) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					String action = intent.getAction();
					if (BluetoothDevice.ACTION_FOUND.equals(action)) {
						BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
						System.out.println("BT unit found: " + device.getName());
						String name = device.getName();
						if (name != null && name.equals(uuid.toString() + code)) {
							showToast("Found host");
							continueDiscovery = false;
							BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

							try {
								BluetoothSocket socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
								socket.connect();
								connThread = new ConnectedThread(socket, false, AndroidLauncher.this);
								connThread.startGameClient(code);
							} catch (IOException e) {
								e.printStackTrace();
								//Ikke riktig device?
							}
						}
					}
					else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
						showToast("Discovery finished");
						if (continueDiscovery) {
							BluetoothAdapter.getDefaultAdapter().startDiscovery();
						}
					}
				}
			}).start();
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
                        showToast("Host req accepted, starting server...");
						try {
							BluetoothServerSocket serverSocket = BluetoothAdapter.getDefaultAdapter().listenUsingInsecureRfcommWithServiceRecord("TANKS", uuid);
							BluetoothSocket socket = serverSocket.accept();
							//Connected
							showToast("Connected, stopping server socket");
							serverSocket.close();
							connThread = new ConnectedThread(socket, true, AndroidLauncher.this);

							if (ConnectedThread.level != -1 && ConnectedThread.seed != -1) {
								connThread.startGameHost();
							}
							//connThread.start();
						}
						catch (Exception e) {
							//TODO: Error handling
							System.out.println("Error accepting sockets");
							e.printStackTrace();
						}
					}
					else {
                        showToast("Host req failed?");
                        System.out.println(resultCode);
                    }

				}

				else if (requestCode == 1 || requestCode == 2) {
				    if (resultCode == RESULT_OK) {
                        System.out.println("Client req accepted, start discovery");
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									switch (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
										case PackageManager.PERMISSION_DENIED:
											((TextView) new AlertDialog.Builder(AndroidLauncher.this)
													.setTitle("Bluetooth permission approval")
													.setMessage(Html.fromHtml("<p>In order to discover nearby Bluetooth devices, we need permission to scan your local area. Please click \"Allow\" on the permission popup.</p>"))
													.setNeutralButton("OK", new DialogInterface.OnClickListener() {
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
													.setMovementMethod(LinkMovementMethod.getInstance());
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
		}).start();
	}
	@Override
	protected void onStop() {
		super.onStop();
		BluetoothAdapter.getDefaultAdapter().setName(oName);
		BluetoothAdapter.getDefaultAdapter().disable();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	private void showToast(final String text) {
		System.out.println(text);
		// FIXME: doesn't work on all android devices?
		/*runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(AndroidLauncher.this, text, Toast.LENGTH_SHORT).show();
			}
		});*/
	}

	@Override
	public void setOnDisconnect(Runnable onDisconnect) {
		this.onDisconnect = onDisconnect;
	}

	@Override
	public Stack<SpriteJSON> getSprites() {
		return sprites;
	}
}

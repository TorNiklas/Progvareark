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
import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.mygdx.game.network.SpriteSerialize;
import com.mygdx.game.sprites.GameSprite;
import com.mygdx.game.states.GameSetupState;
import com.mygdx.game.states.MenuState;
import com.mygdx.game.states.PlayState;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Stack;
import java.util.UUID;

public class AndroidLauncher extends AndroidApplication implements BTInterface {
	private ArrayList<SpriteSerialize> sprites = new ArrayList<>(); //Most recent sprites received over network
	private static final UUID uuid = UUID.fromString("20a85f51-908e-451f-ba1e-395ced9acdf0");
	private String oName = BluetoothAdapter.getDefaultAdapter().getName();
	private static ConnectedThread connThread;
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
		initialize(new TankGame(this), config);
	}

	@Override
	public void startHost(String codeIn, Runnable onConnected, Runnable onDisconnect) {
	    this.code = codeIn;
	    this.onConnected = onConnected;
	    this.onDisconnect = onDisconnect;

	    new Thread(new Runnable() {
			@Override
			public void run() {
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
	public void startClient(String codeIn, Runnable onConnected, Runnable onDisconnect) {
	    this.code = codeIn;
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
						startActivityForResult(enableBtIntent, 1);
					}
					else {
						onActivityResult(1, RESULT_OK, enableBtIntent);
					}
				}
			}
		}).start();

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
		//ArrayList<BluetoothDevice> devices = new ArrayList<>();

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Discovery has found a device. Get the BluetoothDevice
				// object and its info from the Intent.
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				System.out.println("BT unit found: " + device.getName());
				String name = device.getName();
				if (name != null && name.equals(uuid.toString() + code)) {
					showToast("Found host");
					BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

					try {
						BluetoothSocket socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
						socket.connect();
						connThread = new ConnectedThread(socket, false, AndroidLauncher.this);
						connThread.start();
					} catch (IOException e) {
						e.printStackTrace();
						//Ikke riktig device?
					}
				}
			}
			else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				showToast("Discovery finished");
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
                        showToast("Host req accepted, starting server...");
						try {
							BluetoothServerSocket serverSocket = BluetoothAdapter.getDefaultAdapter().listenUsingInsecureRfcommWithServiceRecord("TANKS", uuid);
							BluetoothSocket socket = serverSocket.accept();
							//Connected
							showToast("Connected, stopping server socket");
							serverSocket.close();
							connThread = new ConnectedThread(socket, true, AndroidLauncher.this);
							connThread.start();
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
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(AndroidLauncher.this, text, Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public ArrayList<SpriteSerialize> getSprites() {
		return sprites;
	}

	public void setSprites(ArrayList<SpriteSerialize> sprites) {
		this.sprites = sprites;
	}
}

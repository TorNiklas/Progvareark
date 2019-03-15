package com.mygdx.game;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.Toast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.network.SpriteSerialize;
import com.mygdx.game.sprites.GameSprite;
import com.mygdx.game.states.GameSetupState;
import com.mygdx.game.states.GameStateManager;
import com.mygdx.game.states.PlayState;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;

class ConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    /*private final InputStream mmInStream;
    private final OutputStream mmOutStream;*/
    private final ObjectInputStream oInput;
    private final ObjectOutputStream oOutput;
    private AndroidLauncher act;
    private String code;

    public ConnectedThread(BluetoothSocket socket, boolean host, AndroidLauncher act) {
        this.act = act;
        System.out.println("Creating ConnThread");
        mmSocket = socket;
        ObjectInputStream tmpOIS = null;
        ObjectOutputStream tmpOOS = null;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        // Get the input and output streams; using temp objects because
        // member streams are final.
        System.out.println("Creating streams...");
        try {
            tmpIn = socket.getInputStream();
            //tmpOIS = new ObjectInputStream(socket.getInputStream());
            System.out.println("Input stream created...");
        } catch (IOException e) {
            Log.e("TAG", "Error occurred when creating input stream", e);
        }
        try {
            tmpOut = socket.getOutputStream();
            //tmpOOS = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Output stream created...");
        } catch (IOException e) {
            Log.e("TAG", "Error occurred when creating output stream", e);
        }

        if (host) {
            try {
                tmpOOS = new ObjectOutputStream(tmpOut);
                tmpOOS.flush();
                tmpOIS = new ObjectInputStream(tmpIn);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                tmpOIS = new ObjectInputStream(tmpIn);
                tmpOOS = new ObjectOutputStream(tmpOut);
                tmpOOS.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        oInput = tmpOIS;
        oOutput = tmpOOS;

        //mmInStream = tmpIn;
        //mmOutStream = tmpOut;
    }

    private void writeSprites() {
        write(PlayState.getNetSprites());
    }

    public void startGameHost(int level, int seed) {
        //Send seed
        try {
            oOutput.writeInt(level);
            System.out.println("Sent level: " + level);
            oOutput.writeInt(seed);
            System.out.println("Sent seed: " + seed);
        } catch (IOException e) {
            System.out.println("Error sending seed");
            e.printStackTrace();
        }
        this.start();
    }

    public void startGameClient(String code) {
        this.code = code;
        try {
            final int level = oInput.readInt();
            System.out.println("Read level: " + level);
            final int seed = oInput.readInt();
            System.out.println("Read seed: " + seed);
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    GameStateManager.getGsm().setPlayState(level, seed);
                }
            });
        } catch (IOException e) {
            System.out.println("Error reading seed");
            e.printStackTrace();
        }
        this.start();
    }

    public void run() {
        System.out.println("ConnThread running...");
        //act.onConnected.run();

        Gdx.app.postRunnable(act.onConnected);

        //Send sprites to other player every x millis
        System.out.println("Starting send");
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    writeSprites();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 100, 100, TimeUnit.MILLISECONDS);

        // Keep listening to the InputStream until an exception occurs.
        System.out.println("Starting listen");
        while (true) {
            try {
                // Read from the InputStream.
                try {
                    ArrayList<SpriteSerialize> sprites = (ArrayList<SpriteSerialize>) oInput.readObject();
                    if (sprites != null) {
                        act.setSprites(sprites);
                    } else {
                        System.out.println("Nothing received");
                    }
                } catch (EOFException e) {
                } catch (IOException e) {
                    System.out.println(e.toString());
                    if (e.toString().contains("bt socket closed")) {
                        System.out.println("Disconnected");
                        //act.onDisconnect.run();
                        Gdx.app.postRunnable(act.onDisconnect);
                        break;
                    }
                } catch (Exception e) {
                    System.out.println("Error in object read");
                    e.printStackTrace();
                }


            } catch (/*IOException | */Exception e) {
                Log.d("TAG", "Input stream was disconnected", e);
                break;
            }
        }
    }

    // Call this from the main activity to send data to the remote device.
    public void write(Serializable obj) {
        try {
            oOutput.writeObject(obj);
        } catch (IOException e) {
            Log.e("TAG", "Error occurred when sending data", e);
        }
    }

    // Call this method from the main activity to shut down the connection.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e("TAG", "Could not close the connect socket", e);
        }
    }
}



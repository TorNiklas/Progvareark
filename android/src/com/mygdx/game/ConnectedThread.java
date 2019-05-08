package com.mygdx.game;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.mygdx.game.network.SpriteJSON;
import com.mygdx.game.states.GameStateManager;
import com.mygdx.game.states.PlayState;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class ConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream input;
    private final OutputStream output;
    private final int JOLength = 105; //JSON Object length in chars
    private byte[] buffer = new byte[1024];

    public static int level = -1;
    public static int seed = -1;

    private AndroidLauncher act;

    private ScheduledExecutorService exec;

    public ConnectedThread(BluetoothSocket socket, AndroidLauncher act) {
        this.act = act;
        System.out.println("Creating ConnThread");
        mmSocket = socket;

        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        System.out.println("Creating streams...");
        try {
            tmpIn = socket.getInputStream();
            System.out.println("Input stream created...");
        } catch (IOException e) {
            Log.e("TAG", "Error occurred when creating input stream", e);
        }
        try {
            tmpOut = socket.getOutputStream();
            System.out.println("Output stream created...");
        } catch (IOException e) {
            Log.e("TAG", "Error occurred when creating output stream", e);
        }

        input = tmpIn;
        output = tmpOut;
    }

    private void writeSprites() {
        ArrayList<SpriteJSON> json = PlayState.getJSON();
        for (SpriteJSON s : json) {
            try {
                write(s);
            }
            catch (IOException e) {
                System.out.println("ERROR SENDING STUFF");
                e.printStackTrace();
                cancel();
            }
        }
    }

    public void startGameHost() {
        //Send seed
        try {
            write(level + "");
            System.out.println("Sent level: " + level);
            input.read();
            write(seed + "");
            System.out.println("Sent seed: " + seed);
            input.read();
        } catch (IOException e) {
            System.out.println("Error sending seed");
            e.printStackTrace();
        }
        this.start();
    }

    public void startGameClient() {
        try {
            final int level = Integer.parseInt(readString());
            System.out.println("Read level: " + level);
            output.write(0);
            final int seed = Integer.parseInt(readString());
            System.out.println("Read seed: " + seed);
            output.write(0);
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

    private void startWriteLoop() {
        exec = Executors.newScheduledThreadPool(1);
        exec.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    writeSprites();
                } catch (Exception e) {
                    System.out.println("ERROR IN SCHEDULE");
                    e.printStackTrace();
                }
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
    }

    private StringBuilder builder;
    private InputStreamReader isReader;

    public void run() {
        System.out.println("ConnThread running...");

        Gdx.app.postRunnable(act.onConnected);

        //Send sprites to other player every x millis
        System.out.println("Starting send");
        startWriteLoop();

        System.out.println("Starting listen");
        builder = new StringBuilder(JOLength);
        isReader = new InputStreamReader(input);

        while (true) {
            try {
                int ch = isReader.read();
                if (ch != -1) {
                    char b = (char) ch;
                    switch (b) {
                        case '{': //New JSON object, empty the string builder
                            builder = new StringBuilder(JOLength);
                            builder.append(b);
                            break;
                        case '}': //End of JSON object
                            builder.append(b);
                            act.sprites.push(new SpriteJSON(builder.toString()));
                            break;
                        default:
                            builder.append(b);
                            break;
                    }
                }
            }
            catch (IOException e) {
                System.out.println("Input stream disconnected");
                cancel();
                e.printStackTrace();
                break;
            }
        }
    }

    public void write(SpriteJSON obj) throws IOException {
        write(obj.toString());
    }

    public void write(String str) throws IOException {
        output.write(str.getBytes());
    }

    private String readString() { //Read a single string
        buffer = new byte[1024];
        try {
            int num = input.read(buffer);
            String ret = new String(buffer, 0, num);
            return ret;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ERROR READING STRING");
        }
        return "";
    }

    public void cancel() {
        if (exec != null) {
            exec.shutdown();
        }
        Gdx.app.postRunnable(act.onDisconnect);
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e("TAG", "Could not close the connect socket", e);
        }
    }
}



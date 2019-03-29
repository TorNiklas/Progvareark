package com.mygdx.game;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.mygdx.game.network.SpriteJSON;
import com.mygdx.game.states.GameStateManager;
import com.mygdx.game.states.PlayState;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class ConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    /*private final InputStream mmInStream;
    private final OutputStream mmOutStream;*/
    private final InputStream input;
    private final OutputStream output;
    private final int JOLength = 105; //JSON Object length in chars
    private byte[] buffer = new byte[1024];

    public static int level = -1;
    public static int seed = -1;

    //    private ArrayList<SpriteJSON> objs = new ArrayList<>();
    private AndroidLauncher act;
    private String code;

    public ConnectedThread(BluetoothSocket socket, boolean host, AndroidLauncher act) {
        this.act = act;
        System.out.println("Creating ConnThread");
        mmSocket = socket;

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

        input = tmpIn;
        output = tmpOut;

        //mmInStream = tmpIn;
        //mmOutStream = tmpOut;
    }

    private void writeSprites() {
        ArrayList<SpriteJSON> json = PlayState.getJSON();
        for (SpriteJSON s : json) {
/*                System.out.println("WRITE");
                System.out.println(s);
                System.out.println();*/
            write(s);
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

    public void startGameClient(String code) {
        this.code = code;
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
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
//                System.out.println("Loop send");
//                System.out.println(objs);
                try {
                    writeSprites();
                } catch (Exception e) {
                    System.out.println("ERROR IN SCHEDULE");
                    e.printStackTrace();
                }
            }
        }, 100, 50, TimeUnit.MILLISECONDS);
    }

    public void run() {
        System.out.println("ConnThread running...");
        //act.onConnected.run();

        Gdx.app.postRunnable(act.onConnected);

        //Send sprites to other player every x millis
        System.out.println("Starting send");
        startWriteLoop();

        System.out.println("Starting listen");
        buffer = new byte[1024];
        int numBytes; // bytes returned from read()
        String in;
        StringBuilder bldr = new StringBuilder();

        // Keep listening to the InputStream until an exception occurs.
        while (true) {
            try {
                numBytes = input.read(buffer);

                in = new String(buffer, 0, numBytes);

                /*System.out.println("READ FROM STREAM:");
                System.out.println(in);*/

                int len = in.length();

                if (len == JOLength) {
                    act.sprites.push(new SpriteJSON(in));

                }
                else if (len % JOLength == 0){

                    int amount = len / JOLength;

                    for (int i = 0; i < amount; i++) {
                        int pos = i * JOLength;
                        String s = in.substring(pos, pos + JOLength);
                        //System.out.println(s);
                        act.sprites.push(new SpriteJSON(s));
                    }
                }
                else {
                    System.out.println("DIFF INPUT O SHIT");
                    System.out.println(in);
                }


                /*else if (len == 120) {

                }*/

            } catch (IOException e) {
                System.out.println("Input stream disconnected");
                e.printStackTrace();
                break;
            }
            /*catch (JSONException e) {
                e.printStackTrace();
                System.out.println("ERROR IN JSON");
            }*/
        }
    }

    // Call this from the main activity to send data to the remote device.
    public void write(SpriteJSON obj) {
        write(obj.toString());
    }

    public void write(String str) {
        try {
            output.write(str.getBytes());
//            output.write(0);
        } catch (IOException e) {
            System.out.println("ERROR SENDING STRING");
            e.printStackTrace();
        }
    }

    private String readString() { //Read a single string
        buffer = new byte[1024];
        try {
            int num = input.read(buffer);
            String ret = new String(buffer, 0, num);
//            System.out.println(ret);
            return ret;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ERROR READING STRING");
        }
        return "";
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



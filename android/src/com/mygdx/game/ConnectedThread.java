package com.mygdx.game;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.Toast;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.sprites.GameSprite;
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

class ConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    /*private final InputStream mmInStream;
    private final OutputStream mmOutStream;*/
    private final ObjectInputStream oInput;
    private final ObjectOutputStream oOutput;
    private Activity act;

    public ConnectedThread(BluetoothSocket socket, boolean host, Activity act) {
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
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                tmpOIS = new ObjectInputStream(tmpIn);
                tmpOOS = new ObjectOutputStream(tmpOut);
                tmpOOS.flush();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        oInput = tmpOIS;
        oOutput = tmpOOS;

        //mmInStream = tmpIn;
        //mmOutStream = tmpOut;
    }

    public void run() {
        System.out.println("ConnThread running...");

        // Keep listening to the InputStream until an exception occurs.
        while (true) {
            try {
                // Read from the InputStream.
                try {
                    //final String inn = (String) oInput.readObject();
                    final Integer[] xy = (Integer[]) oInput.readObject();
                    /*int x = oInput.readInt();
                    int y = oInput.readInt();*/
                    System.out.println(Arrays.toString(xy));
                    PlayState.fire(xy[0], xy[1], 1000);
                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(act, Arrays.toString(xy), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                catch (EOFException e) {}
                catch (Exception e) {
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



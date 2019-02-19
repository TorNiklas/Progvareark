package com.mygdx.game.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

public class ClientConnection extends Activity {

    private BluetoothAdapter adapter;

    public ClientConnection() {
        adapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void startClient() {
        if (adapter == null) {
            // Device doesn't support Bluetooth
        }
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        if (!adapter.isEnabled()) {
            startActivityForResult(enableBtIntent, 0);
        }
        else {
            onActivityResult(0, RESULT_OK, enableBtIntent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

        }
    }
}

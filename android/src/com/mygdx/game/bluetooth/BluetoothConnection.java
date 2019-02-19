package com.mygdx.game.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

import com.mygdx.game.BTInterface;

public class BluetoothConnection extends Activity implements BTInterface {

    public BluetoothAdapter getAdapter() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            //IKKENO BT
        }
        if (!adapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            this.startActivityForResult(enableBtIntent, 0);
        }
        return adapter;
    }

    @Override
    public void startHost() {

    }

    @Override
    public void startClient() {

    }
}

package com.mygdx.game.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

import com.mygdx.game.BTInterface;

public class BluetoothConnection implements BTInterface {

    @Override
    public void startHost() {
        System.out.println("BLUETOOTH CONNECTION LOL");
        (new HostConnection()).startHost();

    }

    @Override
    public void startClient() {
        System.out.println("BLUETOOTH CONNECTION LOL");
        (new ClientConnection()).startClient();
    }
}

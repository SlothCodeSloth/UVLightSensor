package com.example.finalproject;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class BLEManager {
    private static BLEManager instance;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice connectedDevice;
    private BluetoothGatt bluetoothGatt;
    private Context context;

    private BLEManager(Context context) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.context = context;
    }

    public static synchronized BLEManager getInstance(Context context) {
        if (instance == null) {
            instance = new BLEManager(context.getApplicationContext());
        }
        return instance;
    }

    public void connectToDevice(BluetoothDevice device, BluetoothGattCallback gattCallback) {
        if (bluetoothGatt != null) {
            bluetoothGatt.close();
        }
        bluetoothGatt = device.connectGatt(context, false, gattCallback);
        connectedDevice = device;
    }

    public void disconnect() {
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
        }
    }

    public boolean isConnectted() {
        return bluetoothGatt != null && connectedDevice != null;
    }

    private boolean hasBluetoothPermission() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED;
    }

}

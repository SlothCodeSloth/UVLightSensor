package com.example.finalproject;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AboutFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final int PERMISSION_REQUEST_CODE = 1001;

    private static final int REQUEST_ENABlE_BT = 1;
    private static final String TAG = "AboutFragment";

    // TODO: Rename and change types of parameters

    String name;
    int spf = 0;
    int skinTypeNumber;
    int selectedType = 0;
    EditText editName;
    EditText editSPF;
    Button saveButton;
    SharedPreferences userInfo;
    private Spinner skinTypeSpinner;
    private skinTypeAdapter adapter;
    private Button scanButton;
    TextView bluetoothView;

    public AboutFragment() {
        // Required empty public constructor
    }
    // TODO: Rename and change types and number of parameters

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        editName = view.findViewById(R.id.editName);
        editSPF = view.findViewById(R.id.editSPF);
        saveButton = view.findViewById(R.id.saveButton);
        skinTypeSpinner = view.findViewById(R.id.skinTypeSpinner);
        adapter = new skinTypeAdapter(getContext(), skinTypeData.getskinTypeList());  // MainActivity.this
        skinTypeSpinner.setAdapter(adapter);
        scanButton = view.findViewById(R.id.scanButton);
        bluetoothView = view.findViewById(R.id.bluetoothView);

        if (restorePrefData()) {
            name = userInfo.getString("name", "");
            spf = userInfo.getInt("spf", 0);
            skinTypeNumber = userInfo.getInt("skinType", 0);
            editName.setText(name);
            editSPF.setText(String.valueOf(spf));
            skinTypeSpinner.setSelection(skinTypeNumber - 1);
            bluetoothView.setText("Connected to: Nano 33 BLE (Temp Name)"); // Remove
        }

        skinTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                skinType type = (skinType) adapter.getItem(i);
                selectedType = type.getTypeNumber();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkPermissions()) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH},
                            PERMISSION_REQUEST_CODE);
                }
                else {
                    doBluetooth();
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmpty(editName) || isEmpty(editSPF)) {
                    Toast.makeText(getActivity(), "Please respond to every prompt", Toast.LENGTH_SHORT).show();
                }
                else if (Integer.parseInt(editSPF.getText().toString()) == 0) {
                    Toast.makeText(getActivity(), "Please respond to every prompt correctly", Toast.LENGTH_SHORT).show();

                }
                else {
                    Toast.makeText(getActivity(), "Information Saved!", Toast.LENGTH_SHORT).show();
                    name = editName.getText().toString();
                    spf = Integer.parseInt(editSPF.getText().toString());
                    skinTypeNumber = selectedType;
                    savePrefsData();
                }
            }
        });
        return view;
    }

    private void savePrefsData() {
        SharedPreferences userInfo = getActivity().getSharedPreferences("userInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = userInfo.edit();
        editor.putBoolean("hasData", true);
        editor.putString("name", name);
        editor.putInt("spf", spf);
        editor.putInt("skinType", skinTypeNumber);
        editor.apply();
    }

    private void doBluetooth() {
        // Check if Bluetooth is supported on the device
        BluetoothManager bluetoothManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Toast.makeText(getActivity(), "Bluetooth not supported on this device", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ensure Bluetooth is enabled
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABlE_BT);
        }
        else {
            // Bluetooth is enabled
            // I want to connect to a specific BLE Device under the name "UVSensor"
            startBLEScan();
            //Intent intentOpenBluetoothSettings = new Intent();
            //intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
            //startActivity(intentOpenBluetoothSettings);
        }
    }

    private boolean restorePrefData() {
        userInfo = getActivity().getSharedPreferences("userInfo", MODE_PRIVATE);
        boolean hasData = userInfo.getBoolean("hasData", false);
        return hasData;
    }

    private boolean isEmpty(EditText myeditText) {
        return myeditText.getText().toString().trim().length() == 0;
    }

    private void startBLEScan() {
        BluetoothManager bluetoothManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

        ScanCallback scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                BluetoothDevice device = result.getDevice();
                // BLUETOOTH CONNECT
                String deviceName = device.getName();
                if (deviceName != null && deviceName.equals("UVSensor")) {
                    Log.d(TAG, "Found Device: " + deviceName);
                    // BLUETOOTH SCAN
                    bluetoothLeScanner.stopScan(this);
                }
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.e(TAG, "BLE Scan failed with error code " + errorCode);
            }
        };

        // Definition of Scan Filters
        ScanFilter scanFilter = new ScanFilter.Builder()
                .setDeviceName("UVSensor")
                .build();
        ArrayList<ScanFilter> filters = new ArrayList<>();
        filters.add(scanFilter);

        // Definition of Scan Settings
        ScanSettings scanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();

        // Start Scanning
        bluetoothLeScanner.startScan(filters, scanSettings, scanCallback);
    }

    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (checkPermissions()) {
                doBluetooth();
            }
            else {
                Toast.makeText(getActivity(), "Permission denied. Cannot show notification.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
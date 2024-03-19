package com.example.finalproject;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
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
                fake();
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

    private boolean restorePrefData() {
        userInfo = getActivity().getSharedPreferences("userInfo", MODE_PRIVATE);
        boolean hasData = userInfo.getBoolean("hasData", false);
        return hasData;
    }

    private boolean isEmpty(EditText myeditText) {
        return myeditText.getText().toString().trim().length() == 0;
    }

    private void startBLEScan() {
        if (!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(getActivity(), "BLE is not supported on this device.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted. Prompt to request it.
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            return;
        }

        BluetoothManager bluetoothManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABlE_BT);
            return;
        }

        BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

        bluetoothLeScanner.startScan(scanCallBack);
    }

    private ScanCallback scanCallBack = new ScanCallback() {
        @Override
        public void onScanResult(int callbacktype, ScanResult result) {
            super.onScanResult(callbacktype, result);

            BluetoothDevice device = result.getDevice();

            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {
                String deviceName = device.getName();
                String deviceAddress = device.getAddress();
                Log.d(TAG, "Device Found: " + deviceName + " (" + deviceAddress + ")");
                String msg = "Connected to: " + deviceName;
                bluetoothView.setText(msg);
            }
            else {
                Log.e(TAG, "Permission to access Bluetooth information denied.");
            }
        }
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.e(TAG, "BLE scan failed with error code: " + errorCode);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (permissions.length > 0 && permissions[0].equals(android.Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission granted, now check Bluetooth permission
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                    // Permission not granted. Prompt to request it.
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.BLUETOOTH}, PERMISSION_REQUEST_CODE);
                } else {
                    // Both permissions granted, start BLE scan
                    startBLEScan();
                }
            } else {
                Toast.makeText(getActivity(), "Permission to scan denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void fake() {
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted. Prompt to request it.
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            (new Handler()).postDelayed(this::wait5, 5000);
            return;
        }
        (new Handler()).postDelayed(this::wait5, 5000);}

    public void wait5() {
        bluetoothView.setText("Connected to: Nano 33 BLE (Temp Name)");
    }
}
package com.example.bletest3;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

public class HomeFragment extends Fragment {
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_CODE = 2;
    private static final long SCAN_TIMEOUT_MS = 60000; // 30 Second Timer
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private BluetoothGatt bluetoothGatt;
    private Timer randomTimer;
    private boolean isRandomGenerationActive = false;
    private Handler mHandler = new Handler();
    String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT};
    Button button, scanButton, stopButton;
    TextView textView, textView3, textView4, timeLeftView, nameTextView;
    Animation btnAnim, btnLeaveRight, btnLeaveBottom;
    private LineChart lineChart;
    private List<Entry> entries = new ArrayList<>();
    private LineDataSet dataSet;
    private double totalTime, currentTime, uv, altitude;
    private CountDownTimer countDownTimer;
    private ProgressBar progressBar;
    private Handler handler;
    private String name;
    private int spfVal, skinTypeVal;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String name, int spfVal, int skinTypeVal) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        scanButton = view.findViewById(R.id.scanButton);
        stopButton = view.findViewById(R.id.stopButton);
        textView = view.findViewById(R.id.textView);
        textView3 = view.findViewById(R.id.textView3);

        textView4 = view.findViewById(R.id.textView4);

        btnAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.button_enter_top);
        btnLeaveRight = AnimationUtils.loadAnimation(getActivity(), R.anim.button_leave_right);
        btnLeaveBottom = AnimationUtils.loadAnimation(getActivity(), R.anim.button_leave_down);
        button = view.findViewById(R.id.button);

        stopButton.setVisibility(View.INVISIBLE);

        // Initialize the LineChart
        lineChart = view.findViewById(R.id.lineChart);
        timeLeftView = view.findViewById(R.id.timeLeftTextView);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        handler = new Handler();
        nameTextView = view.findViewById(R.id.textViewName);
        currentTime = totalTime;

        // Customize the LineChart
        setupLineChart();
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if permissions have been granted.
                //textView.setText("Connected to: UVSensor");
                //textView4.setText("Altitude: 19m");
                //Toast.makeText(requireContext(), "Found UV Sensor", Toast.LENGTH_SHORT).show();
                //startRandomNumberGeneration();
                checkPermissions();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disconnect();
                Log.d("MyApp", "DISCONNECT");
                stopButton.setVisibility(View.INVISIBLE);
                scanButton.setVisibility(View.VISIBLE);
                stopButton.startAnimation(btnLeaveBottom);
                scanButton.startAnimation(btnAnim);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View View) {
                setTimer();
                startTimer();
            }
        });
        return view;
    }

    private void setTimer() {

        String textboxString = textView3.getText().toString();
        String numericSubstring = textboxString.substring("UV Index: ".length()); // Extract substring containing the numeric value
        double uvIndex = Double.parseDouble(numericSubstring); // Parse the numeric substring to double

        //textboxString = textView4.getText().toString();
        //numericSubstring = textboxString.substring("Altitude: ".length()); // Extract substring containing the numeric value
        //double altitude = Double.parseDouble(numericSubstring); // Parse the numeric substring to double
        //totalTime = ((skin * spf) / (uv * altitude)) * 60;
        int skin = 2;
        int spf = 30;
        double altitude = 19;
        totalTime= formula((int) skin,spf,uvIndex,altitude);
        currentTime = totalTime;
        progressBar.setMax((int) currentTime * 100);
        timeLeftView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer((long) (totalTime * 1000), 100) {
            @Override
            public void onTick(long l) {
                // formula
                String textboxString = textView3.getText().toString();
                String numericSubstring = textboxString.substring("UV Index: ".length()); // Extract substring containing the numeric value
                double uvIndex = Double.parseDouble(numericSubstring); // Parse the numeric substring to double
                int skin = 2;
                int spf = 30;
                altitude = 19.0;
                double x = formula(skin,spf,uvIndex,altitude);
                x = totalTime / x;
                currentTime -= 0.1 * x;
                updateTimerText();
                progressBar.setProgress((int) currentTime * 100, true);

                int hour, minute, second;
                double temp = currentTime / x;
                hour = temp > 3600 ? (int) temp / 3600 : 0;
                temp -= hour * 3600;
                minute = temp > 60 ? (int) temp / 60 : 0;
                temp -= minute * 60;
                second = temp > 1 ? (int) temp : 0;
                String time = "Estimated Time Remaining\n" + String.format("%02d:%02d:%02d", hour, minute, second);
                timeLeftView.setText(time);

            }

            @Override
            public void onFinish() {
                timeLeftView.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                countDownTimer.cancel();
            }
        }.start();
    }

    private void updateTimerText() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (currentTime <= 0) {
                    countDownTimer.onFinish();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    protected double formula(int skinType, int spfInt, double uv, double altitude) {
        double skinFactor;
        double altitudeFactor;
        double spfD = (double) spfInt;
        double formula_time_raw;
        switch (skinType) {
            case 1:
                skinFactor = 0.3;
                break;
            case 2:
                skinFactor = 0.4;
                break;
            case 3:
                skinFactor = 0.5;
                break;
            case 4:
                skinFactor = 0.6;
                break;
            case 5:
                skinFactor = 0.7;
                break;
            default:
                skinFactor = 0.8;
                break;
        }
        altitudeFactor = 1 + ((altitude/1000) * 0.1);
        formula_time_raw= ((spfD * skinFactor) / (uv * altitudeFactor)) * 60;
        return formula_time_raw;
        //if the formula is less than 2 and spf spf > 15 then auto to make formula_time_raw 2 hours
        /*
        if ((formula_time_raw < 120) && (spfD >= 15) ) {
            formula_time_raw = 120;
            return formula_time_raw*60;
        } else if (formula_time_raw >= 360) {
            formula_time_raw= 360;
            return formula_time_raw*60;
        } else {
            return (spfD * skinFactor) / (uv * altitudeFactor) * 60;
        }

         */
    }

    private void setupLineChart() {
        // Disable description text
        Description description = new Description();
        description.setText("Your UVI");
        lineChart.setDescription(description);

        // Enable touch gestures
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);

        // Customize X-axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f); // set the minimum value
        xAxis.setAxisMaximum(10f); // set the maximum value
        xAxis.setEnabled(false); // Disable X-axis numbers


        // Customize Y-axis
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMinimum(0f); // start at zero
        leftAxis.setAxisMaximum(10f); // the axis maximum

        // Customize Y-axis
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false); // Disable the right Y-axis

        // Initialize LineDataSet with empty data
        dataSet = new LineDataSet(entries, "UV Index");
        dataSet.setDrawCircles(false);
        dataSet.setLineWidth(2f);
        dataSet.setColor(Color.RED);

        // Add LineDataSet to LineData
        LineData lineData = new LineData(dataSet);

        // Set data to the chart
        lineChart.setData(lineData);

        // Refresh chart
        lineChart.invalidate();
    }

    // Method to add a new data point to the graph
    private void addEntry(int value) {
        // Add the new data point
        if (entries.size() >= 10) {
            entries.remove(0); // Remove the oldest entry
        }

        // Add the new entry at the end
        entries.add(new Entry(entries.size(), value));

        // Initialize LineDataSet with updated data
        dataSet = new LineDataSet(entries, "UV Index");
        dataSet.setDrawCircles(false);
        dataSet.setLineWidth(2f);
        dataSet.setColor(Color.RED);

        // Add LineDataSet to LineData
        LineData lineData = new LineData(dataSet);

        // Set data to the chart
        lineChart.setData(lineData);

        // Refresh chart
        lineChart.invalidate();
    }

    private void disconnect() {
        if (getContext() == null) {
            // Fragment is not attached to a context. Cannot proceed.
            return;
        }

        if (bluetoothGatt != null) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                //stopScan();
                Log.d("MyApp", "DISCONNECT");
                bluetoothGatt.disconnect();
                //stopRandomNumberGeneration();
            }
            else {
                Toast.makeText(getContext(), "Permissions not granted 1.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void stopScan() {
        if (getContext() == null) {
            // Fragment is not attached to a context. Cannot proceed.
            return;
        }
        // Check if BLE Scanner is not null and if it is currently scanning
        if (bluetoothLeScanner != null) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
                bluetoothLeScanner.stopScan(scanCallback);
                // Remove any pending callbacks and messages from the handler
                mHandler.removeCallbacksAndMessages(null);
            }
            else {
                Toast.makeText(getContext(), "Permissions not granted 2.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void scanDevices() {
        if (getContext() == null) {
            // Fragment is not attached to a context. Cannot proceed.
            return;
        }

        Log.d("MyApp", "Scanning starting... [1]");
        // Start scanning for device
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
            Log.d("MyApp", "Scanning starting... [2]");
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            if (bluetoothLeScanner == null) {
                Toast.makeText(getContext(), "Failed to obtain Bluetooth scanner", Toast.LENGTH_SHORT).show();
                return;
            }

            // Define scan filter criteria
            List<ScanFilter> filters = new ArrayList<>();
            ScanFilter.Builder filterBuilder = new ScanFilter.Builder();

            // Filter by device name
            filterBuilder.setDeviceName("UVSensor");
            filters.add(filterBuilder.build());

            // Filter by Service UUIDS
            ParcelUuid serviceUUID = ParcelUuid.fromString("19B10000-E8F2-537E-4F6C-D104768A1214");
            filterBuilder.setServiceUuid(serviceUUID);
            filters.add(filterBuilder.build());

            // Create Scan settings
            ScanSettings scanSettings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                    .build();

            // Get Bluetooth LE Scanner
            bluetoothLeScanner.startScan(filters, scanSettings, scanCallback);
            Log.d("MyApp", "Scanning starting... [3]");

            //Post a Delayed runnable
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopScan();
                }
            }, SCAN_TIMEOUT_MS);
        } else {
            Toast.makeText(getContext(), "Could not start scan", Toast.LENGTH_SHORT).show();
        }
    }

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                Log.d("MyApp", "Test [2]");
                BluetoothDevice device = result.getDevice();
                String deviceName = "Connected to: " + device.getName();
                textView.setText(deviceName);

                String alt = "Altitude: " + getAltitude() + "m";
                textView4.setText(alt);

                Toast.makeText(requireContext(), "Found UV Sensor", Toast.LENGTH_SHORT).show();
                bluetoothGatt = device.connectGatt(requireContext(), false, bluetoothGattCallback);

                scanButton.setVisibility(View.INVISIBLE);
                stopButton.setVisibility(View.VISIBLE);
                scanButton.setAnimation(btnLeaveBottom);
                stopButton.setAnimation(btnAnim);
                // Remove

                stopScan();
            }
            else {
                Toast.makeText(requireContext(), "Permissions not granted 3.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void stopRandomNumberGeneration() {
        if (isRandomGenerationActive) {
            randomTimer.cancel();
            randomTimer.purge();
            isRandomGenerationActive = false;
        }
    }

    private void startRandomNumberGeneration() {
        if (!isRandomGenerationActive) {
            isRandomGenerationActive = true;
            randomTimer = new Timer();
            randomTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    // Generate and display random number
                    final int randomNumber = generateRandomNumber();
                    String uvString = "UV Index: " + String.valueOf(randomNumber);
                    textView3.setText(uvString);
                    addEntry(randomNumber);
                }
            }, 0, 30000); // Start immediately and run every 30 seconds
        }
    }

    // Method to generate random number
    private int generateRandomNumber() {
        // Generate random number as per your requirements
        return (int) (Math.random() * 3) + 6; // Example: Generates a random number between 0 and 100
    }

    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    // Connected to the device. Discover services
                    gatt.discoverServices();
                }
                else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    // Handle disconnection
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText("Device disconnected");
                            stopButton.setVisibility(View.INVISIBLE);
                            scanButton.setVisibility(View.VISIBLE);
                            stopButton.startAnimation(btnLeaveBottom);
                            scanButton.startAnimation(btnAnim);
                            Log.d("MyApp", "DISCONNECT");
                            Log.d("MyApp", "DISCONNECT2");

                        }
                    });
                }
            }
            else {
                Toast.makeText(requireContext(), "Permissions not granted 4.", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    // Services have been discovered. Read in Data
                    BluetoothGattService service = gatt.getService(UUID.fromString("19B10000-E8F2-537E-4F6C-D104768A1214"));
                    if (service != null) {
                        BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString("19B10001-E8F2-537E-4F6C-D104768A1214"));
                        if (characteristic != null) {
                            Log.d("MyApp", "Data reading");
                            gatt.readCharacteristic(characteristic);
                        }
                    }
                }
            }
            else {
                Toast.makeText(requireContext(), "Permissions not granted 5.", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // Data has been read successfully. Continue processing HERE
                byte[] data = characteristic.getValue();
                int uvValue =  bytesToInt(data);
                Log.d("MyApp", "READ VALUE: " + String.valueOf(uvValue));
                //String uviString = "UV Index: " + uvValue;
                //textView3.setText(uviString);

                // Send the Acknowledge to the BLE service
                ///int valueToSend = 1; // ACK Signal
                //writeCharacteristic(gatt, valueToSend);
            }
            else {
                int valueToSend = 0; // No ACK Signal
                writeCharacteristic(gatt, valueToSend);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Toast.makeText(requireContext(), "Sent ACK", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(requireContext(), "Failed to send ACK", Toast.LENGTH_SHORT).show();
            }
        }
    };

    // Write characteristic
    private void writeCharacteristic(BluetoothGatt gatt, int value) {
        BluetoothGattService service = gatt.getService(UUID.fromString("19B10000-E8F2-537E-4F6C-D104768A1214"));
        if (service != null) {
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString("19B10001-E8F2-537E-4F6C-D104768A1214"));
            if (characteristic != null) {
                byte[] data = intToBytes(value);
                characteristic.setValue(data);
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                    gatt.writeCharacteristic(characteristic);
                }
                else {
                    Toast.makeText(requireContext(), "Permissions not granted 6.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //Conversion from BLE Byte Array to Integer
    private int bytesToInt(byte[] bytes) {
        int value = 0;
        for (int i = 0; i < bytes.length; ++i) {
            value += (bytes[i] & 0xFF) << (8 * i);
        }
        return value;
    }

    private byte[] intToBytes(int value) {
        byte[] result = new byte[4];
        for (int i = 0; i < 4; i++) {
            result[i] = (byte) (value >> (i * 8));
        }
        return result;
    }

    private void checkPermissions() {
        if (requireContext() == null) {
            // Fragment is not attached to a context. Cannot proceed.
            return;
        }
        // Check if all permissions are granted
        boolean allPermissionsGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                break;
            }
        }

        if (allPermissionsGranted) {
            getAltitude();
            initializeBluetooth();
        }
        else {
            requestPermissions(permissions, PERMISSION_REQUEST_CODE);
        }
    }

    private void initializeBluetooth() {
        BluetoothManager bluetoothManager = (BluetoothManager) requireContext().getSystemService(Context.BLUETOOTH_SERVICE);

        if (bluetoothManager != null) {
            bluetoothAdapter = bluetoothManager.getAdapter();
        }
        else {
            Toast.makeText(getContext(), "Bluetooth Manager is not available", Toast.LENGTH_SHORT).show();
            requireActivity().finish();
            return;
        }
        // Check if Bluetooth is supported on the device
        if (bluetoothAdapter == null) {
            Toast.makeText(getContext(), "Bluetooth is not supported on this device", Toast.LENGTH_SHORT).show();
            requireActivity().finish();
            return;
        }

        // Check if Bluetooth is enabled.
        if (!bluetoothAdapter.isEnabled()) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                // Prompt the user to enable Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            else {
                Toast.makeText(getContext(), "Permissions not granted 7.", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            scanDevices();
        }
    }

    private int getAltitude() {
        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permissions not granted, handle accordingly
            Toast.makeText(requireContext(), "Location permissions not granted", Toast.LENGTH_SHORT).show();
            return 1; // Return default value or handle the lack of permissions
        } else {
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setInterval(10000); // Set the interval for location updates (in milliseconds)
            locationRequest.setFastestInterval(5000); // Set the fastest interval for location updates (in milliseconds)
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // Set the priority for location accuracy

            // Use the fused location provider client to request location updates
            LocationServices.getFusedLocationProviderClient(requireContext())
                    .requestLocationUpdates(locationRequest, new LocationCallback() {
                        @Override
                        public void onLocationResult(@NonNull LocationResult locationResult) {
                            super.onLocationResult(locationResult);
                            Location location = locationResult.getLastLocation();
                            if (location != null) {
                                // Location obtained successfully, use it
                                int a = (int) location.getAltitude();
                                if (a < 1) {
                                    a = 1;
                                }
                                // Update UI or perform any action with the new location
                                String alt = "Altitude: " + a + "m";
                                textView4.setText(alt);
                            } else {
                                // Location not available, handle accordingly
                                Toast.makeText(requireContext(), "Location not available", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, Looper.getMainLooper());
        }

        return 0; // Return 0 or any default value as requestLocationUpdates is asynchronous
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                scanDevices();
            } else {
                Toast.makeText(getContext(), "Bluetooth is not enabled. Scan cannot commence.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                boolean allPermissionsGranted = true;
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        allPermissionsGranted = false;
                        break;
                    }
                }

                if (allPermissionsGranted) {
                    getAltitude();
                    initializeBluetooth();
                }
                else {
                    Toast.makeText(requireContext(), "Permissions Denied. Scan cannot commence.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void updateData(String newName, int newspf, int newSkinType) {
        name = "Welcome " + newName;
        spfVal = newspf;
        skinTypeVal = newSkinType;
    }
}
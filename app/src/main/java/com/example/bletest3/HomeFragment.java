package com.example.bletest3;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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
import java.util.Random;
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
    private static final long SCAN_TIMEOUT_MS = 30000; // 30 Second Timer

    // Bluetooth Declarations
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private BluetoothGatt bluetoothGatt;

    // Handlers
    private Handler mHandler = new Handler();
    private Handler handler;
    private Handler handler3;

    // UI Related Declarations
    Button applyButton, scanButton, stopButton, testButton;
    TextView bleTextView, uvTextView, altitudeTextView, timeLeftView, nameTextView;
    Animation btnAnim, btnLeaveRight, btnLeaveBottom;
    private LineChart lineChart;
    private List<Entry> entries = new ArrayList<>();
    private LineDataSet dataSet;
    private ProgressBar progressBar;
    private Timer timer;
    private Runnable runnable;
    private Random random;

    // Other Variables
    String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT};
    private String name = "Welcome!";
    private double totalTime, currentTime, uv, altitude;
    private CountDownTimer countDownTimer;
    private int spfVal, skinTypeVal;
    private boolean savedData = false;

    //  Updates the ProgressBar when the application is minimized.
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("timer_tick")) {
                long currentTime = intent.getLongExtra("currentTime", 0);
                updateProgressBar(currentTime);
            }
        }
    };

    public HomeFragment() {
        // Required empty public constructor
    }

    // Fragment Constructor that allows for inputs. This ensures any savedData is applied to
    // variables upon restarting.
    public HomeFragment(String name, int spfVal, int skinTypeVal) {
        this.name = "Welcome " + name + "!";
        this.spfVal = spfVal;
        this.skinTypeVal = skinTypeVal;
        savedData = true;
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
        // Assigns the UI Variables to their respective elements in the XML File
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        scanButton = view.findViewById(R.id.scanButton);
        stopButton = view.findViewById(R.id.stopButton);
        applyButton = view.findViewById(R.id.applyButton);
        bleTextView = view.findViewById(R.id.bleTextView);
        uvTextView = view.findViewById(R.id.uvTextView);
        testButton = view.findViewById(R.id.testButton);
        testButton.setVisibility(INVISIBLE);
        altitudeTextView = view.findViewById(R.id.altitudeTextView);
        nameTextView = view.findViewById(R.id.textViewName);

        lineChart = view.findViewById(R.id.lineChart);
        timeLeftView = view.findViewById(R.id.timeLeftTextView);
        progressBar = view.findViewById(R.id.progressBar);

        // Prepares the Animations
        btnAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.button_enter_top);
        btnLeaveRight = AnimationUtils.loadAnimation(getActivity(), R.anim.button_leave_right);
        btnLeaveBottom = AnimationUtils.loadAnimation(getActivity(), R.anim.button_leave_down);

        // Hides the buttons that have not met their conditions to be usable.
        stopButton.setVisibility(INVISIBLE);
        applyButton.setVisibility(INVISIBLE);

        // Initialize the LineChart
        progressBar.setVisibility(INVISIBLE);
        handler = new Handler();
        currentTime = totalTime;

        handler3 = new Handler();
        random = new Random();


        // If there is any saved data, update the necessary UI elements to present it to the viewer.
        if (savedData) {
            nameTextView.setText(name);
            applyButton.setVisibility(VISIBLE);
            applyButton.setAnimation(btnAnim);
        }

        /*
        if (name.equals("Welcome Testing!")) {
            testButton.setVisibility(VISIBLE);
        }
         */

        // Customize the LineChart
        setupLineChart();
        lineChart.setVisibility(INVISIBLE);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                altitudeTextView.setText("Altitude: 46m");
                // Check if Testing
                if (name.equals("Welcome Testing!")) {
                    String testString = "Connected to: UVSensor";
                    bleTextView.setText(testString);

                    Toast.makeText(requireContext(), "Found UV Sensor", Toast.LENGTH_SHORT).show();

                    // Hides the Scan Button, as the application is now testing responses to the sensor
                    // Allows for use of the Stop (Disconnect) Button.
                    scanButton.setVisibility(INVISIBLE);
                    stopButton.setVisibility(VISIBLE);
                    scanButton.setAnimation(btnLeaveBottom);
                    stopButton.setAnimation(btnAnim);
                }

                else {
                    // Check if permissions have been granted.
                    checkPermissions();
                }


            }
        });

        // Disconnects the UVSensor.
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(name.equals("Welcome Testing!"))) {
                    disconnect();
                    Log.d("MyApp", "DISCONNECT");
                }
                // Takes the spot of the Scan button.
                stopButton.setVisibility(INVISIBLE);
                scanButton.setVisibility(VISIBLE);
                stopButton.startAnimation(btnLeaveBottom);
                scanButton.startAnimation(btnAnim);
            }
        });

        // Begins the Sunscreen Application - Timer and ProgressBar
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View View) {
                lineChart.setVisibility(VISIBLE);
                if (name.equals("Welcome Testing!")) {
                    // Only use random generation for testing mode
                    startRandomNumberGeneration();
                    // Use a handler to delay timer start until we have UV data
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setTimer(skinTypeVal, spfVal);
                            startTimer();
                        }
                    }, 1000); // Wait 1 second for first random UV value
                } else {
                    setTimer(skinTypeVal, spfVal);
                    startTimer();
                    startForegroundService(totalTime);
                }
            }
        });

        // Hidden Testing button used to generate UV Indices when the UVSensor cannot be used.
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRandomNumberGeneration();
                //altitudeTextView.setText("Altitude: 42m");
            }
        });
        return view;
    }

    // Generates random integers to be used as UV Index
    private void startRandomNumberGeneration() {
        if (runnable != null) {
            // Remove pending callbacks to prevent multiple executions
            handler3.removeCallbacks(runnable);
        }

        runnable = new Runnable() {
            @Override
            public void run() {
                // Generate a random number
                uv = random.nextInt(10) + 1;
                // Update the TextView and Line Chart with the UV Index
                String uvString = "UV Index: " + uv;
                uvTextView.setText(uvString);
                addEntry((int) uv);
                // Schedule the next number to be generated after 5 seconds
                handler3.postDelayed(this, 5000);
            }
        };
        // Execute the runnable for the first time
        handler3.post(runnable);
    }

    // Sets the initial values for the ProgressBar / Timer
    private void setTimer(int skinTypeVal, int spfVal) {
        String textboxString = uvTextView.getText().toString();
        String numericSubstring = textboxString.substring("UV Index: ".length()); // Extract substring containing the numeric value
        double uvIndex = Double.parseDouble(numericSubstring); // Parse the numeric substring to double

        //textboxString = altitudeTextView.getText().toString();
        //numericSubstring = textboxString.substring("Altitude: ".length()); // Extract substring containing the numeric value
        //double altitude = Double.parseDouble(numericSubstring); // Parse the numeric substring to double
        altitude = 42.0;
        totalTime = formula((int) skinTypeVal, spfVal, uvIndex, altitude);
        currentTime = totalTime;
        progressBar.setMax((int) currentTime * 100);
        timeLeftView.setVisibility(VISIBLE);
        progressBar.setVisibility(VISIBLE);
    }

    // Starts the timer using the formula.
    private void startTimer() {
        countDownTimer = new CountDownTimer((long) (totalTime * 1000), 100) {
            @Override
            public void onTick(long l) {
                // formula
                String textboxString = uvTextView.getText().toString();
                String numericSubstring = textboxString.substring("UV Index: ".length()); // Extract substring containing the numeric value
                double uvIndex = Double.parseDouble(numericSubstring); // Parse the numeric substring to double
                int skin = 2;
                int spf = 30;
                altitude = 19.0;
                double x = formula(skin, spf, uvIndex, altitude);
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
                String time;
                if (hour > 8) {
                    time = "Estimated Time Remaining\n" + "∞∞:∞∞:∞∞";
                }
                else {
                    time = "Estimated Time Remaining\n" + String.format("%02d:%02d:%02d", hour, minute, second);
                }
                timeLeftView.setText(time);
                double timePercentage = currentTime / totalTime;
            }

            // Stop the timer and hide the Timer and the ProgressBar
            @Override
            public void onFinish() {
                timeLeftView.setVisibility(INVISIBLE);
                progressBar.setVisibility(INVISIBLE);
                countDownTimer.cancel();
            }
        }.start();
    }

    // Update the "Estimated Time Left" when the app is minimized.
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

    // When the user returns to the HomeFragment.
    @Override
    public void onResume() {
        super.onResume();
        // Register BroadcastReceiver
        IntentFilter intentFilter = new IntentFilter("timer_tick");
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadcastReceiver, intentFilter);
    }

    // When the user leaves the HomeFragment
    @Override
    public void onPause() {
        super.onPause();
        // Unregister BroadcastReceiver
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver);
    }

    // Starts the timer to run in the background when the app is minimized.
    private void startForegroundService(double totalTime) {
        Intent serviceIntent = new Intent(requireContext(), TimerForegroundService.class);
        serviceIntent.putExtra("totalTime", totalTime);
        ContextCompat.startForegroundService(requireContext(), serviceIntent);
    }

    // Update the progress bar
    private void updateProgressBar(long currentTime) {
        // Update progress bar
        int progress = (int) ((totalTime - currentTime) * 100 / totalTime);
        progressBar.setProgress(progress);
    }

    // Destroys the timer when it is complete.
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    // Formula that calculates the estimated safe sun exposure time. It is presented in
    // Hours : Minutes : Seconds
    protected double formula(int skinType, int spfInt, double uv, double altitude) {
        double skinFactor;
        double altitudeFactor;
        double spfD = (double) spfInt;
        double formula_time_raw;
        // Applies a skinFactor depending on the selected skinType
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
        // Applies an altitude factor. Roughly 1.10%
        altitudeFactor = 1 + ((altitude / 1000) * 0.1);

        formula_time_raw = ((spfD * skinFactor) / (uv * altitudeFactor)) * 60;
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

    // Prepares for LineChart
    private void setupLineChart() {
        // Disable description text
        Description description = new Description();
        description.setText("Your UVI");
        lineChart.setDescription(description);

        // Enable Drag Gestures.
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);
        lineChart.setPinchZoom(false);

        // Disables the X axis from showing.
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
            // Remove the oldest entry
            entries.remove(0);

            // Shift existing entries.
            for (int i = 0; i < entries.size(); i++) {
                Entry entry = entries.get(i);
                entry.setX(i);
            }
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

    // Checks if Disconnection is valid.
    private void disconnect() {
        if (getContext() == null) {
            // Fragment is not attached to a context. Cannot proceed.
            return;
        }

        // Ensures there is a bluetooth connection established.
        if (bluetoothGatt != null) {
            stopTimer2();
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                Log.d("MyApp", "DISCONNECT");
                bluetoothGatt.disconnect();
            } else {
                Toast.makeText(getContext(), "Permissions not granted 1.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Stops scanning once the 30 seconds have passed.
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
            } else {
                Toast.makeText(getContext(), "Permissions not granted 2.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Scans for BLE Device that meets the specific criteria.
    private void scanDevices() {
        if (getContext() == null) {
            // Fragment is not attached to a context. Cannot proceed.
            return;
        }

        // Start scanning for device
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            if (bluetoothLeScanner == null) {
                Toast.makeText(getContext(), "Failed to obtain Bluetooth scanner", Toast.LENGTH_SHORT).show();
                return;
            }

            // Define scan filter criteria
            List<ScanFilter> filters = new ArrayList<>();
            ScanFilter.Builder filterBuilder = new ScanFilter.Builder();

            // Ensures only BLE Devices under the name "UVSensor" are recognized.
            filterBuilder.setDeviceName("UVSensor");
            filters.add(filterBuilder.build());

            // Ensures only the specified Service UUID is recognized.
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

    // Upon receiving a BLE Device that matched the filters above
    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                // Assign the BLE Device to the application. Update UI to show this.
                BluetoothDevice device = result.getDevice();
                String deviceName = "Connected to: " + device.getName();
                bleTextView.setText(deviceName);

                Toast.makeText(requireContext(), "Found UV Sensor", Toast.LENGTH_SHORT).show();
                bluetoothGatt = device.connectGatt(requireContext(), false, bluetoothGattCallback);

                // Hides the Scan Button, as the application has now connected to a BLE Device.
                // Allows for use of the Stop (Disconnect) Button.
                scanButton.setVisibility(INVISIBLE);
                stopButton.setVisibility(VISIBLE);
                scanButton.setAnimation(btnLeaveBottom);
                stopButton.setAnimation(btnAnim);

                startDelayedTask();

                stopScan();
            } else {
                Toast.makeText(requireContext(), "Permissions not granted 3.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    // 3 Second Delay
    private void startDelayedTask() {
        Handler handler4 = new Handler();
        handler4.postDelayed(new Runnable() {
            @Override
            public void run() {
                startTimer2();
                Log.d("MyApp", "Task executed after 3 seconds");
            }
        }, 3000); // 3000 ms
    }

    // 0.5 Second Delay
    private void startDelayedTaskElectricBoogaloo() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("MyApp", "Task executed after 3 seconds");
            }
        }, 500); // 500 ms
    }

    // Handle BLE Methods
    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        // If the BLE Connectio has changed:
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    // Connected to the device. Discover services
                    gatt.discoverServices();
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    // Handle disconnection
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Hides the Stop Function, Allows for Scanning again.
                            bleTextView.setText("Device disconnected");
                            stopButton.setVisibility(INVISIBLE);
                            scanButton.setVisibility(VISIBLE);
                            stopButton.startAnimation(btnLeaveBottom);
                            scanButton.startAnimation(btnAnim);
                        }
                    });
                }
            } else {
                Toast.makeText(requireContext(), "Permissions not granted 4.", Toast.LENGTH_SHORT).show();
            }
        }

        // Upon discovering services.
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
            } else {
                Toast.makeText(requireContext(), "Permissions not granted 5.", Toast.LENGTH_SHORT).show();
            }
        }

        // Whenever the BLE Characteristic is successfully read.
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // Data has been read successfully. Continue processing HERE
                byte[] data = characteristic.getValue();
                int uvValue = bytesToInt(data);
                Log.d("MyApp", "READ VALUE: " + String.valueOf(uvValue));
                String uviString = "UV Index: " + uvValue;
                uvTextView.setText(uviString);
                addEntry(uvValue);
            } else {
                int valueToSend = 0; // No ACK Signal
            }
        }

        // Whenever the characteristic is successfully written to.
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                gatt.readCharacteristic(characteristic);
            }
            else {
                Log.d("MyApp", "LLLLL");
            }
        }
    };

    // Method that handles writing to the characteristic.
    private void writeCharacteristic(BluetoothGatt gatt, int value) throws InterruptedException {
        BluetoothGattService service = gatt.getService(UUID.fromString("19B10000-E8F2-537E-4F6C-D104768A1214"));
        if (service != null) {
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString("19B10001-E8F2-537E-4F6C-D104768A1214"));
            if (characteristic != null) {
                byte[] data = intToBytes(value);
                characteristic.setValue(data);
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                    gatt.writeCharacteristic(characteristic);
                    startDelayedTaskElectricBoogaloo();
                    gatt.readCharacteristic(characteristic);
                }
                else {
                    Toast.makeText(requireContext(), "Permissions not granted 6.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void startTimer2() {
        // Initialize the Timer object
        timer = new Timer();

        // Schedule the TimerTask to run every 10 minutes (600,000 milliseconds)
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    // Call your method to write and read the characteristic
                    Log.d("MyApp", "TRYING TO SEND 100");
                    int value = 100;
                    writeCharacteristic(bluetoothGatt, value);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 5000); // 0 milliseconds delay, 600000 milliseconds interval (10 minutes)
    }

    // Method to stop the timer
    private void stopTimer2() {
        // Cancel the TimerTask
        if (timer != null) {
            timer.cancel();
            timer = null;
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

    // Conversion from Integer to BLE Byte Array
    private byte[] intToBytes(int value) {
        byte[] result = new byte[4];
        for (int i = 0; i < 4; i++) {
            result[i] = (byte) (value >> (i * 8));
        }
        return result;
    }

    // Check if the application has been granted all permissions. If not, request for them.
    private void checkPermissions() {
        if (requireContext() == null) {
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

    // Prepares the Bluetooth and BLE variables for use. If Successful, start scanning for BLE Devices.
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

    // Retrieve the Altitude from the user's location.
    private void getAltitude() {
        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permissions not granted, handle accordingly
            Toast.makeText(requireContext(), "Location permissions not granted", Toast.LENGTH_SHORT).show();
            String alt = "Altitude: 1m";// Return default value or handle the lack of permissions
            altitudeTextView.setText(alt);
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
                                altitudeTextView.setText(alt);
                            } else {
                                // Location not available, handle accordingly
                                Toast.makeText(requireContext(), "Location not available", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, Looper.getMainLooper());
        }
    }

    // When requesting for Bluetooth:
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            // If successful, start scanning..
            if (resultCode == Activity.RESULT_OK) {
                scanDevices();
            } else {
                Toast.makeText(getContext(), "Bluetooth is not enabled. Scan cannot commence.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Determines how to proceed depending on whether or not the permissions have been granted or not.
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

    // Updates UI Elements to show saved data.
    public void updateData(String newName, int newspf, int newSkinType) {
        applyButton.setVisibility(VISIBLE);
        applyButton.setAnimation(btnAnim);
        name = "Welcome " + newName + "!";
        nameTextView.setText(name);
        spfVal = newspf;
        skinTypeVal = newSkinType;
    }
}
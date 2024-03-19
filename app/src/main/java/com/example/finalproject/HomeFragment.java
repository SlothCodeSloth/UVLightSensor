package com.example.finalproject;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import android.app.NotificationChannel;
import android.app.NotificationManager;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class HomeFragment extends Fragment {
    private static final String CHANNEL_ID = "your_channel_id";
    private static final int NOTIFICATION_ID = 1; // Unique ID for the notification
    private static final int PERMISSION_REQUEST_CODE = 123; // You can choose any value for the request code

    SharedPreferences userInfo;
    private Button button, button2, button3;
    private EditText spfTextView, skinTextView, uvTextView, altitudeTextView;
    private TextView timeLeftView, nameTextView;
    private int spf, skin;
    private double totalTime, currentTime, uv, altitude;
    private CountDownTimer countDownTimer;
    private ProgressBar progressBar;
    private Handler handler;
    private String name;

    // Version 1.1
    public static HomeFragment newInstance(int skinType, int spf, String name) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putInt("skinType", skinType);
        args.putInt("spf", spf);
        args.putString("name", name);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        // Create the notification channel (for Android 8.0 and higher)
        CharSequence name = "Channel Name";
        String description = "Channel Description";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = requireActivity().getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    // Method to show a notification based on variable values
    private void showNotification() {
        if (uv > 7) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                    //.setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("Notification Title")
                    .setContentText("Notification Content")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Permission not granted, request it
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
                // The onRequestPermissionsResult() method will handle the result
            } else {
                // Permission already granted, show the notification
                notificationManager.notify(NOTIFICATION_ID, builder.build());
            }
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            // Check if the permission request was for VIBRATE
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, show the notification
                showNotification();
            } else {
                // Permission denied, show a message or handle the denial
                Toast.makeText(requireContext(), "Permission denied. Cannot show notification.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Version 1.1
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        button = view.findViewById(R.id.button);
        button2 = view.findViewById(R.id.buttonSub);
        button3 = view.findViewById(R.id.buttonAdd);

        spfTextView = view.findViewById(R.id.spfTextView);
        skinTextView = view.findViewById(R.id.skinTextView);
        nameTextView = view.findViewById(R.id.textViewName);
        uvTextView = view.findViewById(R.id.uvTextView);
        altitudeTextView = view.findViewById(R.id.altitudeTextView);
        timeLeftView = view.findViewById(R.id.timeLeftTextView);
        timeLeftView.setVisibility(View.INVISIBLE);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        handler = new Handler();
        currentTime = totalTime;

        // Check if arguments are not null
        Bundle args = getArguments();
        if (args != null) {
            // Retrieve data from arguments
            skin = args.getInt("skinType", 0);
            spf = args.getInt("spf", 0);
            name = args.getString("name", "");

            // Update EditText fields with retrieved data
            skinTextView.setText(String.valueOf(skin));
            spfTextView.setText(String.valueOf(spf));
            String greeting = "Welcome " + name + "!";
            nameTextView.setText(greeting);
            uvTextView.setText("3");
            altitudeTextView.setText("1042");
            savePrefsData();
            // You can update other EditText fields as well if needed
        }

        if (restorePrefData()) {
            name = userInfo.getString("name", "");
            spf = userInfo.getInt("spf", 0);
            skin = userInfo.getInt("skinType", 0);
            skinTextView.setText(String.valueOf(skin));
            spfTextView.setText(String.valueOf(spf));
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View View) {
                setTimer();
                startTimer();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View View) {
                double temp = Double.parseDouble(uvTextView.getText().toString());
                temp = temp == 1.0 ? 1.0 : temp - 1.0;
                uvTextView.setText(String.valueOf((int) temp));
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View View) {
                double temp = Double.parseDouble(uvTextView.getText().toString());
                temp = temp == 11.0 ? 11.0 : temp + 1.0;
                uvTextView.setText(String.valueOf((int) temp));
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.frameLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        return view;
    }

    private void setTimer() {
        uv = Double.parseDouble(uvTextView.getText().toString());
        altitude = Double.parseDouble(altitudeTextView.getText().toString());
        totalTime= formula((int) skin,spf,uv,altitude);
        //totalTime = ((skin * spf) / (uv * altitude)) * 60;
        currentTime = totalTime;
        progressBar.setMax((int) currentTime * 100);
        timeLeftView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer((long) (totalTime * 1000), 100) {
            @Override
            public void onTick(long l) {
                //spf = Integer.parseInt(spfTextView.getText().toString());
                //skin = Integer.parseInt(skinTextView.getText().toString());
                uv = Double.parseDouble(uvTextView.getText().toString());
                altitude = Double.parseDouble(altitudeTextView.getText().toString());

                // formula
                double x = formula(skin,spf,uv,altitude);
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

        altitudeFactor = 1 + (altitude/1000) * 0.1;
        formula_time_raw= (spfD * skinFactor) / (uv * altitudeFactor) * 60;
        //if the formula is less than 2 and spf spf > 15 then auto to make formula_time_raw 2 hours
        if ((formula_time_raw < 120) && (spfD >= 15) ) {
            formula_time_raw = 120;
            //Toast.makeText(getContext(), "Estimate time < 2", Toast.LENGTH_SHORT).show();
            return formula_time_raw*60;
        } else if (formula_time_raw >= 360) {
            //we estimate that sunscreen wearsout by 6th hour
            formula_time_raw= 360;
            //Toast.makeText(getContext(), "Estimate time > 6, reapply by 6th hour", Toast.LENGTH_SHORT).show();
            return formula_time_raw*60;
        } else {
            return (spfD * skinFactor) / (uv * altitudeFactor) * 60;
        }
    }

    private void savePrefsData() {
        SharedPreferences userInfo = getActivity().getSharedPreferences("userInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = userInfo.edit();
        editor.putBoolean("hasData", true);
        editor.putString("name", name);
        editor.putInt("spf", spf);
        editor.putInt("skinType", skin);
        editor.apply();
    }

    private boolean restorePrefData() {
        userInfo = getActivity().getSharedPreferences("userInfo", MODE_PRIVATE);
        boolean hasData = userInfo.getBoolean("hasData", false);
        return hasData;
    }
}
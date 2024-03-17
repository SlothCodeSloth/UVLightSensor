package com.example.finalproject;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    private Button button, button2, button3;
    private EditText spfTextView, skinTextView, uvTextView, altitudeTextView;
    private TextView timeLeftView;
    private double totalTime, currentTime, spf, skin, uv, altitude;
    private CountDownTimer countDownTimer;
    private ProgressBar progressBar;
    private Handler handler;

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
        uvTextView = view.findViewById(R.id.uvTextView);
        altitudeTextView = view.findViewById(R.id.altitudeTextView);
        timeLeftView = view.findViewById(R.id.timeLeftTextView);
        timeLeftView.setVisibility(View.INVISIBLE);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        handler = new Handler();
        currentTime = totalTime;

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
        spf = Double.parseDouble(spfTextView.getText().toString());
        skin = Double.parseDouble(skinTextView.getText().toString());
        uv = Double.parseDouble(uvTextView.getText().toString());
        altitude = Double.parseDouble(altitudeTextView.getText().toString());
        totalTime = ((skin * spf) / (uv * altitude)) * 60;
        currentTime = totalTime;
        progressBar.setMax((int) currentTime * 10);
        timeLeftView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer((long) (totalTime * 1000), 100) {
            @Override
            public void onTick(long l) {
                spf = Double.parseDouble(spfTextView.getText().toString());
                skin = Double.parseDouble(skinTextView.getText().toString());
                uv = Double.parseDouble(uvTextView.getText().toString());
                altitude = Double.parseDouble(altitudeTextView.getText().toString());

                double x = ((skin * spf) / (uv * altitude)) * 60;
                x = totalTime / x;
                currentTime -= 0.1 * x;
                updateTimerText();
                progressBar.setProgress((int) currentTime * 10, true);

                int hour, minute, second;
                double temp = currentTime / x;
                hour = temp > 3600 ? (int) temp / 3600 : 0;
                temp -= hour * 3600;
                minute = temp > 60 ? (int) temp / 60 : 0;
                temp -= minute * 60;
                second = temp > 1 ? (int) temp : 0;
                String time = "Estimated Time Remaining: " + String.format("%02d:%02d:%02d", hour, minute, second);
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

    protected double formula(int skinType, double spf, double uv, double altitude) {
        double skinFactor;
        double altitudeFactor;
        switch (skinType) {
            case 1:
                skinFactor = 0.3;
                break;
            case 2:
                skinFactor = 0.5;
                break;
            case 3:
                skinFactor = 0.6;
                break;
            case 4:
                skinFactor = 0.8;
                break;
            case 5:
                skinFactor = 0.9;
                break;
            default:
                skinFactor = 1.0;
                break;
        }

        altitudeFactor = 1 + (altitude/1000) * 0.1;
        return (spf * skinFactor) / (uv * altitudeFactor);
    }
}

package com.example.websitetest;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    // Declarations
    private Button button, button2, button3;
    private TextView spfTextView, skinTextView, uvTextView,
            altitudeTextView, timeLeftView;
    private double totalTime, currentTime, spf, skin, uv, altitude;
    int skinType;
    private double timeElapsed = 0;
    private CountDownTimer countDownTimer;
    private ProgressBar progressBar;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Assign buttons
        button = findViewById(R.id.button);
        button2 = findViewById(R.id.buttonSub);
        button3 = findViewById(R.id.buttonAdd);

        // When the first button is tapped, start and set the timer.
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View View) {
                setTimer();
                startTimer();
            }
        });

        // When the second button is tapped, reduce the UV input by 1, going no lower than 1.
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View View) {
                double temp = (double) Integer.parseInt(uvTextView.getText().toString());
                temp = temp == 1.0 ? 1.0 : temp - 1.0;
                uvTextView.setText(String.valueOf((int) temp));
            }
        });

        // When the third button is tapped, increase the UV input by 1, going no higher than 11.
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View View) {
                double temp = (double) Integer.parseInt(uvTextView.getText().toString());
                temp = temp == 11.0 ? 11.0 : temp + 1.0;
                uvTextView.setText(String.valueOf((int) temp));
            }
        });

        // Assign TextViews
        spfTextView = findViewById(R.id.spfTextView);
        skinTextView = findViewById(R.id.skinTextView);
        uvTextView = findViewById(R.id.uvTextView);
        altitudeTextView = findViewById(R.id.altitudeTextView);
        timeLeftView = findViewById(R.id.timeLeftTextView);
        timeLeftView.setVisibility(View.INVISIBLE);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        handler = new Handler();
        currentTime = totalTime;

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }



    private void setTimer() {
        spf = (double) Integer.parseInt(spfTextView.getText().toString());
        skin = (double) Integer.parseInt(skinTextView.getText().toString());
        uv = (double) Integer.parseInt(uvTextView.getText().toString());
        altitude = (double) Integer.parseInt(altitudeTextView.getText().toString());
        totalTime = ((skin * spf) / (uv * altitude)) * 60;
        double y = totalTime;
        currentTime = totalTime;
        progressBar.setMax((int) currentTime * 10);
        timeLeftView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }
    private void startTimer() {
        countDownTimer = new CountDownTimer((long) (totalTime * 1000), 100) {
            @Override
            public void onTick(long l) {
                // Retrieve all variables from inputs. Convert from String to Double
                spf = (double) Integer.parseInt(spfTextView.getText().toString());
                skin = (double) Integer.parseInt(skinTextView.getText().toString());
                uv = (double) Integer.parseInt(uvTextView.getText().toString());
                altitude = (double) Integer.parseInt(altitudeTextView.getText().toString());

                // Formula to determine safe sun exposure time.
                double x = ((skin * spf) / (uv * altitude)) * 60;
                x = totalTime / x;
                currentTime -= 0.1 * x;
                timeElapsed += 0.1;
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
    protected void onDestroy() {
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

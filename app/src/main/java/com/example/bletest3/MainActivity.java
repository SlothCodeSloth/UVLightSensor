package com.example.bletest3;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private ViewPager2 screenPager;
    IntroViewPagerAdapter introViewPagerAdapter;
    TabLayout tabIndicator;
    Button btnNxt;
    int position = 0;
    Button btnGetStarted;
    Button btnGetPermissions;
    Animation btnAnim, btnLeaveRight, btnLeaveBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // If the Introduction has already been viewed, skip upon next launch.
        if (restorePrefData()) {
            Intent mainActivity = new Intent(getApplicationContext(), MainActivity2.class);
            startActivity(mainActivity);
            finish();
        }
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        WindowInsetsController insetsController = getWindow().getInsetsController();
        if (insetsController != null) {
            insetsController.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
        }

        btnNxt = findViewById(R.id.btn_next);
        btnGetStarted = findViewById(R.id.btn_get_started);
        tabIndicator = findViewById(R.id.tab_indicator);
        btnGetPermissions = findViewById(R.id.btn_get_permissions);
        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_animation);
        btnLeaveRight = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_leave_right);
        btnLeaveBottom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_leave_down);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Prepares the Introductory Activity. Adds the Title, Description, and Image for each screen.
        List<ScreenItem> mList = new ArrayList<>();
        mList.add(new ScreenItem("The Invisible Danger: UV rays!", "Without proper protection\n long term exposure to UV\n can harm our skin. ", R.drawable.sunwglases));
        mList.add(new ScreenItem("Your Skin's Best Friend: Sunscreen! ", "It protects your skin from\n sunburn, premature aging, and \n possible risk of skin cancer.", R.drawable.spf));
        mList.add(new ScreenItem("But When Do I reapply?", "Link your device to your \n phone for  sunscreen \n reapplication reminders!", R.drawable.hourglass));

        screenPager = findViewById(R.id.screen_viewpager);
        introViewPagerAdapter = new IntroViewPagerAdapter(this, mList);
        screenPager.setAdapter(introViewPagerAdapter);

        new TabLayoutMediator(tabIndicator, screenPager,
                (tab, position) -> {}
        ).attach();

        // Handles the user pressing "next" rather than swiping to get to the next screen.
        btnNxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                position = screenPager.getCurrentItem();
                if (position < mList.size()) {
                    position++;
                    screenPager.setCurrentItem(position);
                }

                if (position == mList.size() - 1) {
                    loadLastScreen();
                }
            }
        });

        // Updates the Tab Bullet Points to indicate what tab(screen) the user is in.
        tabIndicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            // If last tab, make it visible that it is the last screen.
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == mList.size() - 1) {
                    loadLastScreen();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // Requests permissions when the button is pressed.
        btnGetPermissions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermissions();
            }
        });

        // Upon granting permissions, allow the user to proceed to the MainActivity2 (Functional Parts)
        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainActivity = new Intent (getApplicationContext(), MainActivity.class);
                startActivity(mainActivity);
                savePrefsData();
                finish();
            }
        });
    }

    // Determines if the user has viewed the intro or not.
    private boolean restorePrefData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        boolean isIntroActivityOpenedBefore = pref.getBoolean("isIntroOpened", false);
        return isIntroActivityOpenedBefore;
    }

    // Saves whether the user has viewed the intro or not.
    private void savePrefsData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isIntroOpened", true);
        editor.apply();
    }

    // If Loading the last screen, hide some UI elements and show the Get Permissions button.
    private void loadLastScreen() {
        btnNxt.setAnimation(btnLeaveRight);
        btnNxt.setVisibility(View.INVISIBLE);
        btnGetPermissions.setVisibility(View.VISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);
        btnGetPermissions.setAnimation(btnAnim);
    }

    // Prompt the user for permissions.
    private void requestPermissions() {
        if (checkPermissions()) {
            showLast();
        }
        else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN},
                    PERMISSION_REQUEST_CODE);
        }
    }

    // Checks if the following permissions have been granted or not.
    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED;

    }

    // If permissions are given, proceed to the last screen.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (checkPermissions()) {
                showLast();
            }
            else {
                Toast.makeText(this, "Permission denied. Cannot show notification.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Show the last screen.
    private void showLast() {
        btnGetPermissions.setAnimation(btnLeaveBottom);
        btnGetPermissions.setVisibility(View.INVISIBLE);

        btnGetStarted.setVisibility(View.VISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);
        btnGetStarted.setAnimation(btnAnim);
    }
}

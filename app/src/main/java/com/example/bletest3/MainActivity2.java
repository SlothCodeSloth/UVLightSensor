package com.example.bletest3;

import android.content.res.Configuration;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
public class MainActivity2 extends AppCompatActivity {

    ChipNavigationBar chipNavigationBar;
    ViewPager2 viewPager;
    FragmentPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fragment_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        chipNavigationBar = findViewById(R.id.bottom_nav_menu);
        viewPager = findViewById(R.id.view_pager);

        pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(), getLifecycle());
        pagerAdapter.addFragment(new HomeFragment());
        pagerAdapter.addFragment(new ProfileFragment());
        viewPager.setAdapter(pagerAdapter);
        chipNavigationBar.setItemSelected(R.id.home, true);
        bottomMenu();

        int currentMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        boolean isNightMode = currentMode == Configuration.UI_MODE_NIGHT_YES;

        // Set background color for each menu item based on mode
        if (isNightMode) {
            // Night mode colors

        } else {
            // Light mode colors

        }

        // Add a listener to update the selected item in ChipNavigationBar when swiping
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        chipNavigationBar.setItemSelected(R.id.home, true);
                        break;
                    case 1:
                        chipNavigationBar.setItemSelected(R.id.profile, true);
                        break;
                }
            }
        });
    }

    private void bottomMenu() {
        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                Fragment fragment = null;
                if (i == R.id.home) {
                    viewPager.setCurrentItem(0);
                }
                else if (i == R.id.profile) {
                    viewPager.setCurrentItem(1);
                }
            }
        });
    }

}
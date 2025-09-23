package com.example.bletest3;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;
// This class handles the fragments inside of the Bottom Navigation Menu.
public class FragmentPagerAdapter extends FragmentStateAdapter {
    // Prepares an ArrayList to allow for Dynamic Fragment adding.
    private List<Fragment> fragments = new ArrayList<>();

    public FragmentPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    // Add a fragment to the list.
    public void addFragment(Fragment fragment) {
        fragments.add(fragment);
    }

    // Creates a fragment at a specific location in the list.
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    // Retrieve how many fragments there are.
    @Override
    public int getItemCount() {
        return fragments.size();
    }

    // Method to retrieve a fragment by position
    public Fragment getFragment(int position) {
        return fragments.get(position);
    }
}

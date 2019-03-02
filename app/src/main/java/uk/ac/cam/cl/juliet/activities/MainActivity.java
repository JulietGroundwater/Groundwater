package uk.ac.cam.cl.juliet.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import com.microsoft.identity.client.PublicClientApplication;
import java.util.ArrayList;
import java.util.List;
import uk.ac.cam.cl.juliet.R;
import uk.ac.cam.cl.juliet.data.AuthenticationManager;
import uk.ac.cam.cl.juliet.fragments.DataFragmentWrapper;
import uk.ac.cam.cl.juliet.fragments.DisplayFragment;
import uk.ac.cam.cl.juliet.fragments.SettingsFragment;
import uk.ac.cam.cl.juliet.fragments.ToggleableSwipeViewPager;

/** The home screen for the application. */
public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    private final int READ_CONSTANT = 1;
    private final int OFF_SCREEN_LIMIT = 10;
    private BottomNavigationView bottomNavigation;
    private DisplayFragment displayFragment;
    private DataFragmentWrapper dataFragment;
    private SettingsFragment settingsFragment;
    private FragmentManager fragmentManager;
    private ToggleableSwipeViewPager viewPager;
    private List<PermissionListener> permissionListeners;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();

        // Create an instance of each fragment
        displayFragment = new DisplayFragment();
        dataFragment = new DataFragmentWrapper();
        settingsFragment = new SettingsFragment();

        // Set up a ViewPager to handle displaying the three Fragments
        viewPager = findViewById(R.id.contentViewPager);
        viewPager.setAllowSwiping(false);
        BottomNavigationPagerAdapter adapter =
                new BottomNavigationPagerAdapter(
                        getSupportFragmentManager(),
                        displayFragment,
                        dataFragment,
                        settingsFragment);
        viewPager.setAdapter(adapter);

        // Set up the handler for the bottom navigation
        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnNavigationItemSelectedListener(this);
        bottomNavigation.setSelectedItemId(R.id.action_info);

        // To avoid needlessly re-rendering fragments when switching
        viewPager.setOffscreenPageLimit(OFF_SCREEN_LIMIT);

        // Create listener list
        permissionListeners = new ArrayList<>();

        // Get user permission to access file system
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(
                                this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[] {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    READ_CONSTANT);
        }
    }

    /**
     * MSAL requires the calling app to pass an Activity which MUST call this method to get the auth
     * code passed back correctly
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PublicClientApplication clientApp = AuthenticationManager.getInstance().getPublicClient();
        if (clientApp != null) {
            clientApp.handleInteractiveRequestRedirect(requestCode, resultCode, data);
        }
    }

    /**
     * Handles switching to the selected screen.
     *
     * @param menuItem The item that was selected
     * @return Whether the action has been handled
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_info:
                viewPager.setCurrentItem(0, false);
                setTitle(R.string.title_display);
                return true;
            case R.id.action_data:
                viewPager.setCurrentItem(1, false);
                setTitle(R.string.title_data);
                return true;
            case R.id.action_settings:
                viewPager.setCurrentItem(2, false);
                setTitle(R.string.title_settings);
                return true;
        }
        return false;
    }

    /** Displays the info screen containing charts. */
    public void showChartScreen(boolean singlePlot) {
        bottomNavigation.setSelectedItemId(R.id.action_info);
        if (singlePlot) {
            displayFragment.showSinglePlotScreen();
        } else {
            displayFragment.showCollectionPlotScreen();
        }
    }

    /**
     * Adds a listener to the list
     *
     * @param listener the <code>PermissionKListener</code> to add
     */
    public void addListener(PermissionListener listener) {
        permissionListeners.add(listener);
    }

    /** Serves Fragments to the ViewPager. */
    private static class BottomNavigationPagerAdapter extends FragmentPagerAdapter {

        private Fragment[] contents;

        public BottomNavigationPagerAdapter(FragmentManager fm, Fragment... fragments) {
            super(fm);
            contents = fragments;
        }

        @Override
        public Fragment getItem(int i) {
            return contents[i];
        }

        @Override
        public int getCount() {
            return contents.length;
        }
    }

    /**
     * Method called if we are granted access to the filesystem - iterate over listeners and call
     * the method
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case READ_CONSTANT:
                {
                    for (PermissionListener listener : permissionListeners) {
                        listener.onPermissionGranted();
                    }
                }
        }
    }

    public interface PermissionListener {
        void onPermissionGranted();
    }
}

package uk.ac.cam.cl.juliet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import com.microsoft.identity.client.PublicClientApplication;
import uk.ac.cam.cl.juliet.R;
import uk.ac.cam.cl.juliet.data.AuthenticationManager;
import uk.ac.cam.cl.juliet.fragments.DataFragment;
import uk.ac.cam.cl.juliet.fragments.DisplayFragment;
import uk.ac.cam.cl.juliet.fragments.SettingsFragment;
import uk.ac.cam.cl.juliet.fragments.ToggleableSwipeViewPager;

/**
 * The home screen for the application.
 *
 * @author Ben Cole
 */
public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigation;
    private DisplayFragment displayFragment;
    private DataFragment dataFragment;
    private SettingsFragment settingsFragment;
    private FragmentManager fragmentManager;
    private ToggleableSwipeViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();

        // Create an instance of each fragment
        displayFragment = new DisplayFragment();
        dataFragment = new DataFragment();
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
        // TODO: Find way to not have to recreate Fragment objects
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
}

package uk.ac.cam.cl.juliet.activities;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import uk.ac.cam.cl.juliet.R;
import uk.ac.cam.cl.juliet.fragments.DataFragment;
import uk.ac.cam.cl.juliet.fragments.InfoFragment;
import uk.ac.cam.cl.juliet.fragments.SettingsFragment;

/**
 * The home screen for the application.
 *
 * @author Ben Cole
 */
public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigation;
    InfoFragment infoFragment;
    DataFragment dataFragment;
    SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create an instance of each fragment
        infoFragment = new InfoFragment();
        dataFragment = new DataFragment();
        settingsFragment = new SettingsFragment();

        // Set up the handler for the bottom navigation
        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnNavigationItemSelectedListener(this);
        bottomNavigation.setSelectedItemId(R.id.action_info);
        switchToFragment(infoFragment);
    }

    /**
     * Changes the displayed fragment in the activity's content area.
     *
     * @param fragment The fragment to change to.
     */
    private void switchToFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, fragment);
        transaction.commit();
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
                switchToFragment(infoFragment);
                return true;
            case R.id.action_data:
                switchToFragment(dataFragment);
                return true;
            case R.id.action_settings:
                switchToFragment(settingsFragment);
                return true;
        }
        return false;
    }
}

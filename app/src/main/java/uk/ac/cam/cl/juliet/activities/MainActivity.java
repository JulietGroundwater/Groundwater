package uk.ac.cam.cl.juliet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import com.microsoft.identity.client.PublicClientApplication;
import uk.ac.cam.cl.juliet.R;
import uk.ac.cam.cl.juliet.data.AuthenticationManager;
import uk.ac.cam.cl.juliet.fragments.DataFragment;
import uk.ac.cam.cl.juliet.fragments.DisplayFragment;
import uk.ac.cam.cl.juliet.fragments.SettingsFragment;

/**
 * The home screen for the application.
 *
 * @author Ben Cole
 */
public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigation;
    DisplayFragment displayFragment;
    DataFragment dataFragment;
    SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create an instance of each fragment
        displayFragment = new DisplayFragment();
        dataFragment = new DataFragment();
        settingsFragment = new SettingsFragment();

        // Set up the handler for the bottom navigation
        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnNavigationItemSelectedListener(this);
        bottomNavigation.setSelectedItemId(R.id.action_info);
        switchToFragment(displayFragment);
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
     * MSAL requires the calling app to pass an Activity which MUST call this method to get the auth code passed back correctly
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
                switchToFragment(new DisplayFragment());
                return true;
            case R.id.action_data:
                switchToFragment(new DataFragment());
                return true;
            case R.id.action_settings:
                switchToFragment(new SettingsFragment());
                return true;
        }
        return false;
    }
}

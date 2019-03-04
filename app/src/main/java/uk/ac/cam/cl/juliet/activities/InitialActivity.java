package uk.ac.cam.cl.juliet.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

/**
 * Invisibly runs when the app first launches and decides which visible Activity to start the user
 * on.
 *
 * <p>If the user has already given storage permissions then an instance of <code>MainActivity
 * </code> will be started; otherwise an instance of <code>RequestPermissionsActivity</code> will be
 * started.
 */
public class InitialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent;
        if (alreadyHasPermissions()) {
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, RequestPermissionsActivity.class);
        }
        startActivity(intent);
        finish();
    }

    /**
     * Determines whether the user has already given file access permissions.
     *
     * @return true if the user already has given permission; false otherwise
     */
    private boolean alreadyHasPermissions() {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED);
    }
}

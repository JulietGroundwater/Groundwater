package uk.ac.cam.cl.juliet.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import uk.ac.cam.cl.juliet.R;

/**
 * Shows a dialog requesting permissions for the app.
 *
 * <p>Once the user has given permission, an instance of <code>MainActivity</code> will be started
 * and this <code>Activity</code> will finish.
 */
public class RequestPermissionsActivity extends AppCompatActivity {

    /** Used internally for calls to <code>ActivityCompat.requestPermissions</code>. */
    private static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_permissions);
        Button updatePermissionsButton = findViewById(R.id.updatePermissionsButton);
        updatePermissionsButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        performRequestPermissions();
                    }
                });
        performRequestPermissions();
    }

    /** Starts an instance of <code>MainActivity</code> and finishes this <code>Activity</code>. */
    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Requests storage (read and write) permissions.
     *
     * <p>If we do not have the requested permissions then a dialog will be shown to the user.
     */
    private void performRequestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[] {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.INTERNET
                },
                REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startMainActivity();
                }
        }
    }
}

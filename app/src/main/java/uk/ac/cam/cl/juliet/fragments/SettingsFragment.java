package uk.ac.cam.cl.juliet.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import uk.ac.cam.cl.juliet.R;

/**
 * Fragment for the 'settings' screen.
 *
 * @author Ben Cole
 */
public class SettingsFragment extends Fragment {

    private Switch usePhoneDateSwitch;
    private Switch usePhoneTimeSwitch;
    private Switch usePhoneGPSSwitch;
    private TextView connectionStatusText;
    private ImageView connectionStatusIcon;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        usePhoneDateSwitch = view.findViewById(R.id.usePhoneDateSwitch);
        usePhoneDateSwitch.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (!isChecked) {
                            showSetDateDialog();
                        }
                    }
                });
        usePhoneTimeSwitch = view.findViewById(R.id.usePhoneTimeSwitch);
        usePhoneTimeSwitch.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (!isChecked) {
                            showSetTimeDialog();
                        }
                    }
                });
        usePhoneGPSSwitch = view.findViewById(R.id.useThisPhoneGPSSwitch);
        usePhoneGPSSwitch.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (!isChecked) {
                            showSetLocationDialog();
                        }
                    }
                });

        connectionStatusText = view.findViewById(R.id.connectionStatusText);
        connectionStatusIcon = view.findViewById(R.id.connectionStatusImageView);

        setConnectedStatus(getConnectionStatus());
        return view;
    }

    private boolean getConnectionStatus() {
        // TODO: Implement
        return true;
    }

    private void setConnectedStatus(boolean connected) {
        // TODO: Colour the message with success/fail
        if (connected) {
            connectionStatusText.setText(R.string.connected);
            connectionStatusIcon.setImageResource(R.drawable.baseline_wifi_black_24);
        } else {
            connectionStatusText.setText(R.string.disconnected);
            connectionStatusIcon.setImageResource(R.drawable.baseline_wifi_off_black_24);
        }
    }

    private void showSetTimeDialog() {
        // TODO: implement
    }

    private void showSetDateDialog() {
        // TODO: implement
    }

    private void showSetLocationDialog() {
        // TODO: implement
    }
}

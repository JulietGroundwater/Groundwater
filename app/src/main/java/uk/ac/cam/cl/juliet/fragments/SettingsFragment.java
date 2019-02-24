package uk.ac.cam.cl.juliet.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import uk.ac.cam.cl.juliet.R;
import uk.ac.cam.cl.juliet.computationengine.Config;
import uk.ac.cam.cl.juliet.computationengine.InvalidConfigException;
import uk.ac.cam.cl.juliet.data.AttenuatorSettings;
import uk.ac.cam.cl.juliet.dialogs.AttenuatorsDialog;

/**
 * Fragment for the 'settings' screen.
 *
 * @author Ben Cole
 */
public class SettingsFragment extends Fragment
        implements AttenuatorsDialog.OnAttenuatorsSelectedListener, Button.OnClickListener {

    private static double ATTENUATION_MIN = 2;
    private static double ATTENUATION_MAX = 10;

    private Switch usePhoneGPSSwitch;
    private TextView connectionStatusText;
    private ImageView connectionStatusIcon;
    private Button setDateButton;
    private Button setTimeButton;
    private Button configureAttenuatorsButton;

    private List<Integer> attenuators;
    private List<Integer> gains;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

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

        // Find the buttons and set this class as the click listener
        setDateButton = view.findViewById(R.id.setDateButton);
        setDateButton.setOnClickListener(this);
        setTimeButton = view.findViewById(R.id.setTimeButton);
        setTimeButton.setOnClickListener(this);
        configureAttenuatorsButton = view.findViewById(R.id.configureAttenuatorsButton);
        configureAttenuatorsButton.setOnClickListener(this);

        attenuators = new ArrayList<>();
        gains = new ArrayList<>();

        setConnectedStatus(getConnectionStatus());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            getActivity()
                    .getWindow()
                    .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private boolean getConnectionStatus() {
        // TODO: Implement
        return true;
    }

    private void setConnectedStatus(boolean connected) {
        if (connected) {
            connectionStatusText.setText(R.string.connected);
            connectionStatusText.setTextColor(getResources().getColor(R.color.success));
            connectionStatusIcon.setImageResource(R.drawable.baseline_wifi_black_24);
            connectionStatusIcon.setColorFilter(getResources().getColor(R.color.success));
        } else {
            connectionStatusText.setText(R.string.disconnected);
            connectionStatusText.setTextColor(getResources().getColor(R.color.failure));
            connectionStatusIcon.setImageResource(R.drawable.baseline_wifi_off_black_24);
            connectionStatusIcon.setColorFilter(getResources().getColor(R.color.failure));
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

    private void showAttenuatorsDialog() {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager == null) return;
        AttenuatorsDialog dialog = new AttenuatorsDialog();
        dialog.setOnAttenuatorsSelectedListener(this);
        AttenuatorSettings attenuatorSettings = new AttenuatorSettings(attenuators, gains);
        Bundle arguments = new Bundle();
        arguments.putSerializable(AttenuatorsDialog.ATTENUATOR_SETTINGS, attenuatorSettings);
        dialog.setArguments(arguments);
        dialog.show(fragmentManager, "AttenuatorsDialog");
    }

    private Config generateConfig() {
        // TODO: Implement this
        // TODO: we need a way to build a config file by setting params, then let the Config class
        //       write those params to a file.
        try {
            Config config = new Config(null);
        } catch (InvalidConfigException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onAttenuatorsSelected(List<Integer> attenuators, List<Integer> gains) {
        this.attenuators = attenuators;
        this.gains = gains;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setDateButton:
                showSetDateDialog();
                break;
            case R.id.setTimeButton:
                showSetTimeDialog();
                break;
            case R.id.configureAttenuatorsButton:
                showAttenuatorsDialog();
        }
    }
}

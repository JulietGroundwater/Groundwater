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
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import java.text.DecimalFormat;
import java.util.List;
import uk.ac.cam.cl.juliet.R;
import uk.ac.cam.cl.juliet.computationengine.Config;
import uk.ac.cam.cl.juliet.computationengine.InvalidConfigException;
import uk.ac.cam.cl.juliet.dialogs.AttenuatorsDialog;

/**
 * Fragment for the 'settings' screen.
 *
 * @author Ben Cole
 */
public class SettingsFragment extends Fragment
        implements AttenuatorsDialog.OnAttenuatorsSelectedListener {

    private static double ATTENUATION_MIN = 2;
    private static double ATTENUATION_MAX = 10;

    private Switch usePhoneDateSwitch;
    private Switch usePhoneTimeSwitch;
    private Switch usePhoneGPSSwitch;
    private TextView connectionStatusText;
    private ImageView connectionStatusIcon;
    private SeekBar attenuationSeekBar;
    private TextView attenuationValueOutput;

    private Button configureAttenuatorsButton;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        //        usePhoneDateSwitch = view.findViewById(R.id.usePhoneDateSwitch);
        //        usePhoneDateSwitch.setOnCheckedChangeListener(
        //                new CompoundButton.OnCheckedChangeListener() {
        //                    @Override
        //                    public void onCheckedChanged(CompoundButton buttonView, boolean
        // isChecked) {
        //                        if (!isChecked) {
        //                            showSetDateDialog();
        //                        }
        //                    }
        //                });
        //        usePhoneTimeSwitch = view.findViewById(R.id.usePhoneTimeSwitch);
        //        usePhoneTimeSwitch.setOnCheckedChangeListener(
        //                new CompoundButton.OnCheckedChangeListener() {
        //                    @Override
        //                    public void onCheckedChanged(CompoundButton buttonView, boolean
        // isChecked) {
        //                        if (!isChecked) {
        //                            showSetTimeDialog();
        //                        }
        //                    }
        //                });
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

        //        attenuationSeekBar = view.findViewById(R.id.attenuationSeekBar);
        //        attenuationValueOutput = view.findViewById(R.id.attenuationValueOutput);
        //        attenuationSeekBar.setOnSeekBarChangeListener(
        //                new SeekBar.OnSeekBarChangeListener() {
        //                    @Override
        //                    public void onProgressChanged(SeekBar seekBar, int progress, boolean
        // fromUser) {
        //                        attenuationValueOutput.setText(getFormattedAttenuationValue());
        //                    }
        //
        //                    @Override
        //                    public void onStartTrackingTouch(SeekBar seekBar) {}
        //
        //                    @Override
        //                    public void onStopTrackingTouch(SeekBar seekBar) {}
        //                });
        //        attenuationSeekBar.setProgress(attenuationSeekBar.getMax() / 2);

        configureAttenuatorsButton = view.findViewById(R.id.configureAttenuatorsButton);
        configureAttenuatorsButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAttenuatorsDialog();
                    }
                });

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

    /**
     * Computes the currently selected attenuation value, from <code>ATTENUATION_MAX</code> to
     * <code>ATTENUATION_MAX</code> as determined by the seek bar.
     *
     * @return The currently selected attenuation value
     */
    private double getAttenuationValue() {
        double r = (double) attenuationSeekBar.getProgress() / attenuationSeekBar.getMax();
        return ATTENUATION_MIN + r * (ATTENUATION_MAX - ATTENUATION_MIN);
    }

    private String getFormattedAttenuationValue() {
        return new DecimalFormat("#.00").format(getAttenuationValue());
    }

    private void setConnectedStatus(boolean connected) {
        // TODO: Colour the message with success/fail
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
        dialog.show(fragmentManager, "AttenuatorsDialog");
    }

    private Config generateConfig() {
        // TODO: Implement this
        // TODO: we need a way to build a config file by setting params, then let IT write those
        //       params to a file.
        try {
            Config config = new Config(null);
        } catch (InvalidConfigException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onAttenuatorsSelected(List<Integer> attenuators, List<Integer> gains) {
        // TODO: implement
    }
}

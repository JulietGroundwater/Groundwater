package uk.ac.cam.cl.juliet.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import uk.ac.cam.cl.juliet.R;
import uk.ac.cam.cl.juliet.connection.ConnectionSimulator;
import uk.ac.cam.cl.juliet.data.AttenuatorSettings;
import uk.ac.cam.cl.juliet.dialogs.AttenuatorsDialog;

/**
 * Fragment for the 'settings' screen.
 *
 * @author Ben Cole
 */
public class SettingsFragment extends Fragment
        implements AttenuatorsDialog.OnAttenuatorsSelectedListener, Button.OnClickListener {

    private TextView connectionStatusText;
    private ImageView connectionStatusIcon;
    private TextView selectedDateOutput;
    private TextView selectedTimeOutput;
    private TextView latitudeOutput;
    private TextView longitudeOutput;
    private Button setDateButton;
    private Button setTimeButton;
    private Button setGPSButton;
    private Button configureAttenuatorsButton;
    private Button sendToDeviceButton;
    private MenuItem connect;
    private MenuItem disconnect;

    private int minute;
    private int hourOfDay;
    private int day;
    private int month;
    private int year;
    private boolean locationSet;
    private double latitude;
    private double longitude;
    private boolean attenuatorsSet;
    private boolean connected;
    private List<Integer> attenuators;
    private List<Integer> gains;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        connectionStatusText = view.findViewById(R.id.connectionStatusText);
        connectionStatusIcon = view.findViewById(R.id.connectionStatusImageView);

        // Find the text views
        selectedDateOutput = view.findViewById(R.id.selectedDateText);
        selectedTimeOutput = view.findViewById(R.id.selectedTimeText);
        latitudeOutput = view.findViewById(R.id.latitudeText);
        longitudeOutput = view.findViewById(R.id.longitudeText);

        // Find the buttons and set this class as the click listener
        setDateButton = view.findViewById(R.id.setDateButton);
        setDateButton.setOnClickListener(this);
        setTimeButton = view.findViewById(R.id.setTimeButton);
        setTimeButton.setOnClickListener(this);
        setGPSButton = view.findViewById(R.id.setGPSButton);
        setGPSButton.setOnClickListener(this);
        configureAttenuatorsButton = view.findViewById(R.id.configureAttenuatorsButton);
        configureAttenuatorsButton.setOnClickListener(this);
        sendToDeviceButton = view.findViewById(R.id.sendToDeviceButton);
        sendToDeviceButton.setOnClickListener(this);

        setDefaultValues();
        setConnectedStatus(getConnectionStatus());
        updateSendToDeviceButtonEnabled();
        connected = false;
        this.setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_settings, menu);
        connect = menu.findItem(R.id.connect_button);
        disconnect = menu.findItem(R.id.disconnect_button);
        // Only have connect visible if we aren't running any data gathering
        toggleMenuItems();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.connect_button:
                establishConnection();
                return true;
            case R.id.disconnect_button:
                destroyConnection();
                return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        Activity activity = getActivity();
        if (activity == null) return;
        super.onResume();
        try {
            activity.getWindow()
                    .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private boolean getConnectionStatus() {
        return this.connected;
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

    /** Sets the default values for when the screen is first loaded. */
    private void setDefaultValues() {
        Calendar calendar = Calendar.getInstance();
        minute = calendar.get(Calendar.MINUTE);
        hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        onNewTimeSet(hourOfDay, minute);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
        onNewDateSet(year, month, day);
        attenuators = new ArrayList<>();
        attenuators.add(15);
        gains = new ArrayList<>();
        gains.add(-14);
        locationSet = false;
        attenuatorsSet = false;
        // TODO: look up device location and initialise to that
        latitudeOutput.setText(R.string.not_set);
        longitudeOutput.setText(R.string.not_set);
    }

    /** Displays a dialog for setting the time of the radar device. */
    private void showSetTimeDialog() {
        Context context = getContext();
        if (context == null) return;

        // Get current time
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog dialog =
                new TimePickerDialog(
                        context,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                onNewTimeSet(hourOfDay, minute);
                            }
                        },
                        hourOfDay,
                        minute,
                        false);

        dialog.show();
    }

    /**
     * Callback for when the dialog completes and a new time has been chosen.
     *
     * @param hour The hourOfDay of the day
     * @param minute The minute of the hourOfDay
     */
    private void onNewTimeSet(int hour, int minute) {
        this.hourOfDay = hour;
        this.minute = minute;
        // TODO: handle string formatting properly
        selectedTimeOutput.setText(hour + ":" + minute);
    }

    /** Displays a dialog for setting the date of the radar device. */
    private void showSetDateDialog() {
        Context context = getContext();
        if (context == null) return;

        // Get current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog =
                new DatePickerDialog(
                        context,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(
                                    DatePicker view, int year, int month, int dayOfMonth) {
                                onNewDateSet(year, month, dayOfMonth);
                            }
                        },
                        year,
                        month,
                        day);

        dialog.show();
    }

    /**
     * Callback for when the dialog completes and a new date has been chosen.
     *
     * @param year The year that was selected
     * @param month The month of the year
     * @param day The day of the month
     */
    private void onNewDateSet(int year, int month, int day) {
        // TODO: handle string formatting properly
        this.year = year;
        this.month = month;
        this.day = day;
        selectedDateOutput.setText(day + "/" + month + "/" + year);
    }

    private void showSetLocationDialog() {
        // TODO: implement
        Context context = getContext();
        if (context == null) return;
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_gps_coordinates);
        final TextView latitudeInput = dialog.findViewById(R.id.latitudeInput);
        final TextView longitudeInput = dialog.findViewById(R.id.longitudeInput);
        dialog.findViewById(R.id.setButton)
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.cancel();
                                processResultFromLocationDialog(
                                        latitudeInput.getText().toString(),
                                        longitudeInput.getText().toString());
                            }
                        });
        dialog.findViewById(R.id.cancelButton)
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.cancel();
                            }
                        });
        dialog.show();
    }

    /**
     * Processes the values entered by the user into the location selection dialog.
     *
     * <p>If the values are valid then the changes will be saved: otherwise an error message will be
     * displayed.
     *
     * @param latitudeStr The string containing the latitude input from the user
     * @param longitudeStr The string containing the longitude input from the user
     */
    private void processResultFromLocationDialog(String latitudeStr, String longitudeStr) {
        if (latitudeStr.isEmpty() || longitudeStr.isEmpty()) {
            showInvalidLocationDialog();
            return;
        }
        double latitude = Double.valueOf(latitudeStr);
        double longitude = Double.valueOf(longitudeStr);
        if ((-90 <= latitude) && (latitude <= 90) && (-180 <= longitude) && (longitude <= 180)) {
            this.latitude = latitude;
            this.longitude = longitude;
            latitudeOutput.setText(String.format(Locale.getDefault(), "%f", latitude));
            longitudeOutput.setText(String.format(Locale.getDefault(), "%f", longitude));
            locationSet = true;
        } else {
            showInvalidLocationDialog();
        }
    }

    /** Displays an error message to inform the user that that entered an invalid location. */
    private void showInvalidLocationDialog() {
        Toast.makeText(getContext(), R.string.invalid_gps_coords, Toast.LENGTH_SHORT).show();
    }

    /** Shows the dialog for setting the attenuators. */
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

    /** Handles packaging up the configuration settings and sending them to the device. */
    private void sendToDevice() {
        // TODO: Implement this
        // TODO: we need a way to build a config file by setting params, then let the Config class
        //       write those params to a file.
    }

    @Override
    public void onAttenuatorsSelected(List<Integer> attenuators, List<Integer> gains) {
        this.attenuators = attenuators;
        this.gains = gains;
        if (this.attenuators != null && this.gains != null) {
            attenuatorsSet = true;
            updateSendToDeviceButtonEnabled();
        }
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
            case R.id.setGPSButton:
                showSetLocationDialog();
                break;
            case R.id.configureAttenuatorsButton:
                showAttenuatorsDialog();
                break;
            case R.id.sendToDeviceButton:
                sendToDevice();
        }
    }

    /**
     * Recalculates whether or not the 'send to device' button is allowed to be enabled.
     *
     * <p>The button should be enabled when the device is connected and the location and attenuators
     * have all been set.
     */
    private void updateSendToDeviceButtonEnabled() {
        sendToDeviceButton.setEnabled(getConnectionStatus() && locationSet && attenuatorsSet);
    }

    private void establishConnection() {
        // We know we will have a good connection so change buttons
        this.connected = true;
        toggleMenuItems();

        ConnectionSimulator simulator = ConnectionSimulator.getInstance();
        simulator.connect();
        setConnectedStatus(getConnectionStatus());
    }

    private void destroyConnection() {
        // We know we will have a good connection so change buttons
        this.connected = false;
        toggleMenuItems();

        ConnectionSimulator simulator = ConnectionSimulator.getInstance();
        simulator.disconnect();
        setConnectedStatus(getConnectionStatus());
    }

    private void toggleMenuItems() {
        if (!connected) {
            connect.setVisible(true);
            connect.setEnabled(true);
            disconnect.setVisible(false);
            disconnect.setEnabled(false);
        } else {
            connect.setVisible(false);
            connect.setEnabled(false);
            disconnect.setVisible(true);
            disconnect.setEnabled(true);
        }
    }
}

package uk.ac.cam.cl.juliet.computationengine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Class for header of {@code .DAT} file from the radar, adapted from the Auto-pRES Manual page 12
 *
 * <p>Some variables were not mentioned in the manual and so they have been commented as such, and
 * their types have been inferred from their name
 */
public class Header {

    private int average,
            maxDataFileLength,
            maxDepthToGraph,
            nADCSamples,
            nAttenuators,
            nData,
            nSubBursts,
            repSecs,
            settleCycles,
            watchdogTaskSecs,
            maxSafFileLength, // NOT DEFINED IN MANUAL
            interChirpDelay, // NOT DEFINED IN MANUAL
            samplingFreqMode, // NOT DEFINED IN MANUAL
            ramp, // NOT DEFINED IN MANUAL
            noDwell, // NOT DEFINED IN MANUAL
            startFreq, // NOT DEFINED IN MANUAL
            stopFreq, // NOT DEFINED IN MANUAL
            freqStepUp, // NOT DEFINED IN MANUAL
            freqStepDn, // NOT DEFINED IN MANUAL
            burstNo, // NOT DEFINED IN MANUAL
            upTell; // NOT DEFINED IN MANUAL
    private boolean alwaysAttended,
            antennaSelect,
            checkEthernet,
            gpsOn,
            housekeeping,
            intervalMode,
            iridium,
            logOn,
            sleepMode,
            syncGPS,
            battSleep, // NOT DEFINED IN MANUAL
            isEthOn, // NOT DEFINED IN MANUAL
            isWebServerOn, // NOT DEFINED IN MANUAL
            isFTPServerOn; // NOT DEFINED IN MANUAL
    private double batteryVoltage,
            gpsTime,
            latitude,
            longitude,
            temp1,
            temp2,
            vm2Time,
            tStepUp, // NOT DEFINED IN MANUAL
            tStepDn; // NOT DEFINED IN MANUAL
    private Date timeStamp;
    private String reg00,
            reg01,
            reg02,
            reg0B,
            reg0C,
            reg0D,
            reg0E,
            rmbIssue,
            swIssue,
            vabIssue,
            venomIssue;
    private List<Integer> afGain = new ArrayList<>(),
            attenuator1 = new ArrayList<>(),
            rxAnt = new ArrayList<>(),
            triples = new ArrayList<>(),
            txAnt = new ArrayList<>(),
            batteryCheck = new ArrayList<>(); // NOT DEFINED IN MANUAL

    private List<String> headerLines = new ArrayList<>();

    public Header(File file) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(file));

        String line;
        while ((line = reader.readLine()) != null) {
            headerLines.add(line);

            if (line.contains("*** End Header ***")) break;

            if (!line.contains("=")) continue;

            String lhs = line.split("=")[0]; // left hand side
            String rhs = line.split("=")[1]; // right hand side

            // would use a switch statement here, but for Strings it requires java 7
            if (lhs.equals("Time stamp")) {
                DateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss", Locale.ENGLISH);
                try {
                    timeStamp = format.parse(rhs);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if (lhs.equals("RMB_Issue")) rmbIssue = rhs;
            else if (lhs.equals("VAB_Issue")) vabIssue = rhs;
            else if (lhs.equals("SW_Issue")) swIssue = rhs;
            else if (lhs.equals("Venom_Issue")) venomIssue = rhs;
            else if (lhs.equals("NSubBursts")) nSubBursts = Integer.parseInt(rhs);
            else if (lhs.equals("NData")) nData = Integer.parseInt(rhs);
            else if (lhs.equals("Triples"))
                for (String s : rhs.split(",")) triples.add(Integer.parseInt(s));
            else if (lhs.equals("Average")) average = Integer.parseInt(rhs);
            else if (lhs.equals("RepSecs")) repSecs = Integer.parseInt(rhs);
            else if (lhs.equals("CheckEthernet")) checkEthernet = (Integer.parseInt(rhs) != 0);
            else if (lhs.equals("N_ADC_SAMPLES")) nADCSamples = Integer.parseInt(rhs);
            else if (lhs.equals("MAX_DATA_FILE_LENGTH")) maxDataFileLength = Integer.parseInt(rhs);
            else if (lhs.equals("MAX_SAF_FILE_LENGTH")) maxSafFileLength = Integer.parseInt(rhs);
            else if (lhs.equals("ANTENNA_SELECT")) antennaSelect = (Integer.parseInt(rhs) != 0);
            else if (lhs.equals("nAttenuators")) nAttenuators = Integer.parseInt(rhs);
            else if (lhs.equals("Housekeeping")) housekeeping = (Integer.parseInt(rhs) != 0);
            else if (lhs.equals("GPSon")) gpsOn = (Integer.parseInt(rhs) != 0);
            else if (lhs.equals("SyncGPS")) syncGPS = (Integer.parseInt(rhs) != 0);
            else if (lhs.equals("Iridium")) iridium = (Integer.parseInt(rhs) != 0);
            else if (lhs.equals("WATCHDOG_TASK_SECS")) watchdogTaskSecs = Integer.parseInt(rhs);
            else if (lhs.equals("IntervalMode")) intervalMode = (Integer.parseInt(rhs) != 0);
            else if (lhs.equals("InterChirpDelay")) interChirpDelay = Integer.parseInt(rhs);
            else if (lhs.equals("Attenuator1"))
                for (String s : rhs.split(",")) attenuator1.add(Integer.parseInt(s));
            else if (lhs.equals("AFGain"))
                for (String s : rhs.split(",")) afGain.add(Integer.parseInt(s));
            else if (lhs.equals("TxAnt"))
                for (String s : rhs.split(",")) txAnt.add(Integer.parseInt(s));
            else if (lhs.equals("RxAnt"))
                for (String s : rhs.split(",")) rxAnt.add(Integer.parseInt(s));
            else if (lhs.equals("maxDepthToGraph")) maxDepthToGraph = Integer.parseInt(rhs);
            else if (lhs.equals("SleepMode")) sleepMode = (Integer.parseInt(rhs) != 0);
            else if (lhs.equals("LogOn")) logOn = (Integer.parseInt(rhs) != 0);
            else if (lhs.equals("Reg00")) reg00 = rhs;
            else if (lhs.equals("Reg01")) reg01 = rhs;
            else if (lhs.equals("Reg02")) reg02 = rhs;
            else if (lhs.equals("Reg0B")) reg0B = rhs;
            else if (lhs.equals("Reg0C")) reg0C = rhs;
            else if (lhs.equals("Reg0D")) reg0D = rhs;
            else if (lhs.equals("Reg0E")) reg0E = rhs;
            else if (lhs.equals("SamplingFreqMode")) samplingFreqMode = Integer.parseInt(rhs);
            else if (lhs.equals("Settle_Cycles")) settleCycles = Integer.parseInt(rhs);
            else if (lhs.equals("BatteryCheck"))
                for (String s : rhs.split(",")) batteryCheck.add(Integer.parseInt(s));
            else if (lhs.equals("Latitude")) latitude = Double.parseDouble(rhs);
            else if (lhs.equals("Longitude")) longitude = Double.parseDouble(rhs);
            else if (lhs.equals("GPS_Time")) gpsTime = Double.parseDouble(rhs);
            else if (lhs.equals("VM2_Time")) vm2Time = Double.parseDouble(rhs);
            else if (lhs.equals("Temp1")) temp1 = Double.parseDouble(rhs);
            else if (lhs.equals("Temp2")) temp2 = Double.parseDouble(rhs);
            else if (lhs.equals("BatteryVoltage")) batteryVoltage = Double.parseDouble(rhs);
            else if (lhs.equals("Ramp")) ramp = Integer.parseInt(rhs);
            else if (lhs.equals("NoDwell")) noDwell = Integer.parseInt(rhs);
            else if (lhs.equals("StartFreq")) startFreq = Integer.parseInt(rhs);
            else if (lhs.equals("StopFreq")) stopFreq = Integer.parseInt(rhs);
            else if (lhs.equals("FreqStepUp")) freqStepUp = Integer.parseInt(rhs);
            else if (lhs.equals("FreqStepDn")) freqStepDn = Integer.parseInt(rhs);
            else if (lhs.equals("TStepUp")) tStepUp = Double.parseDouble(rhs);
            else if (lhs.equals("TStepDn")) tStepDn = Double.parseDouble(rhs);
            else if (lhs.equals("BattSleep")) battSleep = (Integer.parseInt(rhs) != 0);
            else if (lhs.equals("BurstNo")) burstNo = Integer.parseInt(rhs);
            else if (lhs.equals("IsEthOn")) isEthOn = (Integer.parseInt(rhs) != 0);
            else if (lhs.equals("UpTell")) upTell = Integer.parseInt(rhs);
            else if (lhs.equals("IsWebServerOn")) isWebServerOn = (Integer.parseInt(rhs) != 0);
            else if (lhs.equals("IsFTPServerOn")) isFTPServerOn = (Integer.parseInt(rhs) != 0);
        }
    }

    /**
     * Returns the ASCII representation of the header directly from the file. may not be necessary
     * but available for raw output.
     *
     * @return ASCII string of the entire header
     */
    public String getASCII() {
        StringBuilder sb = new StringBuilder();
        for (String line : headerLines) {
            sb.append(line);
            sb.append('\n');
        }
        return sb.toString();
    }

    /**
     * Returns the timestamp from the header formatted as a {@code java.util.Date} object.
     *
     * @return timestamp
     */
    public Date getTimestamp() {
        return timeStamp;
    }

    /**
     * Returns RMB_Issue as a String
     *
     * @return rmbIssue
     */
    public String getRMBIssue() {
        return rmbIssue;
    }

    /**
     * Returns VAB_Issue as a String
     *
     * @return vabIssue
     */
    public String getVABIssue() {
        return vabIssue;
    }

    /**
     * Returns SW_Issue as a String. (could return as a Number based on the example in the manual)
     *
     * @return swIssue
     */
    public String getSWIssue() {
        return swIssue;
    }

    /**
     * Returns Venom_Issue as a String (could return as a Number based on the example in the manual)
     *
     * @return venomIssue
     */
    public String getVenomIssue() {
        return venomIssue;
    }

    /**
     * Returns Always_Attended as a boolean.
     *
     * <p>From manual page 9:
     *
     * <p>If set to one, this switch causes the web browser to be launched whether or not an active
     * Ethernet port is detected. This can be useful if the laptop keeps entering sleep mode, in
     * which case the Ethernet port will be seen by the radar as inactive.
     *
     * @return alwaysAttended
     */
    public boolean getAlwaysAttended() {
        return alwaysAttended;
    }

    /**
     * Returns NSubBursts as an int
     *
     * @return nSubBursts
     */
    public int getNSubBursts() {
        return nSubBursts;
    }

    /**
     * Returns nData as an int
     *
     * <p>From manual page 11:
     *
     * <p>If Iridium has been set to 1, and the instrument is in Unattended Mode, then an uplink is
     * attempted every Ndata bursts. So NData=1 will cause an uplink every burst. NData=24 will
     * cause a daily uplink if RepSecs is set to 3600 seconds. NData=0 will disable data uplinks.
     *
     * @return nData
     */
    public int getNData() {
        return nData;
    }

    /**
     * Returns Triples as a {@code List<Integer>} since the length is not necessarily known, though
     * the max number of triples is 4 (so 12 numbers maximum).
     *
     * <p>From manual page 11:
     *
     * <p>Defines which depth intervals are reported in the data uplinks. This consists of a series
     * of concatenated triplets of numbers on one line. Eg Triples=200,15,300,300,25,600,650,4,690.
     * Each triple is treated in a similar way to the Matlab xmin:dx:xmax syntax. So the first set
     * of depth intervals in the example would be 200 to 215 m, 215 to 230 m, 230 to 245 m etc up to
     * 275 to 290 m. The maximum number of triples is 4, and the maximum number of depth intervals
     * is 64. The example would report on 28 intervals in total. See section on data uptells for
     * further details, and for the content of the data messages
     *
     * @return copy of triples
     */
    public List<Integer> getTriples() {
        // Returns a shallow clone of the list
        return new ArrayList<>(triples);
    }

    /**
     * return Average as an int.
     *
     * <p>This can have values 0, 1, or 2.
     *
     * <p>Meanings from the manual page 10:
     *
     * <p>0 -{@literal >} All chirps as 2-byte unsigned numbers (default value).
     *
     * <p>1 -{@literal >} All chirps from the same burst are averaged and stored in the same format.
     *
     * <p>2 -{@literal >} All chirps from each burst are stacked (summed) and stored as a 4-byte
     * unsigned integer.
     *
     * @return average
     */
    public int getAverage() {
        return average;
    }

    /**
     * Returns RepSecs as an int.
     *
     * <p>From maunal page 10:
     *
     * <p>If IntervalMode = 0, ensure that the repetition interval is greater than the length of
     * time for the acquisition of an entire burst. If it is shorter, then the radar will continue
     * with an uninterrupted series of bursts, and it is likely that it will not be possible to
     * switch off the unit without losing the final open data file. To estimate the length of the
     * burst, assume that there will be at least 10 seconds of overheads associated with starting up
     * the radar. So the total time = NSubBursts * nAttenuators + 10 seconds. Setting IntervalMode
     * to 1 means that a RepSecs-long delay will be implemented between bursts. If the total time
     * for a burst lasts longer than the Watchdog timeout interval, which defaults to 3600 seconds,
     * the system will automatically reset itself. This would effectively limit the number of chirps
     * per burst to around 2180. Note that the Watchdog Timer timeout can be adjusted with the
     * WATCHDOC_TASK_SECS value, and that the Watchdog is disabled during Attended Mode operation.
     *
     * @return repSecs
     */
    public int getRepSecs() {
        return repSecs;
    }

    /**
     * Returns CheckEthernet as a boolean
     *
     * <p>From manual page 10:
     *
     * <p>If set to zero, the Ethernet port is not checked on power-up. This defaults to one.
     * Probably best left that way.
     *
     * @return checkEthernet
     */
    public boolean getCheckEthernet() {
        return checkEthernet;
    }

    /**
     * Returns N_ADC_Samples as an int. N_ADC_Samples is the number of samples per chirp recorded by
     * the ADC.
     *
     * <p>From manual page 10
     *
     * <p>determines the number of samples per chirp recorded by ADC. Under most circumstances this
     * should be left unset to allow the radar to pick its own value. For normal use, N_DC_SAMPLES
     * will be set to 40000 by the radar. Advanced users might want to change it if they are
     * reprogramming the DDS characteristics, but even then the radar will pick the most appropriate
     * value.
     *
     * @return nADCSamples
     */
    public int getNADCSamples() {
        return nADCSamples;
    }

    /**
     * Returns MAX_DATA_FILE_LENGTH as an int
     *
     * <p>From manual page 10:
     *
     * <p>Before each burst is recorded the length of the file is checked and, if greater than
     * MAX_DATA_FILE_LENGTH, a new file is started. Very large data files should be avoided for two
     * reasons: if for some reason the file is corrupted, fewer bursts will be lost if the file is
     * shorted; and very long files incur time overheads that can run to many seconds while the file
     * is opened and the end of file is found. Very short files are fine, but there will be a lot of
     * them. Each time unattended operation is initiated a new subdirectory is created on the card
     * to hold the files associated with the sequence of bursts
     *
     * @return maxDataFileLength
     */
    public int getMaxDataFileLength() {
        return maxDataFileLength;
    }

    /**
     * Returns ANTENNA_SELECT
     *
     * @return antennaSelect
     */
    public boolean getAntennaSelect() {
        return antennaSelect;
    }

    /**
     * Returns nAttenuators
     *
     * <p>From manual page 10:
     *
     * <p>this actually means the number of attenuator settings to be used, that is, the number of
     * chirps in a sub-burst (assuming not in MIMO mode – see below). If averaging is enabled
     * (Average ≠ 0), all chirps in a burst are averaged together. Averaging should not be used with
     * when multiple attenuator settings are used
     *
     * @return nAttenuators
     */
    public int getNAttenuators() {
        return nAttenuators;
    }

    /**
     * Returns Housekeeping
     *
     * <p>From manual page 11:
     *
     * <p>Relevant only in Unattended mode. Set to 0 by default. If set to 1, daily housekeeping
     * activities will take place at the end of the first burst after midnight. Iridium
     * communications will be attempted (see below), and the SD cards will be checked and any
     * detected errors will be repaired.
     *
     * @return housekeeping
     */
    public boolean getHousekeeping() {
        return housekeeping;
    }

    /**
     * Returns GPSon
     *
     * <p>From manual page 10:
     *
     * <p>Defaults to zero. If set to a non-zero integer, from 1 to 255, each time a burst is
     * initiated, the GPS module is activated. GPSon then indicates the time in seconds allowed for
     * a fix to be obtained. If successful, the GPS location, GPS time and the radar’s system time
     * at the time of the fix are stored in the data header for the burst on the SD card. Should be
     * set to zero if no GPS antenna is connected, or if there is some other reason to think a fix
     * will not be obtained. The module remembers the satellite constellation between fixes: after a
     * fix has been obtained, the module usually requires only a few seconds to reacquire the
     * satellites. So even if a long period is allowed by GPSon (eg 30 seconds), it would be
     * expected to take only a few seconds before each burst to get a fix after the first fix has
     * been obtained.
     *
     * @return gpsOn
     */
    public boolean getGPSon() {
        return gpsOn;
    }

    /**
     * Returns SyncGps
     *
     * <p>From manual page 11:
     *
     * <p>Relevant only if housekeeping is enabled. Default 0. If set to 1, during housekeeping
     * activities the radar's internal real-time clock will be synchronised with a valid GPS time.
     *
     * @return syncGPS
     */
    public boolean getSyncGPS() {
        return syncGPS;
    }

    /**
     * Returns Iridium
     *
     * <p>From manual page 11:
     *
     * <p>Relevant only if housekeeping or data uplinks are enabled. Default 1. If set to 1, an
     * exchange of Iridium SBD messages is attempted during housekeeping or data uplink activities.
     *
     * @return iridium
     */
    public boolean getIridium() {
        return iridium;
    }

    /**
     * Returns WATCHDOG_TASK_SECS
     *
     * @return watchDogTaskSecs
     */
    public int getWatchdogTaskSecs() {
        return watchdogTaskSecs;
    }

    /**
     * Returns IntervalMode
     *
     * @return intervalMode
     */
    public boolean getIntervalMode() {
        return intervalMode;
    }

    /**
     * Returns Attenuator1 as a {@code List<Integer>}
     *
     * <p>From manual page 10:
     *
     * <p>The values, in dB, for each of up to four settings for the attenuator. Only the first
     * nAttenuators values will be used.
     *
     * @return copy of attenuator1
     */
    public List<Integer> getAttenuator1() {
        // returns a shallow copy
        return new ArrayList<>(attenuator1);
    }

    /**
     * Returns AFGain as {@code List<Integer>}
     *
     * <p>From manual page 11:
     *
     * <p>The values, in dB, for up to four settings for the gain in the deramp amplifier. Only the
     * first nAttenuators values will be used. Values are either -14, -4 or +6 dB. If set to a
     * negative value less than -4, it will be coerced to -14 dB, if greater than -4 but negative,
     * -4 will be used. If zero or positive, it will be coerced to +6 dB.
     *
     * @return copy of afGain
     */
    public List<Integer> getAFGain() {
        // returns a shallow copy
        return new ArrayList<>(afGain);
    }

    /**
     * Returns TxAnt as {@code List<Integer>}
     *
     * @return copy of txAnt
     */
    public List<Integer> getTxAnt() {
        // returns a shallow copy
        return new ArrayList<>(txAnt);
    }

    /**
     * Returns RxAnt as {@code List<Integer>}
     *
     * @return copy of rxAnt
     */
    public List<Integer> getRxAnt() {
        // returns a shallow copy
        return new ArrayList<>(rxAnt);
    }

    /**
     * Returns maxDepthToGraph
     *
     * @return maxDepthToGraph
     */
    public int getMaxDepthToGraph() {
        return maxDepthToGraph;
    }

    /**
     * Returns SleepMode
     *
     * <p>From manual page 11:
     *
     * <p>Set to 0 by default. This will cause the instrument to power down to minimal current draw
     * between bursts. If set to 1, the instrument will not power down between bursts. This is
     * useful to avoid any warm-up artefacts at the start of each burst to maximise precision, but
     * unfeasible for long term deployments. This has no effect during Attended mode. A difficulty
     * is that the PLL light will not be extinguished between bursts, and there is a danger that the
     * radar will be switched off during a burst and the final data file might be corrupted.
     *
     * @return sleepMode
     */
    public boolean getSleepMode() {
        return sleepMode;
    }

    /**
     * Returns LogOn
     *
     * <p>From manual page 11:
     *
     * <p>Set to 1 to enable the writing of a log file. This is a text file called Log.txt that will
     * be created and maintained on both SD cards. It records the operation of the radar.
     *
     * @return logOn
     */
    public boolean getLogOn() {
        return logOn;
    }

    /**
     * Returns Settle_Cycles
     *
     * <p>From manual page 10:
     *
     * <p>number of chirps to be discarded at the start of each burst. As the radar components warm
     * up, there is a small change in phase delay through various components. If burst averaging is
     * enabled, it might be useful to remove the first few chirps from the average by setting
     * Settle_Cycles greater than zero. If averaging is not switched on, there is little advantage
     * to discarding the first chirps in a burst – it can be done during post-processing.
     *
     * @return settleCycles
     */
    public int getSettleCycles() {
        return settleCycles;
    }

    /**
     * Returns the value of Reg00 as a String
     *
     * <p>From manual page 11:
     *
     * <p>These define the nature of the chirp transmitted by the radar. A Matlab tool is available
     * that will allow the chirp properties to be changed. The tool appends the relevant lines to
     * the end of a config.ini file. Those lines are then used on power-up to overwrite the default
     * chirp configuration
     *
     * @return reg00
     */
    public String getReg00() {
        return reg00;
    }

    /**
     * Returns the value of Reg01 as a String
     *
     * <p>From manual page 11:
     *
     * <p>These define the nature of the chirp transmitted by the radar. A Matlab tool is available
     * that will allow the chirp properties to be changed. The tool appends the relevant lines to
     * the end of a config.ini file. Those lines are then used on power-up to overwrite the default
     * chirp configuration
     *
     * @return reg01
     */
    public String getReg01() {
        return reg01;
    }

    /**
     * Returns the value of Reg02 as a String
     *
     * <p>From manual page 11:
     *
     * <p>These define the nature of the chirp transmitted by the radar. A Matlab tool is available
     * that will allow the chirp properties to be changed. The tool appends the relevant lines to
     * the end of a config.ini file. Those lines are then used on power-up to overwrite the default
     * chirp configuration
     *
     * @return reg02
     */
    public String getReg02() {
        return reg02;
    }

    /**
     * Returns the value of Reg0B as a String
     *
     * <p>From manual page 11:
     *
     * <p>These define the nature of the chirp transmitted by the radar. A Matlab tool is available
     * that will allow the chirp properties to be changed. The tool appends the relevant lines to
     * the end of a config.ini file. Those lines are then used on power-up to overwrite the default
     * chirp configuration
     *
     * @return reg0B
     */
    public String getReg0B() {
        return reg0B;
    }

    /**
     * Returns the value of Reg0C as a String
     *
     * <p>From manual page 11:
     *
     * <p>These define the nature of the chirp transmitted by the radar. A Matlab tool is available
     * that will allow the chirp properties to be changed. The tool appends the relevant lines to
     * the end of a config.ini file. Those lines are then used on power-up to overwrite the default
     * chirp configuration
     *
     * @return reg0C
     */
    public String getReg0C() {
        return reg0C;
    }

    /**
     * Returns the value of Reg0D as a String
     *
     * <p>From manual page 11:
     *
     * <p>These define the nature of the chirp transmitted by the radar. A Matlab tool is available
     * that will allow the chirp properties to be changed. The tool appends the relevant lines to
     * the end of a config.ini file. Those lines are then used on power-up to overwrite the default
     * chirp configuration
     *
     * @return reg0D
     */
    public String getReg0D() {
        return reg0D;
    }

    /**
     * Returns the value of Reg0E as a String
     *
     * <p>From manual page 11:
     *
     * <p>These define the nature of the chirp transmitted by the radar. A Matlab tool is available
     * that will allow the chirp properties to be changed. The tool appends the relevant lines to
     * the end of a config.ini file. Those lines are then used on power-up to overwrite the default
     * chirp configuration
     *
     * @return reg0E
     */
    public String getReg0E() {
        return reg0E;
    }

    /**
     * Returns Latitude as an double primitive
     *
     * @return latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Returns Longitude as an double primitive
     *
     * @return longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Returns GPS_Time as double (could use {@code java.util.Date} but format is not mentioned in
     * manual)
     *
     * @return gpsTime
     */
    public double getGPSTime() {
        return gpsTime;
    }

    /**
     * Returns VM2_Time as double (could use {@code java.util.Date} but format is not mentioned in
     * manual)
     *
     * @return vm2Time
     */
    public double getVM2Time() {
        return vm2Time;
    }

    /**
     * Returns Temp1
     *
     * @return temp1
     */
    public double getTemp1() {
        return temp1;
    }

    /**
     * Returns Temp2
     *
     * @return temp2
     */
    public double getTemp2() {
        return temp2;
    }

    /**
     * Returns BatteryVoltage
     *
     * @return batteryVoltage
     */
    public double getBatteryVoltage() {
        return batteryVoltage;
    }

    /**
     * Returns MAX_SAF_FILE_LENGTH
     *
     * <p>Not mentioned in manual - so type is inferred as int
     *
     * @return maxSafFileLength
     */
    public int getMaxSafFileLength() {
        return maxSafFileLength;
    }

    /**
     * Returns InterChirpDelay
     *
     * <p>Not mentioned in manual - so type is inferred as int
     *
     * @return interChirpDelay
     */
    public int getInterChirpDelay() {
        return interChirpDelay;
    }

    /**
     * Returns SamplingFreqMode
     *
     * <p>Not mentioned in manual - so type is inferred as int
     *
     * @return samplingFreqMode
     */
    public int getSamplingFreqMode() {
        return samplingFreqMode;
    }

    /**
     * Returns Ramp
     *
     * <p>Not mentioned in manual - so type is inferred as int
     *
     * @return ramp
     */
    public int getRamp() {
        return ramp;
    }

    /**
     * Returns NoDwell
     *
     * <p>Not mentioned in manual - so type is inferred as int
     *
     * @return noDwell
     */
    public int getNoDwell() {
        return noDwell;
    }

    /**
     * Returns StartFreq
     *
     * <p>Not mentioned in manual - so type is inferred as int
     *
     * @return startFreq
     */
    public int getStartFreq() {
        return startFreq;
    }

    /**
     * Returns StopFreq
     *
     * <p>Not mentioned in manual - so type is inferred as int
     *
     * @return stopFreq
     */
    public int getStopFreq() {
        return stopFreq;
    }

    /**
     * Returns FreqStepUp
     *
     * <p>Not mentioned in manual - so type is inferred as int
     *
     * @return freqStepDn
     */
    public int getFreqStepUp() {
        return freqStepUp;
    }

    /**
     * Returns FreqStepDn
     *
     * <p>Not mentioned in manual - so type is inferred as int
     *
     * @return freqStepDn
     */
    public int getFreqStepDn() {
        return freqStepDn;
    }

    /**
     * Returns BurstNo
     *
     * <p>Not mentioned in manual - so type is inferred as int
     *
     * @return burstNo
     */
    public int getBurstNo() {
        return burstNo;
    }

    /**
     * Returns UpTell
     *
     * <p>Not mentioned in manual - so type is inferred as int
     *
     * @return upTell
     */
    public int getUpTell() {
        return upTell;
    }

    /**
     * Returns BattSleep
     *
     * <p>Not mentioned in manual - so type is inferred as boolean
     *
     * @return battSleep
     */
    public boolean getBattSleep() {
        return battSleep;
    }

    /**
     * Returns IsEthOn
     *
     * <p>Not mentioned in manual - so type is inferred as boolean
     *
     * @return isEthOn
     */
    public boolean getIsEthOn() {
        return isEthOn;
    }

    /**
     * Returns IsWebServerOn
     *
     * <p>Not mentioned in manual - so type is inferred as boolean
     *
     * @return isWebServerOn
     */
    public boolean getIsWebServerOn() {
        return isWebServerOn;
    }

    /**
     * Returns IsFTPServerOn
     *
     * <p>Not mentioned in manual - so type is inferred as boolean
     *
     * @return isFTPServerOn
     */
    public boolean getIsFTPServerOn() {
        return isFTPServerOn;
    }

    /**
     * Returns TStepUp
     *
     * <p>Not mentioned in manual - so type is inferred as double
     *
     * @return tStepUp
     */
    public double getTStepUp() {
        return tStepUp;
    }

    /**
     * Returns TStepDn
     *
     * <p>Not mentioned in manual - so type is inferred as double
     *
     * @return tStepDn
     */
    public double getTStepDn() {
        return tStepDn;
    }

    /**
     * Returns BatteryCheck
     *
     * <p>Not mentioned in manual - so type is inferred as {@code List<Integer>}
     *
     * @return copy of batteryCheck
     */
    public List<Integer> getBatteryCheck() {
        // returns a shallow copy
        return new ArrayList<>(batteryCheck);
    }
}

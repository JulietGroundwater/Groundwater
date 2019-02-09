package uk.ac.cam.cl.juliet.computationengine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for representing the configuration of the radar, requires getters and setters for all
 * values int the {@code Config.ini} file.
 *
 * <p>Descriptions of values and functions are adapted from the comments in the {@code Config.ini}
 * file.
 */
public class Config {

    private boolean alwaysAttended = false,
            checkEthernet = true,
            samplingFrequencyMode,
            intervalMode = false,
            logOn = false,
            sleepMode = false,
            gpsOn = false,
            housekeeping,
            syncGPS,
            iridium = false;
    private int maxDepthToGraph,
            nADCSamples,
            nData,
            watchDogTaskSecs = 3600,
            interChirpDelay,
            settleCycles,
            nSubBursts = 10,
            average,
            repSecs,
            maxDataFileLength = 10000000,
            nAttenuators;
    private List<Integer> triples = new ArrayList<>(),
            attenuator1 = new ArrayList<>(),
            afGain = new ArrayList<>();
    private String reg00, reg01, reg02, reg0B, reg0C, reg0D, reg0E;

    /**
     * Creates a {@code Config} object and initialises the values from the specified function
     *
     * <p>Getters and Setters are supplied for all of the fields in the example {@code Config.ini}
     *
     * @param filename name of the {@code Config.ini} file to load
     */
    public Config(String filename) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(filename).getFile());

        BufferedReader reader = new BufferedReader(new FileReader(file));

        String line;
        while ((line = reader.readLine()) != null) {
            if (line.length() == 0) continue;
            if (line.charAt(0) == ';') continue;

            if (!line.contains("=")) continue;

            // split on ';' as well as '=' to ensure only taking the relevant information
            String lhs = line.split(";")[0].split("=")[0]; // left hand side
            String rhs = line.split(";")[0].split("=")[1]; // right hand side

            if (lhs.contains("AlwaysAttended")) alwaysAttended = (Integer.parseInt(rhs) != 0);
            else if (lhs.contains("CheckEthernet")) checkEthernet = (Integer.parseInt(rhs) != 0);
            else if (lhs.contains("maxDepthToGraph")) maxDepthToGraph = Integer.parseInt(rhs);
            else if (lhs.contains("N_ADC_SAMPLES")) nADCSamples = Integer.parseInt(rhs);
            else if (lhs.contains("SamplingFreqMode"))
                samplingFrequencyMode = (Integer.parseInt(rhs) != 0);
            else if (lhs.contains("NData")) nData = Integer.parseInt(rhs);
            else if (lhs.contains("Triples"))
                for (String s : rhs.split(",")) triples.add(Integer.parseInt(s));
            else if (lhs.contains("WATCHDOG_TASK_SECS")) watchDogTaskSecs = Integer.parseInt(rhs);
            else if (lhs.contains("InterChirpDelay")) interChirpDelay = Integer.parseInt(rhs);
            else if (lhs.contains("Settle_Cycles")) settleCycles = Integer.parseInt(rhs);
            else if (lhs.contains("NSubBursts")) nSubBursts = Integer.parseInt(rhs);
            else if (lhs.contains("Average")) average = Integer.parseInt(rhs);
            else if (lhs.contains("RepSecs")) repSecs = Integer.parseInt(rhs);
            else if (lhs.contains("IntervalMode")) intervalMode = (Integer.parseInt(rhs) != 0);
            else if (lhs.contains("MAX_DATA_FILE_LENGTH")) maxDepthToGraph = Integer.parseInt(rhs);
            else if (lhs.contains("LOGON")) logOn = (Integer.parseInt(rhs) != 0);
            else if (lhs.contains("nAttenuators")) nAttenuators = Integer.parseInt(rhs);
            else if (lhs.contains("Attenuator1"))
                for (String s : rhs.split(",")) attenuator1.add(Integer.parseInt(s));
            else if (lhs.contains("AFGain"))
                for (String s : rhs.split(",")) afGain.add(Integer.parseInt(s));
            else if (lhs.contains("SleepMode")) sleepMode = (Integer.parseInt(rhs) != 0);
            else if (lhs.contains("SyncGPS")) syncGPS = (Integer.parseInt(rhs) != 0);
            else if (lhs.contains("Iridium")) iridium = (Integer.parseInt(rhs) != 0);
            else if (lhs.contains("Reg00")) reg00 = rhs;
            else if (lhs.contains("Reg01")) reg01 = rhs;
            else if (lhs.contains("Reg02")) reg02 = rhs;
            else if (lhs.contains("Reg0B")) reg0B = rhs;
            else if (lhs.contains("Reg0C")) reg0C = rhs;
            else if (lhs.contains("Reg0D")) reg0D = rhs;
        }
    }

    /**
     * Returns AlwaysAttended
     *
     * <p>Always start the Web Server (ie always go into Attended Mode), regardless of an active
     * Ethernet connection. Default 0 (false)
     *
     * <p>Relevant for Attended Mode
     *
     * @return alwaysAttended
     */
    public boolean getAlwaysAttended() {
        return alwaysAttended;
    }

    /**
     * sets AlwaysAttended
     *
     * <p>Always start the Web Server (ie always go into Attended Mode), regardless of an active *
     * Ethernet connection. Default false.
     *
     * <p>Relevant for Attended Mode
     *
     * @param attended the value to be set
     */
    public void setAlwaysAttended(boolean attended) {
        alwaysAttended = attended;
    }

    /**
     * Returns CheckEthernet
     *
     * <p>Check for an Ethernet connection on power-up. Default=true.
     *
     * <p>Relevant for Attended Mode
     *
     * @return checkEthernet
     */
    public boolean getCheckEthernet() {
        return checkEthernet;
    }

    /**
     * sets CheckEthernet
     *
     * <p>Check for an Ethernet connection on power-up. Default=true.
     *
     * <p>Relevant for Attended Mode
     *
     * @param ethernet the value to be set
     */
    public void setCheckEthernet(boolean ethernet) {
        checkEthernet = ethernet;
    }

    /**
     * Returns MaxDepthToGraph
     *
     * <p>When used in attended mode, and doing a Trial Sub-Burst, the maximum depth that is
     * displayed on the FFT (A-scope) display. This can be overwritten from the browser.
     *
     * <p>Relevant for Attended Mode
     *
     * @return maxDepthToGraph
     */
    public int getMaxDepthToGraph() {
        return maxDepthToGraph;
    }

    /**
     * Sets MaxDepthToGraph
     *
     * <p>Ensures that the value is >= 0
     *
     * <p>When used in attended mode, and doing a Trial Sub-Burst, the maximum depth that is *
     * displayed on the FFT (A-scope) display. This can be overwritten from the browser.
     *
     * <p>Relevant for Attended Mode
     *
     * @param depth depth to be set
     */
    public void setMaxDepthToGraph(int depth) {
        if (depth >= 0) maxDepthToGraph = depth;

        // throw an exception ?
    }

    /**
     * Returns N_ADC_SAMPLES
     *
     * <p>Number of samples per burst (>=10)
     *
     * <p>Relevant for both Attended and Unattended modes
     *
     * @return N_ADC_SAMPLES
     */
    public int getNADCSamples() {
        return nADCSamples;
    }

    /**
     * Sets N_ADC_SAMPLES
     *
     * <p>Number of samples per burst (>=10)
     *
     * <p>Relevant for both Attended and Unattended modes
     *
     * @param n number of samples to be set
     */
    public void setNADCSamples(int n) {
        if (n >= 10) nADCSamples = n;
    }

    /**
     * Returns SamplingFreqMode
     *
     * <p>0 (false) ->40kHz
     *
     * <p>1 (true) ->80kHz
     *
     * <p>Relevant for both Attended and Unattended modes
     *
     * @return samplingFreqMode
     */
    public boolean getSamplingFrequencyMode() {
        return samplingFrequencyMode;
    }

    /**
     * Sets SamplingFreqMode
     *
     * <p>0 (false) ->40kHz
     *
     * <p>1 (true) ->80kHz
     *
     * <p>Relevant for both Attended and Unattended modes
     *
     * @param mode mode to be set
     */
    public void setSamplingFrequencyMode(boolean mode) {
        samplingFrequencyMode = mode;
    }

    /**
     * Returns NData
     *
     * <p>Every NData burts, one will be averaged, analysed and the results reported via Iridium.
     * Starts with the first burst of the deployment.
     *
     * <p>Relevant for both Attended and Unattended modes
     *
     * @return nData
     */
    public int getNData() {
        return nData;
    }

    /**
     * Sets NData
     *
     * <p>Every NData burts, one will be averaged, analysed and the results reported via Iridium.
     * Starts with the first burst of the deployment.
     *
     * <p>Relevant for both Attended and Unattended modes
     *
     * @param n value to be set
     */
    public void setNData(int n) {
        // check for positive values
        nData = n;
    }

    /**
     * Returns Triples as a {@code java.util.List<Integer>} object.
     *
     * <p>Triples define depth intervals to search for maxima to report on. Up to a max of four
     * triples allowed. Each Triple is used in a Matlab sense to define intervals (A,B,C interpreted
     * as A:B:C). Maximum of 64 intervals allowed.
     *
     * <p>Relevant for both Attended and Unattended modes
     *
     * @return triples
     */
    public List<Integer> getTriples() {
        // returns a clone of the list
        return new ArrayList<>(triples);
    }

    /**
     * Sets Triples as a {@code java.util.List<Integer>} object.
     *
     * <p>Triples define depth intervals to search for maxima to report on. Up to a max of four
     * triples allowed. Each Triple is used in a Matlab sense to define intervals (A,B,C interpreted
     * as A:B:C). Maximum of 64 intervals allowed.
     *
     * <p>Relevant for both Attended and Unattended modes
     *
     * @param values values to be used
     */
    public void setTriples(List<Integer> values) throws InvalidConfigException {
        // ensure that only lists which a size of a multiple of 3, and max 4 triples so 12 values
        if (values.size() % 3 == 0 && values.size() <= 12)
            triples = new ArrayList<>(values); // performs a clone of the data
        else
            throw new InvalidConfigException(
                    "Expecting a list with a size which is a multiple of 3, and a size <= 12");
    }

    /**
     * Returns WATCH_DOG_TASK_SECS
     *
     * <p>WatchDog task behaviour. Time in seconds of operation after which; radar will be reset.
     * Assumption is that a fault has occurred if radar is active for longer than this time.
     * Watchdog does not operate in ; attended mode. If Watchdog time is set to 0, then the default
     * of 3600 seconds is used. If set to -1, then Watchdog task is disabled.
     *
     * <p>Relevant for both Attended and Unattended modes
     *
     * @return watchDogTaskSecs
     */
    public int getWatchdogTaskSecs() {
        return watchDogTaskSecs;
    }

    /**
     * Sets WATCH_DOG_TASK_SECS
     *
     * <p>WatchDog task behaviour. Time in seconds of operation after which; radar will be reset.
     * Assumption is that a fault has occurred if radar is active for longer than this time.
     * Watchdog does not operate in ; attended mode. If Watchdog time is set to 0, then the default
     * of 3600 seconds is used. If set to -1, then Watchdog task is disabled.
     *
     * <p>Relevant for both Attended and Unattended modes
     *
     * @param secs the value to be set
     */
    public void setWatchdogTaskSecs(int secs) throws InvalidConfigException {
        if (secs == 0) watchDogTaskSecs = 3600;
        else if (secs == -1) watchDogTaskSecs = -1; // "disables" Watchdog task
        else if (secs > 0) watchDogTaskSecs = secs;
        else
            throw new InvalidConfigException(
                    "Unexpected value for for WATCHDOG_TASK_SECS, value should be >= -1");
    }

    /**
     * Returns InterChirpDelay
     *
     * <p>The amount of time for the system to settle down
     *
     * <p>Relevant for both Attended and Unattended modes
     *
     * @return interChirpDelay
     */
    public int getInterChirpDelay() {
        return interChirpDelay;
    }

    /**
     * Sets InterChirpDelay
     *
     * <p>The amount of time for the system to settle down
     *
     * <p>Relevant for both Attended and Unattended modes
     *
     * @param delay delay value to be set
     */
    public void setInterChirpDelay(int delay) throws InvalidConfigException {
        if (delay > 0) interChirpDelay = delay;
        else throw new InvalidConfigException("New InterChirpDelay value must be > 0");
    }

    /**
     * Returns Settle_Cycles
     *
     * <p>'Don't record first few chirps'
     *
     * <p>Relevant for both Attended and Unattended modes
     *
     * @return settleCycles
     */
    public int getSettleCycles() {
        return settleCycles;
    }

    /**
     * Sets Settle_Cycles
     *
     * <p>'Don't record first few chirps'
     *
     * <p>Relevant for both Attended and Unattended modes
     *
     * @param cycles amount of cycles
     */
    public void setSettleCycles(int cycles) throws InvalidConfigException {
        if (cycles > 0) settleCycles = cycles;
        else throw new InvalidConfigException("New Settle_Cycles must be > 0");
    }

    /**
     * Returns NSubBursts
     *
     * <p>Number of sub-bursts in a burst (>= 0)
     *
     * <p>Default = 10
     *
     * <p>Relevant for both Attended and Unattended modes
     *
     * @return nSubBursts
     */
    public int getNSubBursts() {
        return nSubBursts;
    }

    /**
     * Sets NSubBursts
     *
     * <p>Number of sub-bursts in a burst (>= 0)
     *
     * <p>Default = 10
     *
     * <p>Relevant for both Attended and Unattended modes
     *
     * @param bursts amount of bursts to be set
     */
    public void setNSubBursts(int bursts) throws InvalidConfigException {
        if (bursts >= 0) nSubBursts = bursts;
        else throw new InvalidConfigException("New NSubBursts value must be >= 0");
    }

    /**
     * Returns Average
     *
     * <p>Acceptable values:
     *
     * <p>0 -> Store all chirps individually
     *
     * <p>1 -> Average all chirps
     *
     * <p>2 -> Stack all chirps
     *
     * <p>Relevant for both Attended and Unattended modes
     *
     * @return average
     */
    public int getAverage() {
        return average;
    }

    /**
     * Sets Average
     *
     * <p>Acceptable values:
     *
     * <p>0 -> Store all chirps individually
     *
     * <p>1 -> Average all chirps
     *
     * <p>2 -> Stack all chirps
     *
     * <p>Relevant for both Attended and Unattended modes
     *
     * @param ave - average mode to be set
     */
    public void setAverage(int ave) throws InvalidConfigException {
        // ensure that average is between 0 and 2 inclusive
        if (ave >= 0 && ave <= 2) average = ave;
        else throw new InvalidConfigException("Average value must be 0, 1 or 2");
    }

    /**
     * Returns RepSecs
     *
     * <p>Burst repetition period (integer seconds) (>0). Interpretation depends on IntervalMode. If
     * IntervalMode = 0 (default), RepSecs is time from start of one burst to the start of the next.
     * If IntervalMode = 1, RepSecs is interval between end of one burst and start of next.
     *
     * <p>Relevant for both Attended and Unattended modes
     *
     * @return repSecs
     */
    public int getRepSecs() {
        return repSecs;
    }

    /**
     * Sets RepSecs
     *
     * <p>Burst repetition period (integer seconds) (>0). Interpretation depends on IntervalMode. If
     * IntervalMode = 0 (default), RepSecs is time from start of one burst to the start of the next.
     * If IntervalMode = 1, RepSecs is interval between end of one burst and start of next.
     *
     * <p>Relevant for both Attended and Unattended modes
     *
     * @param secs number of seconds
     */
    public void setRepSecs(int secs) throws InvalidConfigException {
        if (secs > 0) repSecs = secs;
        else throw new InvalidConfigException("New value must be > 0");
    }

    /**
     * Returns IntervalMode
     *
     * <p>Burst repetition period (integer seconds) (>0). Interpretation depends on IntervalMode. If
     * IntervalMode = 0 (default), RepSecs is time from start of one burst to the start of the next.
     * If IntervalMode = 1, RepSecs is interval between end of one burst and start of next.
     *
     * <p>Relevant for both Attended and Unattended modes
     *
     * @return intervalMode
     */
    public boolean getIntervalMode() {
        return intervalMode;
    }

    /**
     * Sets IntervalMode
     *
     * <p>Burst repetition period (integer seconds) (>0). Interpretation depends on IntervalMode. If
     * IntervalMode = 0 (default), RepSecs is time from start of one burst to the start of the next.
     * If IntervalMode = 1, RepSecs is interval between end of one burst and start of next.
     *
     * <p>Relevant for both Attended and Unattended modes
     *
     * @param mode mode to use
     */
    public void setIntervalMode(boolean mode) {
        intervalMode = mode;
    }

    /**
     * Returns MAX_DATA_FILE_LENGTH
     *
     * <p>Maximum length of data file before another one started (>=1,000,000)
     *
     * <p>Default=10,000,000
     *
     * <p>Relevant for both Attended and Unattended modes
     *
     * @return maxDataFileLength
     */
    public int getMaxDataFileLength() {
        return maxDataFileLength;
    }

    /**
     * Sets MAX_DATA_FILE_LENGTH
     *
     * <p>Maximum length of data file before another one started (>=1,000,000)
     *
     * <p>Default=10,000,000
     *
     * <p>Relevant for both Attended and Unattended modes
     *
     * @param fileLength max data file length to use
     */
    public void setMaxDataFileLength(int fileLength) throws InvalidConfigException {

        // in the supplied Config.ini the value is 100,000 which is less than the specified minimum
        // of 1,000,000

        if (fileLength >= 1000000) maxDataFileLength = fileLength;
        else throw new InvalidConfigException("value must be >= 1,000,000");
    }

    /**
     * Returns LOGON
     *
     * <p>Whether a logging file is to be maintained (default = no (0)).
     *
     * <p>Relevant for both Attended and Unattended modes
     *
     * @return logOn
     */
    public boolean getLogOn() {
        return logOn;
    }

    /**
     * Sets LOGON
     *
     * <p>Whether a logging file is to be maintained (default = no (0)).
     *
     * <p>Relevant for both Attended and Unattended modes
     *
     * @param log value for logOn
     */
    public void setLogOn(boolean log) {
        logOn = log;
    }

    /**
     * Returns nAttenuators
     *
     * <p>Number of combinations of attenuator settings to be used
     *
     * <p>Relevant for both Attended and Unattended modes
     *
     * @return nAttenuators
     */
    public int getNAttenuators() {
        return nAttenuators;
    }

    /**
     * Sets nAttenuators
     *
     * <p>Number of combinations of attenuator settings to be used
     *
     * <p>Relevant for both Attended and Unattended modes
     *
     * @param attenuators value to be set
     */
    public void setNAttenuators(int attenuators) {
        // check for positive value?
        nAttenuators = attenuators;
    }

    /**
     * Returns Attenuator1 as a {@code java.util.List<Integer>} object.
     *
     * <p>Attenuator setting sequences (dB) (>0, <=31.5)
     *
     * <p>Defaults=30dB
     *
     * <p>Relevant for both Attended and Unattended modes
     *
     * @return attenuator1
     */
    public List<Integer> getAttenuator1() {
        // return a copy of the list
        return new ArrayList<>(attenuator1);
    }

    /**
     * Sets Attenuator1 as a {@code java.util.List<Integer>} object.
     *
     * <p>Attenuator setting sequences (dB) (>0, <=31.5)
     *
     * <p>Defaults=30dB
     *
     * <p>Relevant for both Attended and Unattended modes
     *
     * @param values values for attenuator1
     */
    public void setAttenuator1(List<Integer> values) throws InvalidConfigException {
        // should this be a double, the max value is 31.5?
        boolean flag = true;
        for (int i : values) if (!(i > 0 && i <= 31.5)) flag = false;
        if (flag) attenuator1 = new ArrayList<>(values);
        else throw new InvalidConfigException("values for Attenuator1 must be >0 and <= 31.5");
    }

    /**
     * Returns AFGain as a {@code java.util.List<Integer>} object.
     *
     * <p>Attenuator setting sequences (dB) (>0, <=31.5)
     *
     * <p>Defaults=30dB
     *
     * <p>Relevant for both Attended and Unattended modes
     *
     * @return afGain
     */
    public List<Integer> getAFGain() {
        return new ArrayList<>(afGain);
    }

    /**
     * Sets AFGain as a {@code java.util.List<Integer>} object.
     *
     * <p>Attenuator setting sequences (dB) (>0, <=31.5)
     *
     * <p>Defaults=30dB
     *
     * <p>Relevant for both Attended and Unattended modes
     *
     * @param values values for afGain
     */
    public void setAFGain(List<Integer> values) throws InvalidConfigException {
        // should this be a double, the max value is 31.5?
        boolean flag = true;
        for (int i : values) if (!(i > 0 && i <= 31.5)) flag = false;
        if (flag) afGain = new ArrayList<>(values);
        else throw new InvalidConfigException("values for Attenuator1 must be >0 and <= 31.5");
    }

    /**
     * Returns SleepMode
     *
     * <p>In unattended mode, does the radar sleep between bursts (default, 0), or does it wait (1).
     * In the sleep case the system is powered down between bursts and draws a low current (<200uA).
     * Otherwise system remains powered and draws ~1 Amp at 6V, 0.45 Amp at 12 V.
     *
     * <p>Relevant for both Attended and Unattended modes
     *
     * @return sleepMode
     */
    public boolean getSleepMode() {
        return sleepMode;
    }

    /**
     * sets SleepMode
     *
     * <p>In unattended mode, does the radar sleep between bursts (default, 0), or does it wait (1).
     * In the sleep case the system is powered down between bursts and draws a low current (<200uA).
     * Otherwise system remains powered and draws ~1 Amp at 6V, 0.45 Amp at 12 V.
     *
     * <p>Relevant for both Attended and Unattended modes
     *
     * @param mode mode to be set
     */
    public void setSleepMode(boolean mode) {
        sleepMode = mode;
    }

    /**
     * Returns GPSon
     *
     * <p>Time out for GPS receiver for each burst (0-255 seconds)? Default is 0 - do not attempt to
     * obtain fix before each burst.
     *
     * <p>Relevant for both Attended and Unattended modes
     *
     * @return gpsOn
     */
    public boolean getGPSOn() {
        return gpsOn;
    }

    /**
     * Sets GPSon
     *
     * <p>Time out for GPS receiver for each burst (0-255 seconds)? Default is 0 - do not attempt to
     * obtain fix before each burst.
     *
     * <p>Relevant for both Attended and Unattended modes
     *
     * @param gps mode to be set
     */
    public void setGPSOn(boolean gps) {
        gpsOn = gps;
    }

    /**
     * Returns Housekeeping
     *
     * <p>Undertake daily housekeeping (GPS clock check, Iridium exchange and memory card check?
     * (true = yes, false = no)
     *
     * <p>Relevant for both Attended and Unattended modes
     *
     * @return housekeeping
     */
    public boolean getHousekeeping() {
        return housekeeping;
    }

    /**
     * Sets Housekeeping
     *
     * <p>Undertake daily housekeeping (GPS clock check, Iridium exchange and memory card check?
     * (true = yes, false = no)
     *
     * <p>Relevant for both Attended and Unattended modes
     *
     * @param keeping housekeeping mode to use
     */
    public void setHousekeeping(boolean keeping) {
        housekeeping = keeping;
    }

    /**
     * Returns SyncGPS
     *
     * <p>If GPS fix obtained during daily housekeeping, synchronise radar clock ; to GPS time (only
     * if Housekeeping=1)? (true = yes, false = no)
     *
     * <p>Relevant for both Attended and Unattended modes
     *
     * @return syncGPS
     */
    public boolean getSyncGPS() {
        return syncGPS;
    }

    /**
     * Sets SyncGPS
     *
     * <p>If GPS fix obtained during daily housekeeping, synchronise radar clock ; to GPS time (only
     * if Housekeeping=1)? (true = yes, false = no)
     *
     * <p>Relevant for both Attended and Unattended modes
     *
     * @param gps syncGPS mode to use
     */
    public void setSyncGPS(boolean gps) {
        syncGPS = gps;
    }

    /**
     * Returns Iridium
     *
     * <p>If Housekeeping=1, is Iridium messaging enabled? (true = yes, false = no)
     *
     * <p>Default = false
     *
     * <p>Relevant for both Attended and Unattended modes
     *
     * @return iridium
     */
    public boolean getIridium() {
        return iridium;
    }

    /**
     * Sets Iridium
     *
     * <p>If Housekeeping=1, is Iridium messaging enabled? (true = yes, false = no)
     *
     * <p>Default = false
     *
     * <p>Relevant for both Attended and Unattended modes
     *
     * @param iridium Iridium mode to use
     */
    public void setIridium(boolean iridium) {
        iridium = iridium;
    }

    /**
     * Returns the value of the specified register
     *
     * <p>Very much for the advanced user. The DDS programming strings. These strings are set by
     * defaults in the instrument and, like many parameters in the config file, do not need to be
     * set here. They are included for completeness.
     *
     * @return reg
     */
    public String getReg00() {
        return reg00;
    }

    /**
     * Returns the value of the specified register
     *
     * <p>Very much for the advanced user. The DDS programming strings. These strings are set by
     * defaults in the instrument and, like many parameters in the config file, do not need to be
     * set here. They are included for completeness.
     *
     * @param reg - register value to be set
     */
    public void setReg00(String reg) {
        // should check for correct format
        reg00 = reg;
    }

    /**
     * Returns the value of the specified register
     *
     * <p>Very much for the advanced user. The DDS programming strings. These strings are set by
     * defaults in the instrument and, like many parameters in the config file, do not need to be
     * set here. They are included for completeness.
     *
     * @return reg
     */
    public String getReg01() {
        return reg01;
    }

    /**
     * Returns the value of the specified register
     *
     * <p>Very much for the advanced user. The DDS programming strings. These strings are set by
     * defaults in the instrument and, like many parameters in the config file, do not need to be
     * set here. They are included for completeness.
     *
     * @param reg - register value to be set
     */
    public void setReg01(String reg) {
        // should check for correct format
        reg01 = reg;
    }

    /**
     * Returns the value of the specified register
     *
     * <p>Very much for the advanced user. The DDS programming strings. These strings are set by
     * defaults in the instrument and, like many parameters in the config file, do not need to be
     * set here. They are included for completeness.
     *
     * @return reg
     */
    public String getReg02() {
        return reg02;
    }

    /**
     * Returns the value of the specified register
     *
     * <p>Very much for the advanced user. The DDS programming strings. These strings are set by
     * defaults in the instrument and, like many parameters in the config file, do not need to be
     * set here. They are included for completeness.
     *
     * @param reg - register value to be set
     */
    public void setReg02(String reg) {
        // should check for correct format
        reg02 = reg;
    }

    /**
     * Returns the value of the specified register
     *
     * <p>Very much for the advanced user. The DDS programming strings. These strings are set by
     * defaults in the instrument and, like many parameters in the config file, do not need to be
     * set here. They are included for completeness.
     *
     * @return reg
     */
    public String getReg0B() {
        return reg0B;
    }

    /**
     * Returns the value of the specified register
     *
     * <p>Very much for the advanced user. The DDS programming strings. These strings are set by
     * defaults in the instrument and, like many parameters in the config file, do not need to be
     * set here. They are included for completeness.
     *
     * @param reg - register value to be set
     */
    public void setReg0B(String reg) {
        // should check for correct format
        reg0B = reg;
    }

    /**
     * Returns the value of the specified register
     *
     * <p>Very much for the advanced user. The DDS programming strings. These strings are set by
     * defaults in the instrument and, like many parameters in the config file, do not need to be
     * set here. They are included for completeness.
     *
     * @return reg
     */
    public String getReg0C() {
        return reg0C;
    }

    /**
     * Returns the value of the specified register
     *
     * <p>Very much for the advanced user. The DDS programming strings. These strings are set by
     * defaults in the instrument and, like many parameters in the config file, do not need to be
     * set here. They are included for completeness.
     *
     * @param reg - register value to be set
     */
    public void setReg0C(String reg) {
        // should check for correct format
        reg0C = reg;
    }

    /**
     * Returns the value of the specified register
     *
     * <p>Very much for the advanced user. The DDS programming strings. These strings are set by
     * defaults in the instrument and, like many parameters in the config file, do not need to be
     * set here. They are included for completeness.
     *
     * @return reg
     */
    public String getReg0D() {
        return reg0D;
    }

    /**
     * Returns the value of the specified register
     *
     * <p>Very much for the advanced user. The DDS programming strings. These strings are set by
     * defaults in the instrument and, like many parameters in the config file, do not need to be
     * set here. They are included for completeness.
     *
     * @param reg - register value to be set
     */
    public void setReg0D(String reg) {
        // should check for correct format
        reg0D = reg;
    }

    /**
     * Returns the value of the specified register
     *
     * <p>Very much for the advanced user. The DDS programming strings. These strings are set by
     * defaults in the instrument and, like many parameters in the config file, do not need to be
     * set here. They are included for completeness.
     *
     * @return reg
     */
    public String getReg0E() {
        return reg0E;
    }

    /**
     * Returns the value of the specified register
     *
     * <p>Very much for the advanced user. The DDS programming strings. These strings are set by
     * defaults in the instrument and, like many parameters in the config file, do not need to be
     * set here. They are included for completeness.
     *
     * @param reg - register value to be set
     */
    public void setReg0E(String reg) {
        // should check for correct format
        reg0E = reg;
    }
}

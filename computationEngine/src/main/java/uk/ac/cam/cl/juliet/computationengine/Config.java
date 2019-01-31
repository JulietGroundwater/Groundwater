package uk.ac.cam.cl.juliet.computationengine;

import java.util.List;

/**
 * Class for representing the configuration of the radar, requires getters and setters for all
 * values int the {@code Config.ini} file.
 *
 * <p>Descriptions of values and functions are adapted from the comments in the {@code Config.ini}
 * file.
 */
public class Config {

    public Config(String filename) {
        // TODO
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
        // TODO
        return false;
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
        // TODO
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
        // TODO
        return false;
    }

    /**
     * sets AlwaysAttended
     *
     * <p>Check for an Ethernet connection on power-up. Default=true.
     *
     * <p>Relevant for Attended Mode
     *
     * @param ethernet the value to be set
     */
    public void setCheckEthernet(boolean ethernet) {
        // TODO
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
        // TODO
        return 0;
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
        // TODO
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
        // TODO
        return 0;
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
        // TODO
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
        // TODO
        return false;
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
        // TODO
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
        // TODO
        return 0;
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
        // TODO
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
        // TODO
        return null;
    }

    /**
     * Sets Triples as a {@code java.util.List<Integer>} object.
     *
     * <p>Triples define depth intervals to search for maxima to report on. Up to a max of four *
     * triples allowed. Each Triple is used in a Matlab sense to define intervals (A,B,C interpreted
     * * as A:B:C). Maximum of 64 intervals allowed.
     *
     * <p>Relevant for both Attended and Unattended modes
     *
     * @param triples values to be used
     */
    public void setTriples(List<Integer> triples) {
        // TODO
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
        // TODO
        return 0;
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
    public void setWatchdogTaskSecs(int secs) {
        // TODO
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
        // TODO
        return 0;
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
    public void setInterChirpDelay(int delay) {
        // TODO
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
        // TODO
        return 0;
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
    public void setSettleCycles(int cycles) {
        // TODO
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
        // TODO
        return 0;
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
    public void setNSubBursts(int bursts) {
        // TODO
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
        // TODO
        return 0;
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
     * @param average - average mode to be set
     */
    public void setAverage(int average) {
        // TODO
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
        // TODO
        return 0;
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
    public void setRepSecs(int secs) {
        // TODO
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
        // TODO
        return false;
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
        // TODO
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
        // TODO
        return 0;
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
    public void setMaxDataFileLength(int fileLength) {
        // TODO
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
        // TODO
        return false;
    }

    /**
     * Sets LOGON
     *
     * <p>Whether a logging file is to be maintained (default = no (0)).
     *
     * <p>Relevant for both Attended and Unattended modes
     *
     * @param logOn value for logOn
     */
    public void setLogOn(boolean logOn) {
        // TODO
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
        // TODO
        return 0;
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
        // TODO
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
        // TODO
        return null;
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
    public void setAttenuator1(List<Integer> values) {
        // TODO
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
        // TODO
        return null;
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
    public void setAFGain(List<Integer> values) {
        // TODO
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
        // TODO
        return false;
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
        // TODO
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
        // TODO
        return false;
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
        // TODO
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
        // TODO
        return false;
    }

    /**
     * Sets Housekeeping
     *
     * <p>Undertake daily housekeeping (GPS clock check, Iridium exchange and memory card check?
     * (true = yes, false = no)
     *
     * <p>Relevant for both Attended and Unattended modes
     *
     * @param housekeeping housekeeping mode to use
     */
    public void setHousekeeping(boolean housekeeping) {
        // TODO
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
        // TODO
        return false;
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
        // TODO
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
        // TODO
        return false;
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
        // TODO
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
        // TODO
        return null;
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
        // TODO
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
        // TODO
        return null;
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
        // TODO
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
        // TODO
        return null;
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
        // TODO
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
        // TODO
        return null;
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
        // TODO
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
        // TODO
        return null;
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
        // TODO
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
        // TODO
        return null;
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
        // TODO
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
        // TODO
        return null;
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
        // TODO
    }
}

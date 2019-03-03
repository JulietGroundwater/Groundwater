package uk.ac.cam.cl.juliet.computationengine;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import org.apache.commons.math3.complex.Complex;

/**
 * Class to represent a Burst from the radar, this is a java representation of the vdat struct from
 * {@code fmcw_load()}. The only required methods are getters for each of the fields in the struct.
 */
public class Burst {

    private String filename;
    private int subBurstsInBurst, average, nAttenuators, nSamples, chirpsInBurst, burst;
    private Date dateTime;
    private double temperature1, temperature2, batteryVoltage, samplesPerChirp, f0, k, t;
    private List<Double> attenuator1, attenuator2;
    private List<String> processing = new ArrayList<>();
    private List<Complex> chirpAtt = new ArrayList<>();
    private List<Date> chirpTime = new ArrayList<>();
    private List<Integer> startInd, endInd, TxAnt, RxAnt, chirpNum = new ArrayList<>();
    private List<List<Double>> vif;

    // Constants
    private static final double fSysClk = 1e9;
    private static final int MaxHeaderLen = 1500;
    private static final double fs = 4e4;
    private static final double dt = 1.0 / fs;
    private static final double er = 3.18;
    private static final int fileFormat = 5;

    /**
     * A default constructor if the number of the burst is not specified.
     *
     * <p>Burst number will default to 1
     *
     * <p>Mean value will default to false
     *
     * @param file name of the {@code .DAT} file to read from
     * @throws InvalidBurstException if there are errors when reading the file
     */
    public Burst(File file) throws InvalidBurstException {
        this(file, 1, true);
    }

    /**
     * A constructor if the burst number is not specified, but the mean option is
     *
     * <p>Burst number will default to 1
     *
     * @param file name of the {@code .DAT} file to read from
     * @param mean whether to find the mean of the Burst's data or not
     * @throws InvalidBurstException if there are errors when reading the file
     */
    public Burst(File file, boolean mean) throws InvalidBurstException {
        this(file, 1, mean);
    }

    /**
     * A constructor if the burst number is not specified, but the mean option is
     *
     * <p>Burst number will default to 1
     *
     * @param file name of the {@code .DAT} file to read from
     * @param burstNum number of the burst to load (starts from 1)
     * @throws InvalidBurstException if there are errors when reading the file
     */
    public Burst(File file, int burstNum) throws InvalidBurstException {
        this(file, burstNum, true);
    }

    /**
     * Constructor for a burst that takes both the filename and the number of the burst to load
     *
     * @param file name of the {code .DAT} file to read from
     * @param burstNum number of the burst to load (starts from 1)
     * @param mean whether to find the mean of the Burst's data or not
     * @throws InvalidBurstException if there are errors when reading the file
     */
    public Burst(File file, int burstNum, boolean mean) throws InvalidBurstException {
        burst = burstNum;

        if (file == null) {
            throw new InvalidBurstException("file cannot be null");
        }

        filename = file.getName();
        if (!filename.contains(".DAT")) {
            throw new InvalidBurstException("file must be a .DAT file");
        }

        try (FileInputStream f = new FileInputStream(file)) {
            FileChannel fChannel = f.getChannel();
            String header = new String(readFile(f, MaxHeaderLen));

            // Read in all of the simple parameters.
            nSamples = parseInt(header, "N_ADC_SAMPLES=");
            subBurstsInBurst = parseInt(header, "NSubBursts=");
            nAttenuators = parseInt(header, "nAttenuators=");
            attenuator1 = parseDoubleArray(header, "Attenuator1=");
            attenuator2 = parseDoubleArray(header, "AFGain=");
            batteryVoltage = parseDouble(header, "BatteryVoltage=");
            f0 = parseReg(header, "Reg0B=", 9, 17) * fSysClk / Math.pow(2, 32);
            TxAnt = parseIntArray(header, "TxAnt=", 8);
            RxAnt = parseIntArray(header, "RxAnt=", 8);

            // This normally ends up only having 1 value left for each of TxAnt and RxAnt.
            while (TxAnt.contains(0)) {
                TxAnt.remove((Integer) 0);
            }

            while (RxAnt.contains(0)) {
                RxAnt.remove((Integer) 0);
            }

            // Average determines what format the file is in.
            if (header.contains("Average=")) {
                average = parseInt(header, "Average=");
            } else {
                average = 0;
            }

            int wordSize; // Number of bytes per data word. Either 4 or 2 depending on format.
            if (average != 0) {
                wordSize = 4;
                chirpsInBurst = 1;
            } else {
                wordSize = 2;
                chirpsInBurst = subBurstsInBurst * TxAnt.size() * RxAnt.size() * nAttenuators;
            }

            // Extract remaining information from header
            SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            ft.setTimeZone(TimeZone.getTimeZone("GMT"));
            dateTime = ft.parse(parseString(header, "Time stamp="));

            temperature1 = parseDouble(header, "Temp1=");
            temperature2 = parseDouble(header, "Temp2=");
            if (temperature1 > 300) {
                temperature1 -= 512;
            }
            if (temperature2 > 300) {
                temperature2 -= 512;
            }

            double tStepUp = parseReg(header, "Reg0D=", 5, 9) * 4 / fSysClk;
            double freqDiff = parseReg(header, "Reg0B=", 1, 9) - parseReg(header, "Reg0B=", 9, 17);
            double rampUpStep = parseReg(header, "Reg0C=", 9, 17);
            t = Math.round(Math.abs(freqDiff / rampUpStep)) * tStepUp;
            t = Math.min(t, nSamples / fs);
            samplesPerChirp = t * fs;

            double rampDnStep = parseReg(header, "Reg0C=", 1, 9) * fSysClk / Math.pow(2, 32);
            k = (2 * Math.PI * rampDnStep) / (tStepUp);

            String searchString = "*** End Header ***";
            int burstPointer = header.indexOf(searchString) + searchString.length();

            int burstSize = chirpsInBurst * nSamples * wordSize;

            // Subtract 1 because it's 1 indexed.
            burstPointer += (burstNum - 1) * (burstSize);

            if (burstPointer > file.length()) {
                throw new InvalidBurstException("Incorrect number of bursts in the file.");
            }
            fChannel.position(burstPointer);

            startInd = new ArrayList<>();
            endInd = new ArrayList<>();
            for (int i = 0; i < nSamples * chirpsInBurst; i += nSamples) {
                startInd.add(i);
                endInd.add(i + nSamples - 1);
            }

            List<Complex> attSet = new ArrayList<>();
            for (int i = 0; i < attenuator1.size(); i++)
                attSet.add(new Complex(attenuator1.get(i), attenuator2.get(i)));

            double chirpInterval = 1.6384; // 1.6384 / (24 * 3600)

            if (average == 1) {
                fChannel.position(burstPointer + 1);
            }

            ByteBuffer bb = ByteBuffer.wrap(readFile(f, burstSize));
            bb.order(ByteOrder.LITTLE_ENDIAN);
            vif = new ArrayList<>();

            for (int chirp = 0; chirp < chirpsInBurst; chirp++) {
                ArrayList<Double> temp = new ArrayList<>();
                for (int i = 0; i < nSamples; i++) {
                    double d;
                    switch (average) {
                        case 2:
                            d = bb.getInt();
                            break;
                        case 1:
                            d = bb.getFloat();
                            break;
                        default:
                            d = bb.getShort();
                            break;
                    }

                    if (d < 0) {
                        d += Math.pow(2, 16);
                    }
                    d = (d * 2.5 / Math.pow(2, 16));

                    if (average == 2) {
                        d = d / (subBurstsInBurst * nAttenuators);
                    }

                    temp.add(d);
                }
                vif.add(temp);
                chirpNum.add(chirp);
                chirpAtt.add(attSet.get((chirp) % attSet.size())); // TODO check for off by 1 error
                chirpTime.add(new Date(dateTime.getTime() + (long) (chirp * chirpInterval)));
            }

            for (int i = 0; i < vif.size(); i++) {
                vif.set(i, vif.get(i).subList(0, (int) samplesPerChirp));
            }

            if (mean) {
                chirpsInBurst = 1;

                ArrayList<Double> chirpAverage = new ArrayList<>();
                for (int i = 0; i < vif.get(0).size(); i++) {
                    double value = 0;
                    for (int j = 0; j < vif.size(); j++) {
                        value += vif.get(j).get(i);
                    }
                    value /= vif.size();
                    chirpAverage.add(value);
                }
                vif.clear();
                vif.add(chirpAverage);

                // using BigInteger to avoid value overflow
                BigInteger total = BigInteger.ZERO;
                for (Date date : chirpTime) {
                    total = total.add(BigInteger.valueOf(date.getTime()));
                }
                BigInteger averageMillis = total.divide(BigInteger.valueOf(chirpTime.size()));
                Date averageDate = new Date(averageMillis.longValue());

                chirpTime.clear();
                chirpTime.add(averageDate);

                Complex averageComplex = new Complex(0);
                for (Complex c : chirpAtt) {
                    averageComplex = averageComplex.add(c);
                }
                averageComplex = averageComplex.divide(chirpAtt.size());
                chirpAtt.clear();
                chirpAtt.add(averageComplex);

                processing.add("burst mean");
            }
        } catch (InvalidBurstException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidBurstException(
                    String.format("Failed to parse file\nError Message: %s\n", e.toString()));
        }
    }

    /**
     * Returns NSamples parameter
     *
     * @return NSamples parameter
     */
    public int getNSamples() {
        return nSamples;
    }

    /**
     * Returns SubBurstsInBurst parameter
     *
     * @return SubBurstsInBurst parameter
     */
    public int getSubBurstsInBurst() {
        return subBurstsInBurst;
    }

    /**
     * Returns Average parameter
     *
     * @return Average parameter
     */
    public int getAverage() {
        return average;
    }

    /**
     * Returns NAttenuators parameter
     *
     * @return NAttenuators parameter
     */
    public int getNAttenuators() {
        return nAttenuators;
    }

    /**
     * Returns Attenuator1 parameter
     *
     * @return Attenuator1 parameter
     */
    public List<Double> getAttenuator1() {
        return new ArrayList<>(attenuator1);
    }

    /**
     * Returns Attenuator2 parameter
     *
     * @return Attenuator2 parameter
     */
    public List<Double> getAttenuator2() {
        return new ArrayList<>(attenuator2);
    }

    /**
     * Returns TxAnt parameter
     *
     * @return TxAnt parameter
     */
    public List<Integer> getTxAnt() {
        return new ArrayList<>(TxAnt);
    }

    /**
     * Returns RxAnt parameter
     *
     * @return RxAnt parameter
     */
    public List<Integer> getRxAnt() {
        return new ArrayList<>(RxAnt);
    }

    /**
     * Returns ChirpsInBurst parameter
     *
     * @return ChirpsInBurst parameter
     */
    public int getChirpsInBurst() {
        return chirpsInBurst;
    }

    /**
     * Returns TimeStamp parameter
     *
     * @return TimeStamp parameter
     */
    public Date getTimeStamp() {
        return dateTime;
    }

    /**
     * Returns Temperature1 parameter
     *
     * @return Temperature1 parameter
     */
    public double getTemperature1() {
        return temperature1;
    }

    /**
     * Returns Temperature2 parameter
     *
     * @return Temperature1 parameter
     */
    public double getTemperature2() {
        return temperature2;
    }

    /**
     * Returns BatteryVoltage parameter
     *
     * @return BatteryVoltage parameter
     */
    public double getBatteryVoltage() {
        return batteryVoltage;
    }

    /**
     * Returns StartInd parameter
     *
     * <p>implemented as {@code List<Integer>} since in the original MATLAB it was a matrix with a *
     * height of 1
     *
     * @return StartInd parameter
     */
    public List<Integer> getStartInd() {
        return new ArrayList<>(startInd);
    }

    /**
     * Returns EndInd parameter
     *
     * <p>implemented as {@code List<Integer>} since in the original MATLAB it was a matrix with a *
     * height of 1
     *
     * @return EndInd parameter
     */
    public List<Integer> getEndInd() {
        return new ArrayList<>(endInd);
    }

    /**
     * Returns Burst parameter
     *
     * @return Burst parameter
     */
    public int getBurst() {
        return burst;
    }

    /**
     * Returns FileFormat parameter
     *
     * @return FileFormat parameter
     */
    public int getFileFormat() {
        return fileFormat;
    }

    /**
     * Returns vif parameter
     *
     * <p>Implemented as {@code List<List<Double>>} since in the original MATLAB code it was a
     * 2D-Matrix
     *
     * @return vif parameter
     */
    public List<List<Double>> getVif() {
        List<List<Double>> vifClone = new ArrayList<>();

        for (List<Double> arr : vif) vifClone.add(new ArrayList<>(arr));

        return vifClone;
    }

    /**
     * Returns ChirpNum parameter
     *
     * <p>Implemented as {@code List<Integer>} since in the original MATLAB it was a matrix with a
     * height of 1
     *
     * @return ChirpNum parameter
     */
    public List<Integer> getChirpNum() {
        return new ArrayList<>(chirpNum);
    }

    /**
     * Returns ChirpTime parameter
     *
     * <p>Implemented as {@code List<Date>} since in the original MATLAB it was a matrix with a
     * height of 1
     *
     * <p>Returns a {@code Date} object as their is no simple equivalent for the {@code DateNumber}
     * from MATLAB in java
     *
     * @return ChirpTime parameter
     */
    public List<Date> getChirpTime() {
        return new ArrayList<>(chirpTime);
    }

    /**
     * Returns filename parameter
     *
     * @return filename parameter
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Returns SamplesPerChirp parameter
     *
     * @return SamplesPerChirp parameter
     */
    public double getSamplesPerChirp() {
        return samplesPerChirp;
    }

    /**
     * Returns fs parameter
     *
     * @return fs parameter
     */
    public double getFs() {
        return fs;
    }

    /**
     * Returns f0 parameter
     *
     * @return f0 parameter
     */
    public double getF0() {
        return f0;
    }

    /**
     * Returns K parameter
     *
     * @return K parameter
     */
    public double getK() {
        return k;
    }

    /**
     * Returns processing parameter
     *
     * <p>Implemented as {@code List<String>} since in the original MATLAB it was a matrix with a
     * height of 1
     *
     * <p>This parameters says how the data has been processed, currently there are 2 options:
     *
     * <p>{} (empty list) -> No processing has been done to the data, and is the same as the file
     *
     * <p>"burst mean" -> The mean of the data has been calculated and saved in the object
     * (Implements {@code fmcw_burst_mean()}
     *
     * @return processing parameter
     */
    public List<String> getProcessing() {
        return new ArrayList<>(processing);
    }

    /**
     * Returns f1 parameter
     *
     * @return f1 parameter
     */
    public double getF1() {
        return getF0() + getB();
    }

    /**
     * Returns T parameter.
     *
     * <p>(Name conflict with t in vdat -> t renamed to tList)
     *
     * @return T parameter
     */
    public double getT() {
        return t;
    }

    /**
     * Returns B parameter
     *
     * @return B parameter
     */
    public double getB() {
        return (getT() * getK()) / (2 * Math.PI);
    }

    /**
     * Returns fc parameter
     *
     * @return fc parameter
     */
    public double getFc() {
        return getF0() + (getB() / 2);
    }

    /**
     * Returns dt parameter
     *
     * @return dt parameter
     */
    public double getDt() {
        return dt;
    }

    /**
     * Returns er parameter
     *
     * @return er parameter
     */
    public double getEr() {
        return er;
    }

    /**
     * Returns ci parameter
     *
     * @return ci parameter
     */
    public double getCi() {
        return 3e8 / Math.sqrt(getEr());
    }

    /**
     * Returns lambdac parameter
     *
     * @return lambdac parameter
     */
    public double getLambdac() {
        return getCi() / getFc();
    }

    /**
     * Returns tList parameter (originally called t in vdat but changed due to name conflict with T
     * parameter).
     *
     * <p>Implemented as {@code List<Double>} since in the original MATLAB it was a matrix with a
     * height of 1.
     *
     * @return t parameter
     */
    public List<Double> getTList() {
        ArrayList<Double> temp = new ArrayList<>();
        for (int i = 0; i < getSamplesPerChirp(); i++) {
            temp.add(getDt() * i);
        }
        return temp;
    }

    /**
     * Returns f parameter.
     *
     * <p>Implemented as {@code List<Double>} since in the original MATLAB it was a matrix with a
     * height of 1.
     *
     * @return f parameter
     */
    public List<Double> getFList() {
        ArrayList<Double> temp = new ArrayList<>();
        for (int i = 0; i < getSamplesPerChirp(); i++) {
            temp.add(f0 + (i * dt * k) / (2 * Math.PI));
        }
        return temp;
    }

    /**
     * Returns ChirpAtt parameter.
     *
     * <p>Implemented as {code List<Complex>} since in the original MATLAB it was a matrix with a
     * height of 1
     *
     * @return chirpAtt parameter
     */
    public List<Complex> getChirpAtt() {
        return new ArrayList<>(chirpAtt);
    }

    private String parseString(String mainString, String searchString) {
        int searchInd = mainString.indexOf(searchString);
        int searchCR = mainString.indexOf("\r\n", searchInd);
        return mainString.substring(searchInd + searchString.length(), searchCR);
    }

    private int parseInt(String mainString, String searchString) {
        return Integer.parseInt(parseString(mainString, searchString));
    }

    private double parseDouble(String mainString, String searchString) {
        return Double.parseDouble(parseString(mainString, searchString));
    }

    private List<Integer> parseIntArray(String mainString, String searchString, int expectedSize)
            throws InvalidBurstException {
        ArrayList<Integer> ret = new ArrayList<>();
        String[] a_split = parseString(mainString, searchString).split(",");
        for (String s : a_split) {
            ret.add(Integer.parseInt(s));
        }
        if (ret.size() != expectedSize) {
            throw new InvalidBurstException(
                    String.format(
                            "Wrong number of values searhcing for %s. Expected %d, found %d",
                            searchString, expectedSize, ret.size()));
        }
        return ret;
    }

    private List<Double> parseDoubleArray(String mainString, String searchString) {
        ArrayList<Double> ret = new ArrayList<>();
        String[] a_split = parseString(mainString, searchString).split(",");
        for (String s : a_split) {
            ret.add(Double.parseDouble(s));
        }
        return ret;
    }

    private byte[] readFile(FileInputStream f, int readTotal) throws IOException {
        int count = 0;
        byte b[] = new byte[readTotal];
        while (count < readTotal) {
            count = f.read(b, count, readTotal - count);
        }
        return b;
    }

    private long parseReg(String mainString, String searchString, int start, int finish) {
        String s = parseString(mainString, searchString);
        return Long.decode("0x" + s.substring(start, finish));
    }
}

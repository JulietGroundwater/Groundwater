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
    private int subBurstsInBurst;
    private int average;
    private int nAttenuators;
    private int code = 0;
    private int nSamples = 0;
    private int chirpsInBurst = 0;
    private int burst = 0;
    private int fileFormat;
    private double fs;
    private Date dateTime;
    private double temperature1,
            temperature2,
            batteryVoltage,
            samplesPerChirp,
            f0,
            k,
            f1,
            t,
            b,
            fc,
            dt,
            er,
            ci,
            lambdac;
    private List<Double> attenuator1, attenuator2, tList, fList;
    private List<String> processing;
    private List<Complex> chirpAtt = new ArrayList<>();
    private List<Date> chirpTime = new ArrayList<>();
    private List<Integer> startInd, endInd, TxAnt, RxAnt, chirpNum = new ArrayList<>();
    private List<List<Double>> vif;

    // Constants
    private static final double fSysClk = 1e9;
    private static final int MaxHeaderLen = 1500;

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
        loadBurstRMB5(burstNum, file, mean);

        if (code != 0) {
            throw new InvalidBurstException("Unable to correctly create burst");
        }
    }

    /**
     * Returns the Code parameter
     *
     * @return Code parameter
     */
    public int getCode() {
        return code;
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
        return f1;
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
        return b;
    }

    /**
     * Returns fc parameter
     *
     * @return fc parameter
     */
    public double getFc() {
        return fc;
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
        return ci;
    }

    /**
     * Returns lambdac parameter
     *
     * @return lambdac parameter
     */
    public double getLambdac() {
        return lambdac;
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
        return new ArrayList<>(tList);
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
        return new ArrayList<>(fList);
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

    private void loadBurstRMB5(int totalNumberOfBursts, File file, boolean mean)
            throws InvalidBurstException {
        fs = 4e4;
        code = 0;
        fileFormat = 5;
        processing = new ArrayList<>();

        if (file == null) {
            throw new InvalidBurstException("file cannot be null");
        }

        filename = file.getName();

        if (!filename.contains(".DAT")) {
            throw new InvalidBurstException("file must be a .DAT file");
        }
        long fileLength = file.length();

        try (FileInputStream f = new FileInputStream(file)) {
            FileChannel fChannel = f.getChannel();
            int burstCount = 1;
            int burstPointer = 0;
            int wordSize;

            String A = new String(readFile(f, MaxHeaderLen));

            nSamples = parseInt(A, "N_ADC_SAMPLES=");
            subBurstsInBurst = parseInt(A, "NSubBursts=");
            nAttenuators = parseInt(A, "nAttenuators=");
            attenuator1 = parseDoubleArray(A, "Attenuator1=");
            attenuator2 = parseDoubleArray(A, "AFGain=");
            TxAnt = parseIntArray(A, "TxAnt=", 8);
            RxAnt = parseIntArray(A, "RxAnt=", 8);

            while (TxAnt.contains(0)) {
                TxAnt.remove((Integer) 0);
            }

            while (RxAnt.contains(0)) {
                RxAnt.remove((Integer) 0);
            }

            if (A.contains("Average=")) {
                average = parseInt(A, "Average=");
            } else {
                average = 0;
            }

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
            dateTime = ft.parse(parseString(A, "Time stamp="));

            temperature1 = parseDouble(A, "Temp1=");
            temperature2 = parseDouble(A, "Temp2=");
            if (temperature1 > 300) {
                temperature1 -= 512;
            }
            if (temperature2 > 300) {
                temperature2 -= 512;
            }

            batteryVoltage = parseDouble(A, "BatteryVoltage=");

            String Reg0B = parseString(A, "Reg0B=");
            String Reg0C = parseString(A, "Reg0C=");
            String Reg0D = parseString(A, "Reg0D=");

            f0 = regDecode(Reg0B, 9, 17) * fSysClk / Math.pow(2, 32);

            double tStepUp = regDecode(Reg0D, 5, 9) * 4 / fSysClk;
            double freqDiff = regDecode(Reg0B, 1, 9)  - regDecode(Reg0B, 9, 17);
            double rampUpStep = regDecode(Reg0C, 9, 17);
            double chirpLength = Math.round(Math.abs(freqDiff / rampUpStep)) * tStepUp;

            if (chirpLength * fs > nSamples) {
                chirpLength = nSamples / fs;
            }

            double rampDnStep = regDecode(Reg0C, 1, 9) * fSysClk / Math.pow(2, 32);
            k = 2 * Math.PI * (rampDnStep / tStepUp);

            f1 = f0 + chirpLength * k / 2 / Math.PI;
            samplesPerChirp = chirpLength * fs;
            t = chirpLength;
            b = chirpLength * k / 2 / Math.PI;
            fc = f0 + b / 2;
            dt = 1 / fs;
            er = 3.18;
            ci = 3e8 / Math.sqrt(er);
            lambdac = ci / fc;

            tList = new ArrayList<>();
            fList = new ArrayList<>();

            for (int i = 0; i < samplesPerChirp; i++) {
                double tempT = dt * i;
                tList.add(tempT);
                fList.add(f0 + tempT * (k / (2 * Math.PI)));
            }

            String searchString = "*** End Header ***";
            burstPointer += A.indexOf(searchString) + searchString.length();

            int wordsPerBurst = chirpsInBurst * nSamples;

            while ((burstCount < totalNumberOfBursts)
                    && (burstPointer <= fileLength - MaxHeaderLen)) {
                burstPointer += (wordsPerBurst * wordSize);
                burstCount++;
            }

            if (burstPointer != 0) {
                fChannel.position(burstPointer);
            }

            if (burstCount != totalNumberOfBursts) {
                // Too few bursts in file
                burst = burstCount;
                code = -4;
                throw new InvalidBurstException("Incorrect number of bursts in the file");
            }

            startInd = new ArrayList<>();
            endInd = new ArrayList<>();
            for (int i = 0; i < nSamples * chirpsInBurst; i += nSamples) {
                startInd.add(i);
                endInd.add(i + nSamples - 1);
            }

            burst = totalNumberOfBursts;

            List<Complex> attSet = new ArrayList<>();
            for (int i = 0; i < attenuator1.size(); i++)
                attSet.add(new Complex(attenuator1.get(i), attenuator2.get(i)));

            double chirpInterval = 1.6384; // 1.6384 / (24 * 3600)

            if (average == 1) {
                fChannel.position(burstPointer + 1);
            }

            ByteBuffer bb = ByteBuffer.wrap(readFile(f, wordsPerBurst * wordSize));
            bb.order(ByteOrder.LITTLE_ENDIAN);
            double d;
            vif = new ArrayList<>();

            for (int chirp = 0; chirp < chirpsInBurst; chirp++) {
                ArrayList<Double> temp = new ArrayList<>();
                for (int i = 0; i < nSamples; i++) {
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

                chirpsInBurst = 1;

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
            if (code == 0) code = 1;
            e.printStackTrace();
            throw new InvalidBurstException(
                    String.format(
                            "Failed to parse file, code %d\nError Message: %s\n",
                            code, e.toString()));
        }
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

    private long regDecode(String s, int start, int finish) {
        return Long.decode("0x" + s.substring(start, finish));
    }
}

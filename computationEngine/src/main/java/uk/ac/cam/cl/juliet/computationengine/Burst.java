package uk.ac.cam.cl.juliet.computationengine;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Class to represent a Burst from the radar, this is a java representation of the vdat struct from
 * {@code fmcw_load()}. The only required methods are getters for each of the fields in the struct.
 */
public class Burst {

    public Burst(String filename, int burstNum) throws InvalidBurstException {
        this.filename = filename;
        LoadBurstRMB5(burstNum);

        if (temperature1 > 300) {
            temperature1 -= 512;
        }
        if (temperature2 > 300) {
            temperature2 -= 512;
        }

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
    public ArrayList<Double> getAttenuator1() {
        return attenuator1;
    }

    /**
     * Returns Attenuator2 parameter
     *
     * @return Attenuator2 parameter
     */
    public ArrayList<Double> getAttenuator2() {
        return attenuator2;
    }

    /**
     * Returns TxAnt parameter
     *
     * @return TxAnt parameter
     */
    public ArrayList<Integer> getTxAnt() {
        return TxAnt;
    }

    /**
     * Returns RxAnt parameter
     *
     * @return RxAnt parameter
     */
    public ArrayList<Integer> getRxAnt() {
        return RxAnt;
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
     * Returns V parameter
     *
     * <p>implemented as {@code List<Double>} since in the original MATLAB it was a matrix with a
     * height of 1
     *
     * @return V parameter
     */
    public List<Double> getV() {
        return v;
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
        return startInd;
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
        return endInd;
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
        // TODO
        return 0;
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
        // TODO
        return null;
    }

    /**
     * Returns ChirpNum parameter
     *
     * <p>Implemented as {@code List<Double>} since in the original MATLAB it was a matrix with a
     * height of 1
     *
     * @return ChirpNum parameter
     */
    public List<Double> getChirpNum() {
        // TODO
        return null;
    }

    /**
     * Returns ChirpTime parameter
     *
     * <p>Implemented as {@code List<Double>} since in the original MATLAB it was a matrix with a
     * height of 1
     *
     * @return ChirpTime parameter
     */
    public List<Double> getChirpTime() {
        // TODO
        return null;
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
        // TODO
        return 0;
    }

    /**
     * Returns fs parameter
     *
     * @return fs parameter
     */
    public int getFs() {
        // TODO
        return 0;
    }

    /**
     * Returns f0 parameter
     *
     * @return f0 parameter
     */
    public double getF0() {
        // TODO
        return 0;
    }

    /**
     * Returns K parameter
     *
     * @return K parameter
     */
    public double getK() {
        // TODO
        return 0;
    }

    /**
     * Returns processing parameter
     *
     * <p>Implemented as {@code List<Double>} since in the original MATLAB it was a matrix with a
     * height of 1
     *
     * <p>In the original MATLAB processing is always set to {@code {}}, so this could be removed
     *
     * @return processing parameter
     */
    public List<Double> getProcessing() {
        // TODO
        return null;
    }

    /**
     * Returns f1 parameter
     *
     * @return f1 parameter
     */
    public double getF1() {
        // TODO
        return 0;
    }

    /**
     * Returns B parameter.
     *
     * <p>(Name conflict with t in vdat -> t renamed to tList)
     *
     * @return B parameter
     */
    public double getT() {
        // TODO
        return 0;
    }

    /**
     * Returns B parameter
     *
     * @return B parameter
     */
    public double getB() {
        // TODO
        return 0;
    }

    /**
     * Returns fc parameter
     *
     * @return fc parameter
     */
    public double getFc() {
        // TODO
        return 0;
    }

    /**
     * Returns dt parameter
     *
     * @return dt parameter
     */
    public double getDt() {
        // TODO
        return 0;
    }

    /**
     * Returns er parameter
     *
     * @return er parameter
     */
    public double getEr() {
        // TODO
        return 0;
    }

    /**
     * Returns ci parameter
     *
     * @return ci parameter
     */
    public double getCi() {
        // TODO
        return 0;
    }

    /**
     * Returns lambdac parameter
     *
     * @return lambdac parameter
     */
    public double getLambdac() {
        // TODO
        return 0;
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
        // TODO
        return null;
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
        // TODO
        return null;
    }

    private void LoadBurstRMB5(int totalNumberOfBursts) throws InvalidBurstException {
        int MaxHeaderLen = 1500;
        int burstpointer = 0;
        code = 0;

        long fileLength;
        File file;
        file = new File(filename);
        fileLength = file.length();

        try (FileInputStream f = new FileInputStream(new File(filename))) {
            FileChannel fc = f.getChannel();
            int burstCount = 1;
            long current_stream_pos = 0;
            int wordsPerBurst = 0;
            int WperChirpCycle;

            fc.position(burstpointer);
            byte headerBytes[] = new byte[MaxHeaderLen];
            while (current_stream_pos != MaxHeaderLen) {
                current_stream_pos +=
                        f.read(
                                headerBytes,
                                (int) current_stream_pos,
                                (int) (MaxHeaderLen - current_stream_pos));
            }
            String A = new String(headerBytes);

            nSamples = parseInt(A, "N_ADC_SAMPLES=");
            WperChirpCycle = nSamples;
            subBurstsInBurst = parseInt(A, "NSubBursts=");

            int[] searchind = strFind(A, "Average=");
            if (searchind.length == 0) {
                average = 0;
            } else {
                average = parseInt(A, "Average=");
            }

            nAttenuators = parseInt(A, "nAttenuators=");
            attenuator1 = parseDoubleArray(A, "Attenuator1=");
            attenuator2 = parseDoubleArray(A, "AFGain=");
            TxAnt = parseIntArray(A, "TxAnt=");
            RxAnt = parseIntArray(A, "RxAnt=");

            if (TxAnt.size() != 8) {
                throw new InvalidBurstException("TxAnt has wrong number of values");
            }

            if (RxAnt.size() != 8) {
                throw new InvalidBurstException("RxAnt has wrong number of values");
            }

            while (TxAnt.contains(0)) {
                TxAnt.remove(TxAnt.indexOf(0));
            }

            while (RxAnt.contains(0)) {
                RxAnt.remove(RxAnt.indexOf(0));
            }

            if (average != 0) {
                chirpsInBurst = 1;
            } else {
                chirpsInBurst = subBurstsInBurst * TxAnt.size() * RxAnt.size() * nAttenuators;
            }

            String searchString = "*** End Header ***";
            searchind = strFind(A, searchString);

            burstpointer += searchind[0] + searchString.length();

            // Extract remaining information from header
            SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            ft.setTimeZone(TimeZone.getTimeZone("GMT"));
            dateTime = ft.parse(parseString(A, "Time stamp="));

            temperature1 = parseDouble(A, "Temp1=");
            temperature2 = parseDouble(A, "Temp2=");
            batteryVoltage = parseDouble(A, "BatteryVoltage=");

            while ((burstCount <= totalNumberOfBursts)
                    && (burstpointer <= fileLength - MaxHeaderLen)) {
                wordsPerBurst = chirpsInBurst * WperChirpCycle;
                if (burstCount < totalNumberOfBursts && burstpointer <= fileLength - MaxHeaderLen) {
                    if (average != 0) {
                        burstpointer += (chirpsInBurst * WperChirpCycle * 4);
                    } else {
                        burstpointer += (chirpsInBurst * WperChirpCycle * 2);
                    }
                }
                burstCount++;
            }

            if (burstpointer != 0) {
                fc.position(burstpointer - 1);
            }

            if (burstCount == totalNumberOfBursts + 1) {
                int count = 0;
                if (average == 2) {
                    int readTotal = wordsPerBurst * 4;
                    byte b[] = new byte[readTotal];
                    while (count < readTotal) {
                        count = f.read(b, count, readTotal - count);
                    }

                    v = new ArrayList<>();

                    for (int i = 0; i < wordsPerBurst; i++) {
                        // Little Endian.
                        int x =
                                b[4 * i]
                                        + (256 * b[4 * i + 1])
                                        + (256 * 256 * b[4 * i + 2])
                                        + (256 * 256 * 256 * b[4 * i + 3]);
                        v.add((double) x);
                    }
                } else if (average == 1) {
                    fc.position(burstpointer + 1);

                    int readTotal = wordsPerBurst * 4;
                    byte b[] = new byte[readTotal];
                    while (count < readTotal) {
                        count = f.read(b, count, readTotal - count);
                    }

                    v = new ArrayList<>();

                    for (int i = 0; i < wordsPerBurst; i++) {
                        // Little Endian.
                        float x =
                                ByteBuffer.wrap(b, 4 * i, 4)
                                        .order(ByteOrder.LITTLE_ENDIAN)
                                        .getFloat();
                        v.add((double) x);
                    }
                } else {
                    int readTotal = wordsPerBurst * 2;
                    byte b[] = new byte[readTotal];
                    while (count < readTotal) {
                        count = f.read(b, count, readTotal - count);
                    }

                    v = new ArrayList<>();

                    for (int i = 0; i < wordsPerBurst; i++) {
                        // Little Endian.
                        int x = b[2 * i] + (256 * b[2 * i + 1]);
                        v.add((double) x);
                    }
                }

                for (int i = 0; i < v.size(); i++) {
                    if (v.get(i) < 0) {
                        v.set(i, v.get(i) + Math.pow(2, 16));
                    }
                    v.set(i, v.get(i) * 2.5 / Math.pow(2, 16));
                }

                if (average == 2) {
                    for (int i = 0; i < v.size(); i++) {
                        v.set(i, v.get(i) / (subBurstsInBurst * nAttenuators));
                    }
                }

                startInd = new ArrayList<>();
                endInd = new ArrayList<>();
                for (int i = 1; i <= WperChirpCycle * chirpsInBurst; i += WperChirpCycle) {
                    startInd.add(i);
                    endInd.add(i + WperChirpCycle - 1);
                }

                burst = totalNumberOfBursts;
            } else {
                // Too few bursts in file
                burst = burstCount - 1;
                code = -4;
            }
        } catch (InvalidBurstException e) {
            throw e;
        } catch (Exception e) {
            if (code == 0) code = 1;
            throw new InvalidBurstException(String.format("Failed to parse file, code %d", code));
        }
    }

    private int[] strFind(String original, String substring) {
        ArrayList<Integer> list = new ArrayList<>();
        int lastIndex = 0;
        while (lastIndex != -1) {

            lastIndex = original.indexOf(substring, lastIndex);

            if (lastIndex != -1) {
                list.add(lastIndex);
                lastIndex += 1;
            }
        }

        int[] ret = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            ret[i] = list.get(i);
        }

        return ret;
    }

    private String parseString(String mainString, String searchString) {
        int[] searchInd = strFind(mainString, searchString);
        int[] searchCR = strFind(mainString.substring(searchInd[0]), "\r\n");
        return mainString.substring(
                searchInd[0] + searchString.length(), searchCR[0] + searchInd[0]);
    }

    private int parseInt(String mainString, String searchString) {
        return Integer.parseInt(parseString(mainString, searchString));
    }

    private double parseDouble(String mainString, String searchString) {
        return Double.parseDouble(parseString(mainString, searchString));
    }

    private ArrayList<Integer> parseIntArray(String mainString, String searchString) {
        ArrayList<Integer> ret = new ArrayList<>();
        String[] a_split = parseString(mainString, searchString).split(",");
        for (String s : a_split) {
            ret.add(Integer.parseInt(s));
        }
        return ret;
    }

    private ArrayList<Double> parseDoubleArray(String mainString, String searchString) {
        ArrayList<Double> ret = new ArrayList<>();
        String[] a_split = parseString(mainString, searchString).split(",");
        for (String s : a_split) {
            ret.add(Double.parseDouble(s));
        }
        return ret;
    }

    private String filename;
    private int subBurstsInBurst;
    private int average;
    private int nAttenuators;
    private int code = 0;
    private int nSamples = 0;
    private int chirpsInBurst = 0;
    private int burst = 0;
    private Date dateTime;
    private double temperature1;
    private double temperature2;
    private double batteryVoltage;
    private ArrayList<Double> attenuator1;
    private ArrayList<Double> attenuator2;
    private ArrayList<Double> v;
    private ArrayList<Integer> startInd;
    private ArrayList<Integer> endInd;
    private ArrayList<Integer> TxAnt;
    private ArrayList<Integer> RxAnt;
}

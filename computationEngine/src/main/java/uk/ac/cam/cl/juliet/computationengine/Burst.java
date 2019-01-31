package uk.ac.cam.cl.juliet.computationengine;

import java.util.List;

/**
 * Interface to represent a Burst from the radar, this is a java representation of the vdat struct
 * from {@code fmcw_load()}. The only required methods are getters for each of the fields in the
 * struct.
 */
public class Burst {

  public Burst(String filename, int burstNum) {
    // TODO
  }

  /**
   * Returns the Code parameter
   *
   * @return Code parameter
   */
  public int getCode() {
    // TODO
    return 0;
  }

  /**
   * Returns NSamples parameter
   *
   * @return NSamples parameter
   */
  public int getNSamples() {
    // TODO
    return 0;
  }

  /**
   * Returns SubBurstsInBurst parameter
   *
   * @return SubBurstsInBurst parameter
   */
  public int getSubBurstsInBurst() {
    // TODO
    return 0;
  }

  /**
   * Returns Average parameter
   *
   * @return Average parameter
   */
  public int getAverage() {
    // TODO
    return 0;
  }

  /**
   * Returns NAttenuators parameter
   *
   * @return NAttenuators parameter
   */
  public int getNAttenuators() {
    // TODO
    return 0;
  }

  /**
   * Returns Attenuator1 parameter
   *
   * @return Attenuator1 parameter
   */
  public int getAttenuator1() {
    // TODO
    return 0;
  }

  /**
   * Returns Attenuator2 parameter
   *
   * @return Attenuator2 parameter
   */
  public int getAttenuator2() {
    // TODO
    return 0;
  }

  /**
   * Returns TxAnt parameter
   *
   * @return TxAnt parameter
   */
  public int getTxAnt() {
    // TODO
    return 0;
  }

  /**
   * Returns RxAnt parameter
   *
   * @return RxAnt parameter
   */
  public int RxAnt() {
    // TODO
    return 0;
  }

  /**
   * Returns ChirpsInBurst parameter
   *
   * @return ChirpsInBurst parameter
   */
  public int getChirpsInBurst() {
    //TODO
    return 0;
  }

  /**
   * Returns TimeStamp parameter
   *
   * @return TimeStamp parameter
   */
  public double getTimeStamp() {
    //TODO
    return 0;
  }

  /**
   * Returns Temperature1 parameter
   *
   * @return Temperature1 parameter
   */
  public double getTemperature1() {
    //TODO
    return 0;
  }

  /**
   * Returns Temperature2 parameter
   *
   * @return Temperature1 parameter
   */
  public double getTemperature2() {
    //TODO
    return 0;
  }

  /**
   * Returns BatteryVoltage parameter
   *
   * @return BatteryVoltage parameter
   */
  public double getBatteryVoltage() {
    //TODO
    return 0;
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
    //TODO
    return null;
  }

  /**
   * Returns StartInd parameter
   *
   * <p>implemented as {@code List<Double>} since in the original MATLAB it was a matrix with a *
   * height of 1
   *
   * @return StartInd parameter
   */
  public List<Double> getStartInd() {
    //TODO
    return null;
  }

  /**
   * Returns EndInd parameter
   *
   * <p>implemented as {@code List<Double>} since in the original MATLAB it was a matrix with a *
   * height of 1
   *
   * @return EndInd parameter
   */
  public List<Double> getEndInd() {
    //TODO
    return null;
  }

  /**
   * Returns Burst parameter
   *
   * @return Burst parameter
   */
  public int getBurst() {
    //TODO
    return 0;
  }

  /**
   * Returns FileFormat parameter
   *
   * @return FileFormat parameter
   */
  public int getFileFormat() {
    //TODO
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
    //TODO
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
    //TODO
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
    //TODO
    return null;
  }

  /**
   * Returns filename parameter
   *
   * @return filename parameter
   */
  public String getFilename() {
    //TODO
    return null;
  }

  /**
   * Returns SamplesPerChirp parameter
   *
   * @return SamplesPerChirp parameter
   */
  public double getSamplesPerChirp() {
    //TODO
    return 0;
  }

  /**
   * Returns fs parameter
   *
   * @return fs parameter
   */
  public int getFs() {
    //TODO
    return 0;
  }

  /**
   * Returns f0 parameter
   *
   * @return f0 parameter
   */
  public double getF0() {
    //TODO
    return 0;
  }

  /**
   * Returns K parameter
   *
   * @return K parameter
   */
  public double getK() {
    //TODO
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
    //TODO
    return null;
  }

  /**
   * Returns f1 parameter
   *
   * @return f1 parameter
   */
  public double getF1() {
    //TODO
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
    //TODO
    return 0;
  }

  /**
   * Returns B parameter
   *
   * @return B parameter
   */
  public double getB() {
    //TODO
    return 0;
  }

  /**
   * Returns fc parameter
   *
   * @return fc parameter
   */
  public double getFc() {
    //TODO
    return 0;
  }

  /**
   * Returns dt parameter
   *
   * @return dt parameter
   */
  public double getDt() {
    //TODO
    return 0;
  }

  /**
   * Returns er parameter
   *
   * @return er parameter
   */
  public double getEr() {
    //TODO
    return 0;
  }

  /**
   * Returns ci parameter
   *
   * @return ci parameter
   */
  public double getCi() {
    //TODO
    return 0;
  }

  /**
   * Returns lambdac parameter
   *
   * @return lambdac parameter
   */
  public double getLambdac() {
    //TODO
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
    //TODO
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
    //TODO
    return null;
  }
}

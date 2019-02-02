package uk.ac.cam.cl.juliet.computationengine.range;

import org.apache.commons.math3.complex.Complex;

import java.util.ArrayList;
import java.util.List;

//TODO : Unit testing of everything
public class ComplexVector {
    private List<Complex> values;

    public ComplexVector() {
        values = new ArrayList<>();
    }

    public ComplexVector(List<Double> values) {
        this.values = new ArrayList<>();

        for (int i = 0; i < values.size(); i++) {
            this.values.add(new Complex(values.get(i)));
        }
    }

    public ComplexVector(Double[] values) {
        this.values = new ArrayList<>();

        for (int i = 0; i < values.length; i++) {
            this.values.add(new Complex(values[i]));
        }
    }

    public ComplexVector(Complex[] values) {
        this.values = new ArrayList<>();

        for (int i = 0; i < values.length; i++) {
            this.values.add(values[i]);
        }
    }

    public Double getReal(int i) {
        return values.get(i).getReal();
    }

    public Double getImaginary(int i) {
        return values.get(i).getImaginary();
    }

    public Complex get(int i) {
        return values.get(i);
    }

    public void add(Double num) {
        values.add(new Complex(num));
    }

    public void add(Double rnum, Double inum) {
        values.add(new Complex(rnum, inum));
    }

    public void add(Complex num) {
        values.add(num);
    }

    public ComplexVector add(ComplexVector vector2) {
        ComplexVector result = new ComplexVector();

        for (int i = 0; i < values.size(); i++) {
            result.add(values.get(i).add(vector2.get(i)));
        }

        return result;
    }

    public ComplexVector addConstant(Complex c) {
        ComplexVector result = new ComplexVector();

        for (int i = 0; i < values.size(); i++) {
            result.add(values.get(i).add(c));
        }

        return result;
    }

    public ComplexVector addConstant(Double c) {
        return addConstant(new Complex(c));
    }

    public ComplexVector multiplyElements(ComplexVector vector2) {
        ComplexVector result = new ComplexVector();

        for (int i = 0; i < values.size(); i++) {
            result.add(values.get(i).multiply(vector2.get(i)));
        }

        return result;
    }

    public ComplexVector divideElements(ComplexVector vector2) {
        ComplexVector result = new ComplexVector();

        for (int i = 0; i < values.size(); i++) {
            result.add(values.get(i).divide(vector2.get(i)));
        }

        return result;
    }

    public ComplexVector divideByConstant(Complex c) {
        ComplexVector result = new ComplexVector();

        for (int i = 0; i < values.size(); i++) {
            result.add(values.get(i).divide(c));
        }

        return result;
    }

    public ComplexVector multiplyByConstant(Complex c) {
        ComplexVector result = new ComplexVector();

        for (int i = 0; i < values.size(); i++) {
            result.add(values.get(i).multiply(c));
        }

        return result;
    }

    public ComplexVector multiplyByConstant(Double c) {
        return multiplyByConstant(new Complex(c));
    }

    public ComplexVector divideByConstant(Double c) {
        return divideByConstant(new Complex(c));
    }

    public ComplexVector negate() {
        ComplexVector result = new ComplexVector();

        for (int i = 0; i < values.size(); i++) {
            result.add(values.get(i).negate());
        }

        return result;
    }

    public ComplexVector subtract(ComplexVector vector2) {
        ComplexVector result = new ComplexVector();

        for (int i = 0; i < values.size(); i++) {
            result.add(values.get(i).subtract(vector2.get(i)));
        }

        return result;
    }

    public ComplexVector squareElements() {
        ComplexVector result = new ComplexVector();

        for (int i = 0; i < values.size(); i++) {
            result.add(values.get(i).multiply(values.get(i)));
        }

        return result;
    }

    public ComplexVector exp() {
        ComplexVector result = new ComplexVector();

        for (int i = 0; i < values.size(); i++) {
            result.add(values.get(i).exp());
        }

        return result;
    }

    public Complex[] toArray() {
        return values.toArray(new Complex[0]);
    }

    public List<Complex> toList() {
        return new ArrayList<>(values);
    }

    public int size() {
        return values.size();
    }

    public ComplexVector slice(int l, int r) {
        ComplexVector result = new ComplexVector();

        for (int i = l; i < r; i++) {
            result.add(values.get(i));
        }

        return result;
    }

    public Complex sum() {
        Complex sum = new Complex(0.0);

        for (int i = 0; i < values.size(); i++) {
            sum = sum.add(values.get(i));
        }

        return sum;
    }

    public Complex mean() {
        return sum().divide(new Complex((double)(size())));
    }

    public Complex rms() {
        Complex avgSquareSum = new Complex(0.0);

        for (int i = 0; i < values.size(); i++) {
            avgSquareSum.add(values.get(i).multiply(values.get(i)).divide(1.0 / (double)(size())));
        }

        return avgSquareSum.sqrt();
    }

    public ComplexVector absElements() {
        ComplexVector result = new ComplexVector();

        for (int i = 0; i < values.size(); i++) {
            result.add(values.get(i).abs());
        }

        return result;
    }
}

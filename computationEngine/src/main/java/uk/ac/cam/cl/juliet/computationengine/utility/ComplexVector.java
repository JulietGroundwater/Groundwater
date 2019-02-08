package uk.ac.cam.cl.juliet.computationengine.utility;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.complex.Complex;

// TODO : Unit testing of everything

/**
 * A class representing a complex vector. The elements of the vector are of type {@link Complex}.
 * The class can be used to represent a real vector by ignoring the imaginary parts of the elements.
 */
public class ComplexVector {
    private List<Complex> values;

    /** Creates an empty {@link ComplexVector}. */
    public ComplexVector() {
        values = new ArrayList<>();
    }

    /**
     * Creates a {@link ComplexVector} containing the given double values.
     *
     * @param values A {@link List} of {@link Double} values
     */
    public ComplexVector(List<Double> values) {
        this.values = new ArrayList<>();

        for (int i = 0; i < values.size(); i++) {
            this.values.add(new Complex(values.get(i)));
        }
    }

    /**
     * Creates a {@link ComplexVector} containing the given double values.
     *
     * @param values A primitive array of {@link Double} values
     */
    public ComplexVector(Double[] values) {
        this.values = new ArrayList<>();

        for (int i = 0; i < values.length; i++) {
            this.values.add(new Complex(values[i]));
        }
    }

    /**
     * Creates a {@link ComplexVector} containing the given complex values.
     *
     * @param values A primitive array of {@link Complex} values
     */
    public ComplexVector(Complex[] values) {
        this.values = new ArrayList<>();

        for (int i = 0; i < values.length; i++) {
            this.values.add(values[i]);
        }
    }

    /**
     * Returns the real part of the value on position {@code i} in the vector.
     *
     * @param i The index of the value requested
     * @return The real part of the value requested as a {@link Double}
     */
    public Double getReal(int i) {
        return values.get(i).getReal();
    }

    /**
     * Returns the imaginary part of the value on position {@code i} in the vector.
     *
     * @param i The index of the value requested
     * @return The imaginary part of the value requested as a {@link Double}
     */
    public Double getImaginary(int i) {
        return values.get(i).getImaginary();
    }

    /**
     * Returns the value on position {@code i} in the vector.
     *
     * @param i The index of the value requested
     * @return The value requested as a {@link Complex}
     */
    public Complex get(int i) {
        return values.get(i);
    }

    /**
     * Appends a number to the end of the vector.
     *
     * @param num A {@link Double} number to be appended
     */
    public void add(Double num) {
        values.add(new Complex(num));
    }

    /**
     * Appends a number to the end of the vector.
     *
     * @param num A {@link Complex} number to be appended
     */
    public void add(Complex num) {
        values.add(num);
    }

    /**
     * Creates a new vector that is the equal to the sum of {@code this} and {@code vector2}.
     *
     * @param vector2 A {@link ComplexVector} vector to be added to the current vector
     * @return The new resulting {@link ComplexVector}
     */
    public ComplexVector addElements(ComplexVector vector2) {
        ComplexVector result = new ComplexVector();

        for (int i = 0; i < values.size(); i++) {
            result.add(values.get(i).add(vector2.get(i)));
        }

        return result;
    }

    /**
     * Creates a new vector that is equal to {@code this} with each value summed with {@code c}.
     *
     * @param c A {@link Complex} constant to sum each element with
     * @return The new resulting {@link ComplexVector}
     */
    public ComplexVector addConstant(Complex c) {
        ComplexVector result = new ComplexVector();

        for (int i = 0; i < values.size(); i++) {
            result.add(values.get(i).add(c));
        }

        return result;
    }

    /**
     * Creates a new vector that is equal to {@code this} with each value summed with {@code c}.
     *
     * @param c A {@link Double} constant to sum each element with
     * @return The new resulting {@link ComplexVector}
     */
    public ComplexVector addConstant(Double c) {
        return addConstant(new Complex(c));
    }

    /**
     * Creates a new vector that is the equal to {@code vector2} subtracted of {@code this}.
     *
     * @param vector2 A {@link ComplexVector} vector to be subtracted by {@code this}
     * @return The new resulting {@link ComplexVector}
     */
    public ComplexVector subtractElements(ComplexVector vector2) {
        ComplexVector result = new ComplexVector();

        for (int i = 0; i < values.size(); i++) {
            result.add(values.get(i).subtract(vector2.get(i)));
        }

        return result;
    }

    /**
     * Creates a new vector that is the equal to the element-wise multiplication of {@code this} and
     * {@code vector2}.
     *
     * @param vector2 A {@link ComplexVector} vector to be element-wise multiplied with {@code this}
     * @return The new resulting {@link ComplexVector}
     */
    public ComplexVector multiplyElements(ComplexVector vector2) {
        ComplexVector result = new ComplexVector();

        for (int i = 0; i < values.size(); i++) {
            result.add(values.get(i).multiply(vector2.get(i)));
        }

        return result;
    }

    /**
     * Creates a new vector that is equal to {@code this} with each value multiplied {@code c}.
     *
     * @param c A {@link Complex} constant to multiply each element by
     * @return The new resulting {@link ComplexVector}
     */
    public ComplexVector multiplyByConstant(Complex c) {
        ComplexVector result = new ComplexVector();

        for (int i = 0; i < values.size(); i++) {
            result.add(values.get(i).multiply(c));
        }

        return result;
    }

    /**
     * Creates a new vector that is equal to {@code this} with each value multiplied {@code c}.
     *
     * @param c A {@link Complex} constant to multiply each element by
     * @return The new resulting {@link ComplexVector}
     */
    public ComplexVector multiplyByConstant(Double c) {
        return multiplyByConstant(new Complex(c));
    }

    /**
     * Creates a new vector that is the equal to the element-wise division of {@code this} and
     * {@code vector2}.
     *
     * @param vector2 A {@link ComplexVector} vector to element-wise divide {@code this}
     * @return The new resulting {@link ComplexVector}
     */
    public ComplexVector divideElements(ComplexVector vector2) {
        ComplexVector result = new ComplexVector();

        for (int i = 0; i < values.size(); i++) {
            result.add(values.get(i).divide(vector2.get(i)));
        }

        return result;
    }

    /**
     * Creates a new vector that is equal to {@code this} with each value divided by a constant.
     *
     * @param c A {@link Complex} constant to divide each element by
     * @return The new resulting {@link ComplexVector}
     */
    public ComplexVector divideByConstant(Complex c) {
        ComplexVector result = new ComplexVector();

        for (int i = 0; i < values.size(); i++) {
            result.add(values.get(i).divide(c));
        }

        return result;
    }

    /**
     * Creates a new vector that is equal to {@code this} with each value divided by a constant.
     *
     * @param c A {@link Double} constant to divide each element by
     * @return The new resulting {@link ComplexVector}
     */
    public ComplexVector divideByConstant(Double c) {
        return divideByConstant(new Complex(c));
    }

    /**
     * Creates a new vector that is equal to {@code this} with each value negated.
     *
     * @return The new resulting {@link ComplexVector}
     */
    public ComplexVector negate() {
        ComplexVector result = new ComplexVector();

        for (int i = 0; i < values.size(); i++) {
            result.add(values.get(i).negate());
        }

        return result;
    }

    /**
     * Creates a new vector that is equal to {@code this} with each value squared.
     *
     * @return The new resulting {@link ComplexVector}
     */
    public ComplexVector squareElements() {
        ComplexVector result = new ComplexVector();

        for (int i = 0; i < values.size(); i++) {
            result.add(values.get(i).multiply(values.get(i)));
        }

        return result;
    }

    /**
     * Creates a new vector that is equal to {@code this} with each value replaced with its
     * exponential.
     *
     * @return The new resulting {@link ComplexVector}
     */
    public ComplexVector expElements() {
        ComplexVector result = new ComplexVector();

        for (int i = 0; i < values.size(); i++) {
            result.add(values.get(i).exp());
        }

        return result;
    }

    /**
     * Creates a new vector that is equal to {@code this} with each value replaced with its absolute
     * value. Since the elements of the vector are of type {@link Complex} the absolute value
     * represents the complex magnitude.
     *
     * @return The new resulting {@link ComplexVector}
     */
    public ComplexVector absElements() {
        ComplexVector result = new ComplexVector();

        for (int i = 0; i < values.size(); i++) {
            result.add(values.get(i).abs());
        }

        return result;
    }

    /**
     * Creates a new {@link ComplexVector} containing the slice of values of {@code this} between
     * indices {@code l} and {@code r}.
     *
     * @param l The left end of the slice (inclusive)
     * @param r The right end of the slice (exclusive)
     * @return The new resulting {@link ComplexVector}
     */
    public ComplexVector slice(int l, int r) {
        ComplexVector result = new ComplexVector();

        for (int i = l; i < r; i++) {
            result.add(values.get(i));
        }

        return result;
    }

    /**
     * @return A primitive array of type {@link Complex} containing all values of {@code this}. The
     *     primitive array is a copy.
     */
    public Complex[] toArray() {
        return values.toArray(new Complex[0]);
    }

    /**
     * @return A {@link List} of type {@link Complex} containing all values of {@code this}. The
     *     list is a copy.
     */
    public List<Complex> toList() {
        return new ArrayList<>(values);
    }

    /** @return The size of the vector. */
    public int size() {
        return values.size();
    }

    /** @return The sum of all values in the vector. */
    public Complex sum() {
        Complex sum = new Complex(0.0);

        for (int i = 0; i < values.size(); i++) {
            sum = sum.add(values.get(i));
        }

        return sum;
    }

    /** @return The mean of all values in the vector. */
    public Complex mean() {
        return sum().divide(new Complex((double) (size())));
    }

    /** @return The root-mean-square level of the values in the vector. */
    public Complex rms() {
        Complex avgSquareSum = new Complex(0.0);

        for (int i = 0; i < values.size(); i++) {
            avgSquareSum =
                    avgSquareSum.add(
                            values.get(i).multiply(values.get(i)).divide((double) (size())));
        }

        return avgSquareSum.sqrt();
    }
}

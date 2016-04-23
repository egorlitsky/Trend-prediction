package ru.suai.generators;

import java.util.Random;

/**
 * It is a class for generation a different data
 */
public class ArtificialGenerator implements Generator {
    /**
     * Random generator for value deviation
     */
    private Random randomGenerator;

    /**
     * Parameter for randomGenerator
     */
    private int randomness;

    /**
     * Coefficient for 'a' for functions
     */
    private double a;

    /**
     * Coefficient for 'b' for functions
     */
    private double b;

    /**
     * Constructor of the class.
     * @param a coefficient 'a' for generating function
     * @param b coefficient 'b' for generating function
     * @param randomness maximum value of the randomness
     */
    public ArtificialGenerator(double a, double b, int randomness) {
        this.randomGenerator = new Random();
        this.randomness = randomness;
        this.a = a;
        this.b = b;
    }

    /**
     * Sets new value of the 'a' coefficient
     * @param a coefficient 'a' for generating function
     */
    public void setA(double a) {
        this.a = a;
    }

    /**
     * Sets new value of the 'b' coefficient
     * @param b coefficient 'b' for generating function
     */
    public void setB(double b) {
        this.b = b;
    }

    /**
     * Returns the new value of the linear function
     * @param x input value
     * @return new value of the linear function
     */
    @Override
    public Object getLinearValue(double x) {
        double minRandomValue =  - this.randomness / 2,
                maxRandomValue = this.randomness / 2,
                randomValue = minRandomValue + (maxRandomValue - minRandomValue) * this.randomGenerator.nextDouble();

        return (this.a * x + this.b + randomValue);
    }

    /**
     * Returns the new value of the degree function
     * @param x input value
     * @return new value of the degree function
     */
    @Override
    public Object getDegreeValue(double x) {
        double minRandomValue =  - this.randomness / 2,
                maxRandomValue = this.randomness / 2,
                randomValue = minRandomValue + (maxRandomValue - minRandomValue) * this.randomGenerator.nextDouble();

        return (this.a * Math.pow(x, this.b) + randomValue);
    }

    /**
     * Returns the new value of the exponential function
     * @param x input value
     * @return new value of the exponential function
     */
    @Override
    public Object getExponentialValue(double x) {
        double minRandomValue =  - this.randomness / 2,
                maxRandomValue = this.randomness / 2,
                randomValue = minRandomValue + (maxRandomValue - minRandomValue) * this.randomGenerator.nextDouble();

        return (this.a * Math.pow(this.b, x) + randomValue);
    }
}

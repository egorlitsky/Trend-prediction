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

    @Override
    public Object getLinearValue(double x) {
        double minRandomValue =  - this.randomness / 2,
                maxRandomValue = this.randomness / 2,
                randomValue = minRandomValue + (maxRandomValue - minRandomValue) * this.randomGenerator.nextDouble();

        return (this.a * x + this.b + randomValue);
    }

    @Override
    public Object getDegreeValue(double x) {
        double minRandomValue =  - this.randomness / 2,
                maxRandomValue = this.randomness / 2,
                randomValue = minRandomValue + (maxRandomValue - minRandomValue) * this.randomGenerator.nextDouble();

        return (this.a * Math.pow(x, this.b) + randomValue);
    }

    @Override
    public Object getExponentialValue(double x) {
        double minRandomValue =  - this.randomness / 2,
                maxRandomValue = this.randomness / 2,
                randomValue = minRandomValue + (maxRandomValue - minRandomValue) * this.randomGenerator.nextDouble();

        return (this.a * Math.pow(this.b, x) + randomValue);
    }
}

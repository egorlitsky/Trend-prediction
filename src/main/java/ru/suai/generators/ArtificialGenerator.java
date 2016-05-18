package ru.suai.generators;

import ru.suai.computing.Predictor;

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
     * Type of generated function
     */
    private String functionType;

    /**
     * Constructor of the class.
     * @param a coefficient 'a' for generating function
     * @param b coefficient 'b' for generating function
     * @param randomness maximum value of the randomness
     */
    public ArtificialGenerator(double a, double b, int randomness, String initialType) {
        this.randomGenerator = new Random();
        this.randomness = randomness;

        this.functionType = initialType;
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
     * Sets the type of function which generates the values.
     * @param functionType function type
     */
    public void setFunctionType(String functionType) {
        this.functionType = functionType;
    }

    /**
     * Returns the new value of the linear function
     * @param x input value
     * @return new value of the linear function
     */
    private double getLinearValue(double x) {
        return (this.a * x + this.b);
    }

    /**
     * Returns the new value of the degree function
     * @param x input value
     * @return new value of the degree function
     */
    private double getDegreeValue(double x) {
        return (this.a * Math.pow(x, this.b));
    }

    /**
     * Returns the new value of the exponential function
     * @param x input value
     * @return new value of the exponential function
     */
    private double getExponentialValue(double x) {
        return (this.a * Math.pow(this.b, x));
    }

    public Object getValue(double x) {
        double generatedValue,
                minRandomValue =  - this.randomness / 2,
                maxRandomValue = this.randomness / 2,
                randomValue = minRandomValue + (maxRandomValue - minRandomValue) * this.randomGenerator.nextDouble();

        switch (this.functionType) {
            case Predictor.LINEAR_FUNCTION_TYPE:
                generatedValue = this.getLinearValue(x) + randomValue;
                break;
            case Predictor.DEGREE_FUNCTION_TYPE:
                generatedValue = this.getDegreeValue(x) + randomValue;
                break;
            case Predictor.EXPONENTIAL_FUNCTION_TYPE:
                generatedValue = this.getExponentialValue(x) + randomValue;
                break;
            default:
                generatedValue = 0;
                break;
        }

        return generatedValue;
    }
}

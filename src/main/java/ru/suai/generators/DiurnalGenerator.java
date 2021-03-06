package ru.suai.generators;

import java.util.HashMap;
import java.util.Random;

/**
 * Class implements the method for generating data with Poisson or
 * constant distribution. Generated data has a linear, degree or exponential
 * shape.
 */
public class DiurnalGenerator implements Generator {
    /**
     * Poisson distribution for stochastic character of the data.
     */
    public static final String POISSON_DISTRIBUTION_TYPE = "POISSON";

    /**
     * Constant distribution w/o stochastic character of the data.
     */
    public static final String CONSTANT_DISTRIBUTION_TYPE = "CONSTANT";

    /**
     * Enumeration of 'diurnal' generated data modulation.
     */
    public static enum modulation {AMPLITUDE, PERIOD, PHASE}

    /**
     * Enumeration of 'diurnal' generated data distribution.
     */
    public static enum distribution {DISTRIBUTION_TYPE, SHAPE_TYPE, COEFFICIENT_A, COEFFICIENT_B}

    /**
     * Amplitude of the 'diurnal' statistic.
     */
    private double amplitude;

    /**
     * Period of the 'diurnal' statistic.
     */
    private double period;

    /**
     * Phase of the 'diurnal' statistic.
     */
    private double phase;

    /**
     * Type of data distribution.
     */
    private String distributionType;

    /**
     * Type of data shape.
     */
    private String shapeType;

    /**
     * Random generator for stochastic character of data.
     */
    private Random randomGenerator;

    /**
     * Mean value for Poisson generation.
     */
    private double mean;

    /**
     * Counter for define the period.
     */
    private int generatedNumbersCount;

    /**
     * Number generated by ArtificialGenerator.
     */
    private double baseNumber;

    /**
     * Argument for growing by ArtificialGenerator.
     */
    private int growArgument;

    /**
     * It is generator to give a linear, degree or exponential character
     * to data.
     */
    private ArtificialGenerator artificialGenerator;

    /**
     * Constructor of class.
     * @param modulation HashMap with parameters for modulation data
     * @param distribution HashMap with parameters for distribution data
     * @param mean mean for Poisson thread
     */
    public DiurnalGenerator(HashMap<modulation, Double> modulation, HashMap<distribution, String> distribution, double mean) {
        this.amplitude = modulation.get(DiurnalGenerator.modulation.AMPLITUDE);
        this.period = modulation.get(DiurnalGenerator.modulation.PERIOD);
        this.phase = modulation.get(DiurnalGenerator.modulation.PHASE);

        this.distributionType = distribution.get(DiurnalGenerator.distribution.DISTRIBUTION_TYPE);
        this.shapeType = distribution.get(DiurnalGenerator.distribution.SHAPE_TYPE);

        double a = Double.valueOf(distribution.get(DiurnalGenerator.distribution.COEFFICIENT_A));
        double b = Double.valueOf(distribution.get(DiurnalGenerator.distribution.COEFFICIENT_B));

        this.artificialGenerator = new ArtificialGenerator(a, b, 0, this.shapeType); // '0' because less randomness
        this.randomGenerator = new Random();

        this.mean = mean;

        this.generatedNumbersCount = 0;
        this.growArgument = 1;
        this.baseNumber = 0;
    }

    /**
     * Returns the new value generated by 'diurnal' function
     * @param x input value
     * @return new value generated by 'diurnal' function
     */
    public double getValue(double x) {
        double stochasticNumber = 0;

        // check of the distribution
        switch (this.distributionType) {
            case POISSON_DISTRIBUTION_TYPE:
                stochasticNumber = this.getPoissonNumber();
                break;
            case CONSTANT_DISTRIBUTION_TYPE:
                this.artificialGenerator.setA(0);
                break;
        }

        if (this.generatedNumbersCount % this.period == 0) {
            this.baseNumber = this.artificialGenerator.getValue(this.growArgument);

            ++this.growArgument;
        }

        ++this.generatedNumbersCount;

        // A * sin((2 * Pi) / T) * x + Fi)
        return (this.baseNumber + stochasticNumber + (this.amplitude * Math.sin((2 * Math.PI / (this.period)) * x + this.phase)));
    }

    /**
     * Returns the random number, generated by Poisson
     * distribution with mean from this.mean field of class.
     *
     * @return the random number, generated by Poisson distribution.
     */
    private int getPoissonNumber() {
        int r = 0;
        double a = this.randomGenerator.nextDouble(),
                p = Math.exp(-this.mean);

        while (a > p) {
            ++r;
            a -= p;
            p *= this.mean / r;
        }
        return r;
    }
}

package main.java.ru.suai.computing;

import java.util.ArrayList;

public class Predictor {
    /**
     * Degree of the square
     */
    public static final int SQUARE_DEGREE = 2;

    /**
     * Window of the statistic
     */
    private int w;

    /**
     * The first sum of the 'a'-coefficient formula
     */
    private double sumOfDataAndTimeProd;

    /**
     * The second sum of the 'a'-coefficient formula
     */
    private double sumOfTime;

    /**
     * The third sum of the 'a'-coefficient formula
     */
    private double sumOfData;

    /**
     * The forth sum of the 'a'-coefficient formula
     */
    private double sumOfTimeSquare;

    /**
     * 'a' coefficient
     */
    private double a;

    /**
     * 'b' coefficient
     */
    private double b;

    /**
     * ArrayList with input workload statistic.
     */
    private ArrayList<Double> y;

    public Predictor(int w) {
        this.w = w;

        this.sumOfDataAndTimeProd = 0;
        this.sumOfTime = 0;
        this.sumOfData = 0;
        this.sumOfTimeSquare = 0;
    }

    /**
     * Getter a coefficient
     *
     * @return a
     */
    public double getA() {
        return a;
    }

    /**
     * Getter b coefficient
     *
     * @return b
     */
    public double getB() {
        return b;
    }

    /**
     * Computes the second coefficient for prediction.
     */
    private void computeCoefficientA() {
        int beginIndex = this.y.size() - this.w;    // index of the statistic begin

        if (beginIndex < 0)
            beginIndex = 0;

        for (int x = beginIndex; x < this.y.size(); ++x) {
            this.sumOfDataAndTimeProd += (x + 1) * this.y.get(x);   // (x + 1) because x may be zero
            this.sumOfTime += x + 1;
            this.sumOfData += this.y.get(x);
            this.sumOfTimeSquare += Math.pow(x + 1, SQUARE_DEGREE);
        }

        this.a = (sumOfDataAndTimeProd - (sumOfTime * sumOfData) / this.w)
                / (sumOfTimeSquare - Math.pow(sumOfTime, SQUARE_DEGREE) / this.w);
    }

    /**
     * Computes the second coefficient for prediction.
     */
    private void computeCoefficientB() {
        this.b = (this.sumOfData - this.a * this.sumOfTime) / this.w;
    }

    /**
     * Returns the new predicted value based on  input statistics.
     *
     * @param y ArrayList with the input statistics
     * @return the new predicted value
     */
    public double getPredict(ArrayList<Double> y) {
        this.y = y;

        // computing coefficients
        computeCoefficientA();
        computeCoefficientB();

        double prediction = this.a * (y.size() + 1) + this.b;

        if (Double.isNaN(prediction))    // for some values of the window result is NaN
            return 0;
        else return prediction;
    }
}

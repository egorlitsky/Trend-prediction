package main.java.ru.suai.computing;

import java.util.ArrayDeque;
import java.util.ArrayList;

public class Predictor {
    /**
     * Degree of the square
     */
    public static final int SQUARE_DEGREE = 2;

    /**
     * ArrayList with input workload statistic.
     */
    private ArrayDeque<Double> y;

    /**
     * Data structure with future predictions.
     */
    private ArrayList<Double> futurePredictions;

    /**
     * Copy of the queue in array for access via indexes.
     */
    private Double[] yArray;

    /**
     * Window of the statistic
     */
    private int predictWindow;

    /**
     * 'a' coefficient for prediction
     */
    private double a;

    /**
     * 'b' coefficient for prediction
     */
    private double b;

    /**
     * Current time moment ('x' in algorithm)
     */
    private int timeCounter; // TODO: int?

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
     * Count of the elements for future predictions.
     */
    private int predictFutureTime;

    /**
     * The required maximum of the workload value.
     */
    private double qos;

    /**
     * Time moment value where the qos may be violated
     */
    private int qosViolatedTime;

    public Predictor(int smoothingWindow, int predictWindow, int predictFutureTime, double qos) {
        this.y = new ArrayDeque<Double>();
        this.predictWindow = predictWindow;
        this.timeCounter = smoothingWindow + 1; // TODO: CHECK THIS PARAMETER

        this.predictFutureTime = predictFutureTime;
        this.qos = qos;

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
     *
     * @return time moment value where the qos may be violated
     */
    public int getQosViolatedTime() {
        return qosViolatedTime;
    }

    /**
     * Converts queue with elements of window
     * into array for access via indexes.
     */
    private void convertQueueToArray() {
        this.yArray = new Double[this.y.size()];

        int count = 0;
        for (Double aY : this.y) {
            this.yArray[count++] = aY;
        }
    }

    /**
     * Adds the new value into queue with elements of the window.
     * @param newValue new value for prediction
     */
    public void addValue(double newValue) {
        this.y.addLast(newValue);

        if(this.y.size() > this.predictWindow)
            this.y.pollFirst();

        this.convertQueueToArray();
    }

    /**
     * Computes the second coefficient for prediction.
     */
    private void computeCoefficientA() {
        for (Double aYArray : this.yArray) {
            this.sumOfDataAndTimeProd += (timeCounter) * aYArray;   // (x + 1) because x may be zero
            this.sumOfTime += timeCounter;
            this.sumOfData += aYArray;
            this.sumOfTimeSquare += Math.pow(timeCounter, SQUARE_DEGREE);
        }

        this.a = (sumOfDataAndTimeProd - (sumOfTime * sumOfData) / this.predictWindow)
                / (sumOfTimeSquare - Math.pow(sumOfTime, SQUARE_DEGREE) / this.predictWindow);
    }

    /**
     * Computes the second coefficient for prediction.
     */
    private void computeCoefficientB() {
        this.b = (this.sumOfData - this.a * this.sumOfTime) / this.predictWindow;
    }

    /**
     * Returns the new predicted value based on
     * input statistics from queue.
     *
     * @return the new predicted value
     */
    public double getPredict() {
        // computing coefficients
        computeCoefficientA();
        computeCoefficientB();

        double prediction = this.a * (timeCounter) + this.b;

        ++timeCounter;

        if (Double.isNaN(prediction))   // may be div on '0'
            return 0;
        else return prediction;
    }

    /**
     * Computes the future predictions and fill futurePredictions
     * ArrayList with size of predictFutureTime.
     */
    public void computeFuturePredictions() {
        this.futurePredictions = new ArrayList<Double>();

        for (int i = 0; i < this.predictFutureTime; i++) {
            this.futurePredictions.add(this.a * (timeCounter + i) + this.b);
        }
    }

    /**
     * Return true if qos was violated or false if else and computes the qosViolatedTime
     * @return true if qos was violated or false if else
     */
    public boolean isQosViolated() {
        for (int i = 0; i < this.futurePredictions.size(); ++i) {
            if(this.futurePredictions.get(i) > this.qos) {
                this.qosViolatedTime = this.timeCounter + this.predictWindow + i;
                return true;
            }
        }

        return false;
    }
}

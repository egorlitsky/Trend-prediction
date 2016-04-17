package ru.suai.computing;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.ArrayList;

/**
 * Class implements the methods for trend predictions
 */
public class Predictor {
    /**
     * Linear function type of the form (y = a * x + b).
     */
    public static final String LINEAR_FUNCTION_TYPE = "LINEAR";

    /**
     * Degree function type of the form (y = a * x ^ b).
     */
    public static final String DEGREE_FUNCTION_TYPE = "DEGREE";

    /**
     * Exponential function type of the form (y = a * b ^ x).
     */
    public static final String EXPONENTIAL_FUNCTION_TYPE = "EXPONENTIAL";
    /**
     * Degree of the square
     */
    public static final int SQUARE_DEGREE = 2;

    /**
     * Offset from the last element of the window for future predictions.
     */
    public static final int FUTURE_PREDICT_OFFSET = 2;

    /**
     * Offset from the last element of the window for single prediction.
     */
    public static final int FIRST_PREDICT_OFFSET = 1;

    /**
     * A number of the digits after after dot in coefficients computing.
     */
    public static final int COEFFICIENTS_ACCURACY = 10;

    /**
     * Offset from the last element of the window for future predictions.
     */
    public static final int SECOND_PREDICTION_OFFSET = 2;

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
     * Current time moment ('x' in algorithm)
     */
    private int timeCounter;

    /**
     * Count of the elements for future predictions.
     */
    private int predictFutureTime;

    /**
     * The required maximum of the workload value.
     */
    private double qos;

    /**
     * Time moment value where the qos may be violated.
     */
    private int qosViolatedTime;

    /**
     * Type of the function based on current statistic.
     */
    private String currentFunctionType;

    /**
     * 'a' coefficient for prediction by linear regression.
     * (y = a * x + b)
     */
    private double linearCoefficientA;

    /**
     * 'b' coefficient for prediction by linear regression
     * (y = a * x + b)
     */
    private double linearCoefficientB;

    /**
     * 'a' coefficient for prediction by degree regression
     * (y = a * x^b)
     */
    private double degreeCoefficientA;

    /**
     * 'b' coefficient for prediction by degree regression
     * (y = a * x^b)
     */
    private double degreeCoefficientB;

    /**
     * 'a' coefficient for prediction by degree regression
     * (y = a * b^x)
     */
    private double exponentialCoefficientA;

    /**
     * 'b' coefficient for prediction by degree regression
     * (y = a * b^x)
     */
    private double exponentialCoefficientB;

    /**
     * Constructor of the class.
     * @param smoothingWindow the number of items that are not included in the statistics
     * @param predictWindow the number of items for prediction
     * @param predictFutureTime the number of items for future predictions
     * @param qos Quality of Service (the maximum allowed value)
     */
    public Predictor(int smoothingWindow, int predictWindow, int predictFutureTime, double qos) {
        this.y = new ArrayDeque<>();
        this.predictWindow = predictWindow;
        this.timeCounter = smoothingWindow;

        this.predictFutureTime = predictFutureTime;
        this.qos = qos;
        this.currentFunctionType = "";
    }

    /**
     * Getter of 'a' coefficient of linear function
     * @return 'a' coefficient of linear function
     */
    public double getLinearCoefficientA() {
        return linearCoefficientA;
    }

    /**
     * Getter of 'b' coefficient of linear function
     * @return 'b' coefficient of linear function
     */
    public double getLinearCoefficientB() {
        return linearCoefficientB;
    }

    /**
     * Getter of 'a' coefficient of degree function
     * @return 'a' coefficient of degree function
     */
    public double getDegreeCoefficientA() {
        return degreeCoefficientA;
    }

    /**
     * Getter of 'b' coefficient of degree function
     * @return 'b' coefficient of degree function
     */
    public double getDegreeCoefficientB() {
        return degreeCoefficientB;
    }

    /**
     * Getter of 'a' coefficient of exponential function
     * @return 'a' coefficient of exponential function
     */
    public double getExponentialCoefficientA() {
        return exponentialCoefficientA;
    }

    /**
     * Getter of 'b' coefficient of exponential function
     * @return 'b' coefficient of exponential function
     */
    public double getExponentialCoefficientB() {
        return exponentialCoefficientB;
    }

    /**
     * Returns the character of the current statistic.
     * @return the character of the current statistic.
     */
    public String getCurrentFunctionType() {
        return currentFunctionType;
    }

    /**
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
     *
     * @param newValue new value for prediction
     */
    public void addValue(double newValue) {
        this.y.addLast(newValue);

        if (this.y.size() > this.predictWindow)
            this.y.pollFirst();

        this.convertQueueToArray();
        ++timeCounter;
    }

    /**
     * Computes the coefficient 'a' and 'b' of linear regression.
     * Function of the form (y = a * x + b).
     */
    public void computeLinearCoefficients() {
        int i = 1;  // counter of time inside the window

        double sumOfDataAndTimeProd = 0,
                sumOfTime = 0,
                sumOfTimeSquare = 0,
                sumOfData = 0;

        // calculation of intermediate values
        for (Double aYArray : this.yArray) {
            sumOfDataAndTimeProd += i * aYArray;   // (x + 1) because x may be zero
            sumOfTime += i;
            sumOfTimeSquare += Math.pow(i, SQUARE_DEGREE);
            sumOfData += aYArray;

            ++i;
        }

        this.linearCoefficientA = getRoundedDouble((sumOfDataAndTimeProd - (sumOfTime * sumOfData) / this.predictWindow)
                / (sumOfTimeSquare - Math.pow(sumOfTime, SQUARE_DEGREE) / this.predictWindow));

        this.linearCoefficientB = getRoundedDouble((sumOfData - this.linearCoefficientA * sumOfTime) / this.predictWindow);
    }

    /**
     * Returns the prediction by linear regression.
     *
     * @param predictionOffset offset from the last element in statistic.
     * @return the prediction by linear regression.
     */
    private double getLinearPrediction(int predictionOffset) {
        return this.linearCoefficientA * (this.predictWindow + predictionOffset) + this.linearCoefficientB;
    }

    /**
     * Computes the coefficient 'a' and 'b' of degree regression.
     * Function of the form (y = a * x ^ b).
     */
    public void computeDegreeCoefficients() {
        int i = 1;  // counter of time inside the window

        double sumOfDataAndTimeProd = 0,
                sumOfTime = 0,
                sumOfTimeSquare = 0,
                sumOfData = 0;

        // calculation of intermediate values
        for (Double aYArray : this.yArray) {
            sumOfDataAndTimeProd += Math.log(i) * Math.log(aYArray);   // (x + 1) because x may be zero
            sumOfTime += Math.log(i);
            sumOfTimeSquare += Math.pow(Math.log(i), SQUARE_DEGREE);
            sumOfData += Math.log(aYArray);

            ++i;
        }

        this.degreeCoefficientB = getRoundedDouble((sumOfDataAndTimeProd - (sumOfTime * sumOfData) / this.predictWindow)
                / (sumOfTimeSquare - Math.pow(sumOfTime, SQUARE_DEGREE) / this.predictWindow));

        this.degreeCoefficientA = getRoundedDouble(Math.exp((sumOfData - this.degreeCoefficientB * sumOfTime) / this.predictWindow));
    }

    /**
     * Returns the prediction by degree regression.
     *
     * @param predictionOffset offset from the last element in statistic.
     * @return the prediction by degree regression.
     */
    private double getDegreePrediction(int predictionOffset) {
        return this.degreeCoefficientA * Math.pow((this.predictWindow + predictionOffset), this.degreeCoefficientB);
    }

    /**
     * Computes the coefficient 'a' and 'b' of exponential regression.
     * Function of the form (y = a * b ^ x).
     */
    public void computeExponentialCoefficients() {
        int i = 1;  // counter of time inside the window

        double sumOfDataAndTimeProd = 0,
                sumOfTime = 0,
                sumOfTimeSquare = 0,
                sumOfData = 0;

        // calculation of intermediate values
        for (Double aYArray : this.yArray) {
            sumOfDataAndTimeProd += i * Math.log(aYArray);   // (x + 1) because x may be zero
            sumOfTime += i;
            sumOfTimeSquare += Math.pow(i, SQUARE_DEGREE);
            sumOfData += Math.log(aYArray);

            ++i;
        }

        this.exponentialCoefficientB = getRoundedDouble(Math.exp((sumOfDataAndTimeProd - ((sumOfTime * sumOfData) / this.predictWindow))
                / (sumOfTimeSquare - (Math.pow(sumOfTime, SQUARE_DEGREE) / this.predictWindow))));

        this.exponentialCoefficientA = getRoundedDouble(Math.exp((sumOfData - (Math.log(this.exponentialCoefficientB) * sumOfTime))
                / this.predictWindow));
    }

    /**
     * Returns the prediction by exponential regression.
     *
     * @param predictionOffset offset from the last element in statistic.
     * @return the prediction by exponential regression.
     */
    private double getExponentialPrediction(int predictionOffset) {
        return this.exponentialCoefficientA * Math.pow(this.exponentialCoefficientB, (this.predictWindow + predictionOffset));
    }

    /**
     * It defines the character of current statistics.
     * Computes mean square deviation and returns function type with
     * minimal deviation.
     *
     * @return String with function type.
     */
    public String determineFunctionType() {
        double linearError = 0,
                degreeError = 0,
                exponentialError = 0;

        int i = 1;

        // computing of the all coefficients
        computeLinearCoefficients();
        computeDegreeCoefficients();
        computeExponentialCoefficients();

        // computing th deviations
        for (Double aYArray : this.yArray) {
            linearError += Math.pow(aYArray -
                    (this.linearCoefficientA * i + this.linearCoefficientB), SQUARE_DEGREE);

            degreeError += Math.pow(aYArray -
                    (this.degreeCoefficientA * Math.pow(i, this.degreeCoefficientB)), SQUARE_DEGREE);

            exponentialError += Math.pow(aYArray -
                    (this.exponentialCoefficientA * Math.pow(this.exponentialCoefficientB, i)), SQUARE_DEGREE);

            ++i;
        }

        // deviations compare
        return (linearError < degreeError) ? ((linearError < exponentialError) ?
                LINEAR_FUNCTION_TYPE : EXPONENTIAL_FUNCTION_TYPE) : ((degreeError < exponentialError) ?
                DEGREE_FUNCTION_TYPE : EXPONENTIAL_FUNCTION_TYPE);
    }

    /**
     * Returns the new predicted value based on
     * input statistics from queue in dependency of funtion type.
     *
     * @return the new predicted value
     */
    public double getPredict() {
        double prediction = 0;

        switch (determineFunctionType()) {
            case LINEAR_FUNCTION_TYPE:
                this.currentFunctionType = LINEAR_FUNCTION_TYPE;
                prediction = getLinearPrediction(FIRST_PREDICT_OFFSET);
                break;
            case DEGREE_FUNCTION_TYPE:
                this.currentFunctionType = DEGREE_FUNCTION_TYPE;
                prediction = getDegreePrediction(FIRST_PREDICT_OFFSET);
                break;
            case EXPONENTIAL_FUNCTION_TYPE:
                this.currentFunctionType = EXPONENTIAL_FUNCTION_TYPE;
                prediction = getExponentialPrediction(FIRST_PREDICT_OFFSET);
                break;
        }

        if (Double.isNaN(prediction)) // may be div on '0'
            return 0;
        else return prediction;
    }

    /**
     * Computes the future predictions and fill futurePredictions
     * ArrayList with size of predictFutureTime.
     */
    public void computeFuturePredictions() {
        this.futurePredictions = new ArrayList<>();

        for (int i = SECOND_PREDICTION_OFFSET; i < this.predictFutureTime + FUTURE_PREDICT_OFFSET; i++) {
            switch (this.currentFunctionType) {
                case LINEAR_FUNCTION_TYPE:
                    this.futurePredictions.add(getLinearPrediction(i));
                    break;
                case DEGREE_FUNCTION_TYPE:
                    this.futurePredictions.add(getDegreePrediction(i));
                    break;
                case EXPONENTIAL_FUNCTION_TYPE:
                    this.futurePredictions.add(getExponentialPrediction(i));
            }
        }

        isQosViolated();
    }

    /**
     * Check that QoS is violated.
     * Return true if qos was violated or false if else and computes the qosViolatedTime.
     *
     * @return true if qos was violated or false if else
     */
    public boolean isQosViolated() {
        for (int i = 0; i < this.futurePredictions.size(); ++i) {
            if (this.futurePredictions.get(i) > this.qos) {
                this.qosViolatedTime = this.timeCounter + FIRST_PREDICT_OFFSET + i;

                return true;
            }
        }

        return false;
    }

    /**
     * Rounding coefficients. For example, coefficient of the form
     * '4.000000000001' transforms to '4.0'.
     *
     * @param roundingNumber count of digits after '.' in double number
     * @return reduced number
     */
    private double getRoundedDouble(double roundingNumber) {
        BigDecimal bd = new BigDecimal(roundingNumber);

        return bd.setScale(COEFFICIENTS_ACCURACY, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}

package ru.suai.computing;

import java.util.ArrayDeque;
import java.util.Arrays;

/**
 * It is a class for data smoothing / filtering.
 */
public class DataSmoothing {

    /**
     * Queue for store the current smoothing window values.
     */
    private ArrayDeque<Double> y;

    /**
     * Copy of the queue for computing in Hybrid method of data smoothing.
     */
    private Double[] yArray;

    /**
     * The window of smoothing data.
     */
    private int w;

    /**
     * Given percentage of the maximum values of all window.
     * Parameter for Hybrid method.
     */
    private double p;

    /**
     * It is constructor of the class.
     *
     * @param w the window of data smoothing
     * @param p given percentage of the maximum values of all window
     */
    public DataSmoothing(int w, double p) {
        this.y = new ArrayDeque<Double>();
        this.w = w;
        this.p = p;
    }

    /**
     * Returns the given percentage of the maximum values of all window
     * @return p
     */
    public double getP() {
        return p;
    }

    /**
     * Sets the given percentage of the maximum values of all window
     * @param p proportion ov maximum values for averaging
     */
    public void setP(double p) {
        this.p = p;
    }

    /**
     * Returns current window value
     * @return w
     */
    public int getW() {
        return w;
    }

    /**
     * Sets the window for data smoothing
     * @param w value of the window for data smoothing
     */
    public void setW(int w) {
        this.w = w;
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
     * @param newValue new value for data smoothing
     */
    public void addValue(double newValue) {
        this.y.addLast(newValue);

        if(this.y.size() > this.w)
            this.y.pollFirst();

        this.convertQueueToArray();
    }

    /**
     * Returns the moving average value.
     *
     * @return moving average value for current window
     */
    public double getMovingAverageValue() {
        double sum = 0;

        for (Double yData : this.yArray) {
            sum += yData;
        }

        return sum / this.w;
    }

    /**
     * Returns the smoothed value for current window
     * by the Hybrid method.
     *
     * @return the smoothed value
     */
    public double getHybridSmoothValue() {
        int beginIndex = this.w - (int) (this.p * this.w),  // from algorithm
                sum = 0;

        // sorting
        Arrays.sort(this.yArray, 0, this.w - 1);

        for(int i = beginIndex; i < this.w; ++i) {
            sum += this.yArray[i];
        }

        return sum / (this.p * this.w);
    }
}

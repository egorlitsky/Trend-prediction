package main.java.ru.suai.computing;

import java.util.Arrays;

/**
 * It is a class for modeling and testing of the data smoothing.
 *
 */
public class DataSmoothing {
    /**
     * Exceptions messages
     */
    public static final String ERR_Y_ARRAY_IS_NOT_DEFINED = "ERR: Y array is not defined!";
    public static final String ERR_BEGIN_OR_END_INDEX_OF_SORTING_IS_INCORRECT = "ERR: Begin or end index of sorting is incorrect!!";

    /**
     * Input data array.
     */
    private double[] y;

    /**
     * The window of moving averaging.
     */
    private int w;

    /**
     * Proportion ov maximum values for averaging.
     */
    private double p;

    /**
     * It is constructor of the class.
     *
     * @param y Input data array
     * @param w The window of moving averaging
     * @param p Proportion ov maximum values for averaging
     */
    public DataSmoothing(double[] y, int w, double p) {
        this.y = y;
        this.w = w;
        this.p = p;
    }

    /**
     * Returns the proportion ov maximum values for averaging
     * @return p
     */
    public double getP() {
        return p;
    }

    /**
     * Sets the proportion ov maximum values for averaging
     * @param p proportion ov maximum values for averaging
     */
    public void setP(double p) {
        this.p = p;
    }

    /**
     * Returns the window of moving averaging
     * @return w
     */
    public int getW() {
        return w;
    }

    /**
     * Sets the window of moving averaging
     * @param w window of moving averaging
     */
    public void setW(int w) {
        this.w = w;
    }

    /**
     * Sorts input array from beginSortingIndex to endSortingIndex.
     * For hybrid smoothing of data
     *
     * @param beginSortingIndex the first index of the sorting
     * @param endSortingIndex the last index of the sorting
     *
     * @throws Exception
     */
    private void sortArray(int beginSortingIndex, int endSortingIndex) throws Exception {
        if(this.y == null)
            throw new Exception(ERR_Y_ARRAY_IS_NOT_DEFINED);

        if(beginSortingIndex < 0 || endSortingIndex >= this.y.length || beginSortingIndex >= endSortingIndex) {
            throw new Exception(ERR_BEGIN_OR_END_INDEX_OF_SORTING_IS_INCORRECT);
        }

        Arrays.sort(this.y, beginSortingIndex, endSortingIndex);
    }

    /**
     * Returns the moving average value.
     *
     * @param i index of the input value from input array
     * @return moving average value
     */
    public double getMovingAverageValue(int i) {
        double sum = 0;

        int beginIndex = i - this.w / 2,
                endIndex = i + this.w / 2;

        if(beginIndex < 0)
            beginIndex = 0;

        if(endIndex >= this.y.length)
            endIndex = this.y.length - 1;

        for (int j = beginIndex; j <= endIndex; j++) {
            sum += this.y[j];
        }

        return sum / this.w;
    }

    /**
     * Returns the data smooth value by Hybrid method.
     *
     * @param i index of the input value from input array
     * @return data smooth value
     */
    public double getHybridSmoothValue(int i) {

        // TODO: check w and p parameters, check sorting indexes

        int beginSortingIndex = i - this.w / 2,
                endSortingIndex = i + this.w / 2;

        double sum = 0;

        if(beginSortingIndex < 0)
            beginSortingIndex = 0;

        if(endSortingIndex >= this.y.length)
            endSortingIndex = this.y.length - 1;

        int j = (int) (this.w - this.p * this.w);

        try {
            this.sortArray(beginSortingIndex, endSortingIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for(int m = j; m <= w; ++m) {
            sum += this.y[m];
        }

        return sum / (this.p * this.w);
    }
}

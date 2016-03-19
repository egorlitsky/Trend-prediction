package main.java.ru.suai;

import main.java.ru.suai.computing.DataSmoothing;

import java.util.Random;

/**
 * It's the main class of the project.
 *
 */
public class Main {
    /**
     * Tests the classic method of the data smoothing.
     * Displays the random array and his smoothing result on the console.
     *
     */
    private static void testDataSmoothing() {
        Random r = new Random();

        int testArrayLength = 200,
            minRandomValue = 500,
            maxRandomValue = 800,
            w = 20; // the window of moving averaging

        double[] testArray = new double[testArrayLength];
        double p = 0.2; // proportion of maximum values for averaging

        for(int i = 0; i < testArray.length; ++i) {
            testArray[i] = minRandomValue + (maxRandomValue - minRandomValue) * r.nextDouble();
        }

        DataSmoothing ds = new DataSmoothing(testArray, w, p);

        System.out.println("Input data:\n");

        for (double aTestArray : testArray) {
            System.out.println(aTestArray);
        }

        System.out.println("\nSmooth data:\n");

        for (int i = 0; i < testArray.length; i++) {
            System.out.println(ds.getMovingAverageValue(i));
        }
    }

    public static void main(String[] args) {
        testDataSmoothing();
    }
}

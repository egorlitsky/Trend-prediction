package main.java.ru.suai;

import main.java.ru.suai.computing.DataSmoothing;
import main.java.ru.suai.computing.Predictor;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * It's the main class of the project.
 *
 */
public class Main {
    // String constants
    public static final String CSV_FILE_FORMAT = ".csv";
    public static final String CSV_DELIMITER = "\n";
    public static final String ERROR_IN_SAVING_CSV_FILE = "Error in saving .CSV file";

    /**
     * Generates values of function (k * x + b) for each x.
     * Simulates the data prediction on each iteration of data generation,
     * tests the classic method of the data smoothing.
     *
     * Saves the input, predicted and smoothed arrays.
     *
     */
    private static void testPredictionAndDataSmoothing() {
        Random r = new Random();

        int arrayLength = 1000,   // size of array for testing
                minRandomValue = -10, // minimum value of number for randomize data
                maxRandomValue = 10,    // maximum value of number for randomize data
                k = 4,  // coefficient k in function (k * x + b)
                b = 6,  // coefficient b in function (k * x + b)
                w = 5; // the window of moving averaging

        double p = 0.2; // proportion of maximum values for averaging

        double currentSmoothValue = 0, currentPredictedValue = 0;

        ArrayList<Double> yGenerated = new ArrayList<Double>();

        ArrayList<Double> ySmoothed = new ArrayList<Double>();  // collection for smoothed data

        ArrayList<Double> yPredicted = new ArrayList<Double>();

        DataSmoothing ds = new DataSmoothing(w, p);

        Predictor pr = new Predictor(w, w, 3, 100);

        for(int i = 1; i <= arrayLength; ++i) {
            double generatedNumber = Math.sqrt(i) + minRandomValue + (maxRandomValue - minRandomValue) * r.nextDouble();
            System.out.println(Math.sqrt(i));
            ds.addValue(generatedNumber);

            if(i > w) {
                currentSmoothValue = ds.getHybridSmoothValue();
                pr.addValue(currentSmoothValue);
            }

            if(i > w * 2) {
                currentPredictedValue = pr.getPredict();
                pr.computeFuturePredictions();

                if(pr.isQosViolated())
                    System.out.println(pr.getQosViolatedTime());
            }

            yGenerated.add(generatedNumber);
            ySmoothed.add(currentSmoothValue);
            yPredicted.add(currentPredictedValue);
        }

        // saving into .csv files
        saveArrayListToCsv(yGenerated, "gen");
        saveArrayListToCsv(yPredicted, "prediction");
        saveArrayListToCsv(ySmoothed, "smoothing");
    }

    /**
     * Saves the ArrayList into .csv file.
     *
     * @param arrayList input ArrayList
     * @param filename name of the output file
     */
    private static void saveArrayListToCsv(ArrayList<Double> arrayList, String filename) {
        try {
            OutputStream f = new FileOutputStream(filename + CSV_FILE_FORMAT, false);

            OutputStreamWriter writer = new OutputStreamWriter(f);
            BufferedWriter out = new BufferedWriter(writer);

            for (Double item : arrayList) {
                out.write(String.valueOf(item) + CSV_DELIMITER);
            }

            out.flush();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(ERROR_IN_SAVING_CSV_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        testPredictionAndDataSmoothing();
    }
}

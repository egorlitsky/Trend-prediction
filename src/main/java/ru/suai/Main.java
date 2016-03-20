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

        int arrayLength = 50,   // size of array for testing
                minRandomValue = -25, // minimum value of number for randomize data
                maxRandomValue = 25,    // maximum value of number for randomize data
                k = 4,  // coefficient k in function (k * x + b)
                b = 6,  // coefficient b in function (k * x + b)
                w = 5; // the window of moving averaging

        double p = 0.2; // proportion ov maximum values for averaging

        ArrayList<Double> y = new ArrayList<Double>();  // collection for generated data
        ArrayList<Double> yPredicted = new ArrayList<Double>(); // collection for predicted data
        ArrayList<Double> ySmoothed = new ArrayList<Double>();  // collection for smoothed data

        Predictor predictor = new Predictor(w);
        DataSmoothing ds = new DataSmoothing(y, w, p);

        for(int i = 0; i < arrayLength; ++i) {
            // generation new value
            double generateNumber = k * i + b + minRandomValue + (maxRandomValue - minRandomValue) * r.nextDouble();

            y.add(generateNumber);
            yPredicted.add(predictor.getPredict(y));
        }

        // data smoothing
        for(int i = 0; i < arrayLength; ++i) {
            ySmoothed.add(ds.getMovingAverageValue(i));
        }

        // saving into .csv files
        saveArrayListToCsv(y, "gen");
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

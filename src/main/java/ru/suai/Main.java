package ru.suai;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RefineryUtilities;

import ru.suai.computing.*;
import ru.suai.generators.*;
import ru.suai.view.Visualizator;

import java.io.IOException;
import java.util.HashMap;

/**
 * It's the main class of the project.
 */
public class Main {

    public static final int FETCH_PERIOD = 60000;
    public static final int PLOT_RENDERING_DELAY = 1000;
    public static final int PLOT_POINTS_COUNT = 10;
    public static final String GENERATED_PLOT_TITLE = "generated";
    public static final String SMOOTHED_PLOT_TITLE = "smoothed";
    public static final String PREDICTED_PLOT_TITLE = "predicted";

    public static void main(String[] args) {
        testDiurnalGenerator();
/*        testArtificialGenerator();
        testMySQLConnection();

        try {
            testRRD4J();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    /**
     * Blank for testing on storage emulator.
     */
    private static void testMySQLConnection() {
        IOGenerator iogen = new IOGenerator("jdbc:mysql://192.168.245.1:3306/Shop", "gen", "pwd123");
        iogen.generateRequests(5, 10, "SELECT * FROM products");
    }

    /**
     * Testing on Ganglia's monitoring data.
     *
     * @throws IOException
     */
    private static void testRRD4J() throws IOException {
        int w = 5,
                i = 1,
                qos = 100,
                futurePredicts = 3;

        double p = 0.8, // proportion of maximum values for averaging
                currentSmoothValue = 0,
                currentPredictedValue = 0,
                generatedNumber = 0;

        DataSmoothing ds = new DataSmoothing(w, p);
        Predictor pr = new Predictor(w, w, futurePredicts, qos);


        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Visualizator chart = new Visualizator(dataset);

        RefineryUtilities.centerFrameOnScreen(chart);

        chart.setVisible(true);

        while (true) {
            RrdGenerator rrdGen = new RrdGenerator("diskstat_sda1_io_time");
            generatedNumber = rrdGen.getNextValue();

            System.out.println(generatedNumber);
            ds.addValue(generatedNumber);

            if (i > w) {
                currentSmoothValue = ds.getHybridSmoothValue();
                pr.addValue(currentSmoothValue);
            }

            if (i > w * 2) {
                currentPredictedValue = pr.getPredict();
                //pr.computeFuturePredictions();
            }

            if (i % PLOT_POINTS_COUNT == 0) {
                dataset.clear();
            }

            dataset.addValue(generatedNumber, GENERATED_PLOT_TITLE, "" + i);
            dataset.addValue(currentSmoothValue, SMOOTHED_PLOT_TITLE, "" + i);
            dataset.addValue(currentPredictedValue, PREDICTED_PLOT_TITLE, "" + (i + 1));

            // update graphic
            chart.pack();
            ++i;

            try {
                Thread.sleep(FETCH_PERIOD);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Testing work of predictor on generated linear / degree/ exponential data.
     * Visualization shows plot with generated, smoothed and predicted data.
     */
    public static void testArtificialGenerator() {
        int w = 10,
                i = 1,
                qos = 100,
                futurePredicts = 3,
                pointsCount = 100;

        double p = 0.3, // proportion of maximum values for averaging
                currentSmoothValue = 0,
                currentPredictedValue = 0,
                generatedNumber,
                a = 4,
                b = 2;

        DataSmoothing ds = new DataSmoothing(w, p);
        Predictor pr = new Predictor(w, w, futurePredicts, qos);

        DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
        Visualizator chart = new Visualizator(dataSet);
        ArtificialGenerator ag = new ArtificialGenerator(a, b, 25);

        RefineryUtilities.centerFrameOnScreen(chart);

        chart.setVisible(true);

        for (int j = 1; j < pointsCount; j++) {
            generatedNumber = (double) ag.getLinearValue(i);

            ds.addValue(generatedNumber);

            if (i > w) {
                currentSmoothValue = ds.getHybridSmoothValue();
                pr.addValue(currentSmoothValue);
            }

            if (i > w * 2) {
                currentPredictedValue = pr.getPredict();
                //pr.computeFuturePredictions();
            }

            if (i % PLOT_POINTS_COUNT == 0) {
                dataSet.clear();
            }

            dataSet.addValue(generatedNumber, GENERATED_PLOT_TITLE, "" + i);
            dataSet.addValue(currentSmoothValue, SMOOTHED_PLOT_TITLE, "" + i);
            dataSet.addValue(currentPredictedValue, PREDICTED_PLOT_TITLE, "" + (i + 1));

            // update graphic
            chart.pack();
            ++i;

            try {
                Thread.sleep(PLOT_RENDERING_DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * It simulates the operation of the predictor with generated
     * diurnal function and shows the results on the plot.
     */
    private static void testDiurnalGenerator() {
        int w = 5,
                i = 1,
                qos = 100,
                futurePredicts = 3,
                pointsCount = 100;

        double p = 0.8, // proportion of maximum values for averaging
                currentSmoothValue = 0,
                currentPredictedValue = 0,
                generatedNumber;

        HashMap<DiurnalGenerator.modulation, Double> modulation = new HashMap<>();
        modulation.put(DiurnalGenerator.modulation.AMPLITUDE, 5.0);
        modulation.put(DiurnalGenerator.modulation.PERIOD, 5.0);
        modulation.put(DiurnalGenerator.modulation.PHASE, 0.0);

        HashMap<DiurnalGenerator.distribution, String> distribution = new HashMap<>();
        distribution.put(DiurnalGenerator.distribution.DISTRIBUTION_TYPE, DiurnalGenerator.POISSON_DISTRIBUTION_TYPE);
        distribution.put(DiurnalGenerator.distribution.SHAPE_TYPE, Predictor.LINEAR_FUNCTION_TYPE);
        distribution.put(DiurnalGenerator.distribution.COEFFICIENT_A, "1.0");
        distribution.put(DiurnalGenerator.distribution.COEFFICIENT_B, "2.0");

        DataSmoothing ds = new DataSmoothing(w, p);
        Predictor pr = new Predictor(w, w, futurePredicts, qos);
        DiurnalGenerator diurnalGenerator = new DiurnalGenerator(modulation, distribution, 0.5);

        DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
        Visualizator chart = new Visualizator(dataSet);

        RefineryUtilities.centerFrameOnScreen(chart);
        chart.setVisible(true);

        for (int j = 1; j < pointsCount; j++) {
            generatedNumber = diurnalGenerator.getValue(i);
            ds.addValue(generatedNumber);

            if (i > w) {
                currentSmoothValue = ds.getHybridSmoothValue();
                pr.addValue(currentSmoothValue);
            }

            if (i > w * 2) {
                currentPredictedValue = pr.getPredict();
                //pr.computeFuturePredictions();
            }

            dataSet.addValue(generatedNumber, GENERATED_PLOT_TITLE, "" + i);
            dataSet.addValue(currentSmoothValue, SMOOTHED_PLOT_TITLE, "" + i);
            dataSet.addValue(currentPredictedValue, PREDICTED_PLOT_TITLE, "" + (i + 1));

            // update graphic
            chart.pack();
            ++i;

            try {
                Thread.sleep(PLOT_RENDERING_DELAY / 2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

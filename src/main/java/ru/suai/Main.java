package ru.suai;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RefineryUtilities;

import ru.suai.computing.*;
import ru.suai.generators.*;
import ru.suai.view.Visualisator;

import java.io.IOException;

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
        //testMySQLConnection();
        testArtificialGenerator();

/*        try {
            testRRD4J();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    private static void testMySQLConnection() {
        IOGenerator iogen = new IOGenerator("jdbc:mysql://192.168.245.1:3306/Shop", "gen", "pwd123");
        iogen.generateRequests(5, 10, "SELECT * FROM products");
    }

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
        Visualisator chart = new Visualisator(dataset);

        RefineryUtilities.centerFrameOnScreen(chart);

        chart.setVisible(true);

        // TODO: change
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
                pr.computeFuturePredictions();

                if (pr.isQosViolated())
                    System.out.println(pr.getQosViolatedTime());
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

    private static void testArtificialGenerator() {
        int arrayLength = 50,   // size of array for testing
                randomness = 100,
                k = 4,  // coefficient k in function (k * x + b)
                w = 5,
                qos = 100,
                futurePredicts = 3;

        double p = 0.8, // proportion of maximum values for averaging
                currentSmoothValue = 0,
                currentPredictedValue = 0;

        ArtificialGenerator a = new ArtificialGenerator(ArtificialGenerator.LINEAR_FUNCTION_TYPE, k, randomness);
        DataSmoothing ds = new DataSmoothing(w, p);
        Predictor pr = new Predictor(w, w, futurePredicts, qos);


        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Visualisator chart = new Visualisator(dataset);

        RefineryUtilities.centerFrameOnScreen(chart);

        chart.setVisible(true);

        for (int i = 1; i <= arrayLength; ++i) {
            double generatedNumber = (Double) a.getNextValue(i);

            ds.addValue(generatedNumber);

            if (i > w) {
                currentSmoothValue = ds.getHybridSmoothValue();
                pr.addValue(currentSmoothValue);
            }

            if (i > w * 2) {
                currentPredictedValue = pr.getPredict();
                pr.computeFuturePredictions();

                if (pr.isQosViolated())
                    System.out.println(pr.getQosViolatedTime());
            }

            if (i % PLOT_POINTS_COUNT == 0) {
                dataset.clear();
            }

            dataset.addValue(generatedNumber, GENERATED_PLOT_TITLE, "" + i);
            dataset.addValue(currentSmoothValue, SMOOTHED_PLOT_TITLE, "" + i);
            dataset.addValue(currentPredictedValue, PREDICTED_PLOT_TITLE, "" + (i + 1));

            try {
                Thread.sleep(PLOT_RENDERING_DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // update graphic
            chart.pack();
        }
    }
}

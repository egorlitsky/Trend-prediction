package ru.suai;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RefineryUtilities;

import ru.suai.computing.ArtificialGenerator;
import ru.suai.computing.DataSmoothing;
import ru.suai.computing.Predictor;
import ru.suai.computing.RrdGenerator;
import ru.suai.view.Visualisator;

import java.io.IOException;

/**
 * It's the main class of the project.
 */
public class Main {

    public static final int FETCH_PERIOD = 60000;
    public static final String GENERATED_PLOT_TITLE = "generated";
    public static final String SMOOTHED_PLOT_TITLE = "smoothed";
    public static final String PREDICTED_PLOT_TITLE = "predicted";

    public static void main(String[] args) {
        testArtificialGenerator();

/*        try {
            testRRD4J();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    private static void testRRD4J() throws IOException {
        int w = 5,
                i = 1,
                qos = 100,
                futurePredicts = 3;

        double p = 0.8, // proportion of maximum values for averaging
                currentSmoothValue = 0,
                currentPredictedValue = 0;

        RrdGenerator rrdGen = new RrdGenerator("diskstat_sda1_io_time");
        DataSmoothing ds = new DataSmoothing(w, p);
        Predictor pr = new Predictor(w, w, futurePredicts, qos);


        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Visualisator chart = new Visualisator(dataset);

        RefineryUtilities.centerFrameOnScreen(chart);

        chart.setVisible(true);

        // TODO: change
        while (true) {
            double generatedNumber = rrdGen.getNextValue();

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

            dataset.addValue(generatedNumber, GENERATED_PLOT_TITLE, "" + i);
            dataset.addValue(currentSmoothValue, SMOOTHED_PLOT_TITLE, "" + i);
            dataset.addValue(currentPredictedValue, PREDICTED_PLOT_TITLE, "" + (i + 1));

            try {
                Thread.sleep(FETCH_PERIOD);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // update graphic
            chart.pack();
            ++i;
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

            dataset.addValue(generatedNumber, GENERATED_PLOT_TITLE, "" + i);
            dataset.addValue(currentSmoothValue, SMOOTHED_PLOT_TITLE, "" + i);
            dataset.addValue(currentPredictedValue, PREDICTED_PLOT_TITLE, "" + (i + 1));

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // update graphic
            chart.pack();
        }
    }
}

package ru.suai;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RefineryUtilities;
import ru.suai.computing.ArtificialGenerator;
import ru.suai.computing.DataSmoothing;
import ru.suai.computing.Predictor;
import ru.suai.view.Visualisator;

/**
 * It's the main class of the project.
 */
public class Main {
    public static void main(String[] args) {
        int arrayLength = 50,   // size of array for testing
                randomness = 50,
                k = 4,  // coefficient k in function (k * x + b)
                w = 5,
                qos = 100,
                futurePredicts = 3;

        double p = 0.8; // proportion of maximum values for averaging

        double currentSmoothValue = 0, currentPredictedValue = 0;

        DataSmoothing ds = new DataSmoothing(w, p);

        Predictor pr = new Predictor(w, w, futurePredicts, qos);

        ArtificialGenerator a = new ArtificialGenerator(ArtificialGenerator.LINEAR_FUNCTION_TYPE, k, randomness);

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

            dataset.addValue(generatedNumber, "generated", "" + i);
            dataset.addValue(currentSmoothValue, "smoothed", "" + i);
            dataset.addValue(currentPredictedValue, "predicted", "" + (i + 1));

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

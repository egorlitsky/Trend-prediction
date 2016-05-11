package ru.suai;

import org.apache.log4j.*;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RefineryUtilities;

import ru.suai.computing.*;
import ru.suai.generators.*;
import ru.suai.view.Visualizator;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;

/**
 * It's the main class of the project.
 */
public class Main {
    /**
     * Time period of Ganglia data getting.
     */
    public static final int FETCH_PERIOD = 60000;

    /**
     * Value of plot rendering delay.
     */
    public static final int PLOT_RENDERING_DELAY = 1000;

    /**
     * Count of points on the plot.
     */
    public static final int PLOT_POINTS_COUNT = 10;

    /**
     * Title of generated data line.
     */
    public static final String GENERATED_PLOT_TITLE = "generated";

    /**
     * Title of smoothed data line.
     */
    public static final String SMOOTHED_PLOT_TITLE = "smoothed";

    /**
     * Title of predicted data line on the plot.
     */
    public static final String PREDICTED_PLOT_TITLE = "predicted";

    /**
     * Logger for result check.
     */
    private static final Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        testDiurnalGenerator();
        //testArtificialGenerator();

/*        try {
            testRRD4J();
        } catch (IOException e) {
            e.printStackTrace();
        }
		*/
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
                generatedNumber;

        PropertyConfigurator.configure("log4j.properties");

        DataSmoothing ds = new DataSmoothing(w, p);
        Predictor pr = new Predictor(w, w, futurePredicts, qos);

        HashMap<DiurnalGenerator.modulation, Double> modulation = new HashMap<>();
        modulation.put(DiurnalGenerator.modulation.AMPLITUDE, 2.0);
        modulation.put(DiurnalGenerator.modulation.PERIOD, 5.0);
        modulation.put(DiurnalGenerator.modulation.PHASE, 0.0);

        HashMap<DiurnalGenerator.distribution, String> distribution = new HashMap<>();
        distribution.put(DiurnalGenerator.distribution.DISTRIBUTION_TYPE, DiurnalGenerator.POISSON_DISTRIBUTION_TYPE);
        distribution.put(DiurnalGenerator.distribution.SHAPE_TYPE, Predictor.LINEAR_FUNCTION_TYPE);
        distribution.put(DiurnalGenerator.distribution.COEFFICIENT_A, "3.0");
        distribution.put(DiurnalGenerator.distribution.COEFFICIENT_B, "2.0");
        DiurnalGenerator diurnalGenerator = new DiurnalGenerator(modulation, distribution, 10);
        //IOGenerator iogen = new IOGenerator("jdbc:mysql://192.168.119.134:3306/test", "generator", "asdf1234");


        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Visualizator chart = new Visualizator(dataset);

        RefineryUtilities.centerFrameOnScreen(chart);

        chart.setVisible(true);

        FileSystem system = FileSystems.getDefault();
        Path original = system.getPath("1.txt");
        Path target = system.getPath("2.txt");

        while (true) {
            int requestsCount = (int)diurnalGenerator.getValue(i);
            for (int k = 0; k < requestsCount; k++) {
                Files.copy(original, target, StandardCopyOption.REPLACE_EXISTING);
            }

/*            switch ((int) (Math.random() * 3)) {
                case 0:
                    iogen.generateRequests((int)diurnalGenerator.getValue(i), 500, "SELECT * FROM test");
                    System.out.println("1");
                    break;
                case 1:
                    iogen.generateRequests((int)diurnalGenerator.getValue(i), 500, "SELECT * FROM test1");
                    System.out.println("2");
                    break;
                case 2:
                    System.out.println("3");
                    iogen.generateRequests((int)diurnalGenerator.getValue(i), 500, "SELECT * FROM test2");
                    break;
            }*/


            RrdGenerator rrdGen = new RrdGenerator("diskstat_sda1_writes");
            generatedNumber = rrdGen.getNextValue();

            logger.info("Time moment: " + i + ", I/O requests: " + requestsCount
                    + ", RRD data: " + generatedNumber);


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
        modulation.put(DiurnalGenerator.modulation.AMPLITUDE, 2.0);
        modulation.put(DiurnalGenerator.modulation.PERIOD, 5.0);
        modulation.put(DiurnalGenerator.modulation.PHASE, 0.0);

        HashMap<DiurnalGenerator.distribution, String> distribution = new HashMap<>();
        distribution.put(DiurnalGenerator.distribution.DISTRIBUTION_TYPE, DiurnalGenerator.POISSON_DISTRIBUTION_TYPE);
        distribution.put(DiurnalGenerator.distribution.SHAPE_TYPE, Predictor.LINEAR_FUNCTION_TYPE);
        distribution.put(DiurnalGenerator.distribution.COEFFICIENT_A, "1.0");
        distribution.put(DiurnalGenerator.distribution.COEFFICIENT_B, "2.0");

        DataSmoothing ds = new DataSmoothing(w, p);
        Predictor pr = new Predictor(w, w, futurePredicts, qos);
        DiurnalGenerator diurnalGenerator = new DiurnalGenerator(modulation, distribution, 10);

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

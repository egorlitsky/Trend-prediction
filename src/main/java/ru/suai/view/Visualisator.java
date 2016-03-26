package main.java.ru.suai.view;

import main.java.ru.suai.computing.ArtificialGenerator;
import main.java.ru.suai.computing.DataSmoothing;
import main.java.ru.suai.computing.Predictor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;

import java.io.*;
import java.util.ArrayList;

/**
 * It is class for visualisation the generated, smoothed and
 * predicted data on the graphic.
 */
public class Visualisator extends ApplicationFrame {
    // String constants
    public static final String CSV_FILE_FORMAT = ".csv";
    public static final String CSV_DELIMITER = "\n";
    public static final String ERROR_IN_SAVING_CSV_FILE = "Error in saving .CSV file";
    public static final String WINDOW_TITLE = "Graphics";
    public static final String COLUMN_TITLE = "Time";
    public static final String ROW_TITLE = "Workload";

    public static final int WIDTH_OF_THE_GRAPHIC = 1200;
    public static final int HEIGHT_OF_THE_GRAPHIC = 500;


    /**
     * Class for visualisation the results of trend-prediction work
     */
    public Visualisator() {
        super(WINDOW_TITLE);
        JFreeChart lineChart = ChartFactory.createLineChart("", COLUMN_TITLE, ROW_TITLE,
                createDataset(),
                PlotOrientation.VERTICAL,
                true, true, false);

        // chart with generated, smoothed and predicted data
        ChartPanel chartPanel = new ChartPanel(lineChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(WIDTH_OF_THE_GRAPHIC, HEIGHT_OF_THE_GRAPHIC));
        setContentPane(chartPanel);
    }

    /**
     * Creates the dataSet for graphics
     * @return the dataSet for visualisation data
     */
    private DefaultCategoryDataset createDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

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
            dataset.addValue(currentPredictedValue, "predicted", "" + i);
        }

        return dataset;
    }

    /**
     * Saves the ArrayList into .csv file.
     *
     * @param arrayList input ArrayList
     * @param filename  name of the output file
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
}

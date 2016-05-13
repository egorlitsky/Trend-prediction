package ru.suai.view;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

import java.io.*;
import java.util.ArrayList;

/**
 * It is class for visualisation the generated, smoothed and
 * predicted data on the graphic.
 */
public class Visualizator extends ApplicationFrame {
    // String constants
    public static final String CSV_FILE_FORMAT = ".csv";
    public static final String CSV_DELIMITER = "\n";
    public static final String ERROR_IN_SAVING_CSV_FILE = "Error in saving .CSV file";
    public static final String WINDOW_TITLE = "Graphics";
    public static final String COLUMN_TITLE = "Time moments";
    public static final String ROW_TITLE = "Workload value";

    // layout parameters
    public static final int WIDTH_OF_THE_GRAPHIC = 1200;
    public static final int HEIGHT_OF_THE_GRAPHIC = 500;


    /**
     * Class for visualisation the results of trend-prediction work
     */
    public Visualizator(XYSeries generated, XYSeries smoothed, XYSeries predicted) {
        super(WINDOW_TITLE);

        final XYSeriesCollection data = new XYSeriesCollection();

        data.addSeries(generated);
        data.addSeries(smoothed);
        data.addSeries(predicted);

        final JFreeChart lineChart = ChartFactory.createXYLineChart(
                "Trend prediction demo",
                COLUMN_TITLE,
                ROW_TITLE,
                data,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // chart with generated, smoothed and predicted data
        ChartPanel chartPanel = new ChartPanel(lineChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(WIDTH_OF_THE_GRAPHIC, HEIGHT_OF_THE_GRAPHIC));
        setContentPane(chartPanel);
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

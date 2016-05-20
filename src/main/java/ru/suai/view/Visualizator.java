package ru.suai.view;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

/**
 * It is class for visualisation the generated, smoothed and
 * predicted data on the graphic.
 */
public class Visualizator {
    // String constants
    public static final String CSV_FILE_FORMAT = ".csv";
    public static final String CSV_DELIMITER = "\n";
    public static final String ERROR_IN_SAVING_CSV_FILE = "Error in saving .CSV file";

    public static final String WINDOW_TITLE = "Storage IOPS trend prediction v.1.0";
    public static final String COLUMN_TITLE = "Time moments";
    public static final String ROW_TITLE = "Workload value";

    public static final String QOS_VIOLATED_STATUS = "VIOLATED";
    public static final String QOS_WILL_BE_VIOLATED_STATUS = "WILL_BE_VIOLATED";
    public static final String QOS_COMPLIED_STATUS = "COMPLIED";

    public static final String OK_MESSAGE = "QoS requirements are complied";
    public static final String ATTENTION_MESSAGE = "QoS requirements may be violated!";
    public static final String CRITICAL_MESSAGE = "QoS requirements are violated!";

    // layout parameters
    public static final int WIDTH_OF_THE_GRAPHIC = 1200;
    public static final int HEIGHT_OF_THE_GRAPHIC = 500;

    // graphic components
    private JPanel panel;

    private JLabel alertLabel;

    private JLabel okIcon;

    private JLabel attentionIcon;

    private JLabel criticalIcon;

    private XYPlot plot;

    /**
     * Class for visualisation the results of trend-prediction work
     */
    public Visualizator(XYSeries generated, XYSeries smoothed, XYSeries predicted) {
        JFrame frame = new JFrame(WINDOW_TITLE);
        frame.setBackground(Color.WHITE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(0, 0));

        ChartPanel chartPanel = getChartPanel(generated, smoothed, predicted);

        frame.add(chartPanel, BorderLayout.WEST);
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setHorizontalAxisTrace(true);
        chartPanel.setVerticalAxisTrace(true);

        panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(Color.WHITE);
        panel.add(new JLabel("Workload status: "));

        this.alertLabel = new JLabel("");

        Font font = this.alertLabel.getFont();
        Font boldFont = new Font(font.getFontName(), Font.BOLD, font.getSize());
        this.alertLabel.setFont(boldFont);
        panel.add(this.alertLabel);

        frame.add(panel, BorderLayout.SOUTH);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        this.initializeIcons();
    }

    /**
     * Initializes the status icons of
     * QoS violating.
     */
    private void initializeIcons() {
        BufferedImage okPicture = null;
        try {
            okPicture = ImageIO.read(new File("ok.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.okIcon = new JLabel(new ImageIcon(okPicture));

        BufferedImage criticalPicture = null;
        try {
            criticalPicture = ImageIO.read(new File("critical.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.criticalIcon = new JLabel(new ImageIcon(criticalPicture));

        BufferedImage attentionPicture = null;
        try {
            attentionPicture = ImageIO.read(new File("attention.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.attentionIcon = new JLabel(new ImageIcon(attentionPicture));

        this.panel.add(this.attentionIcon);
        this.panel.add(this.okIcon);
        this.panel.add(this.criticalIcon);

        this.attentionIcon.setVisible(false);
        this.criticalIcon.setVisible(false);
        this.okIcon.setVisible(false);
    }

    /**
     * Returns the plot with generated, filtered and
     * predicted lines.
     *
     * @param generated XYSeries of generated data
     * @param smoothed XYSeries of filtered data
     * @param predicted XYSeries of predicted data
     * @return ChartPanel with plot.
     */
    private ChartPanel getChartPanel(XYSeries generated, XYSeries smoothed, XYSeries predicted) {
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

        plot =  lineChart.getXYPlot();
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        plot.setBackgroundPaint(Color.WHITE);
        // chart with generated, smoothed and predicted data
        ChartPanel chartPanel = new ChartPanel(lineChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(WIDTH_OF_THE_GRAPHIC, HEIGHT_OF_THE_GRAPHIC));

        return chartPanel;
    }

    /**
     * Render the line with QoS value.
     *
     * @param qos value of QoS requirement
     */
    public void setQosOnPlot(double qos) {
        ValueMarker vm = new ValueMarker(qos, new Color(44, 149, 241), new BasicStroke(2.0F));
        vm.setLabel("         QoS");
        vm.setLabelOffset(new RectangleInsets(10, 0, 0, 0));
        plot.addRangeMarker(vm);
    }

    /**
     * Updates the QoS violating status.
     *
     * @param currentStatus status
     * @throws IOException
     */
    public void setAlertState(String currentStatus) throws IOException {
        BufferedImage picture;

        switch (currentStatus) {
            case QOS_COMPLIED_STATUS:
                this.alertLabel.setText(OK_MESSAGE);
                this.alertLabel.setForeground(Color.GREEN);

                this.attentionIcon.setVisible(false);
                this.criticalIcon.setVisible(false);
                this.okIcon.setVisible(true);
                break;

            case QOS_WILL_BE_VIOLATED_STATUS:
                this.alertLabel.setText(ATTENTION_MESSAGE);
                this.alertLabel.setForeground(Color.RED);

                this.attentionIcon.setVisible(true);
                this.criticalIcon.setVisible(false);
                this.okIcon.setVisible(false);
                break;

            case QOS_VIOLATED_STATUS:
                this.alertLabel.setText(CRITICAL_MESSAGE);
                this.alertLabel.setForeground(Color.RED);

                this.attentionIcon.setVisible(false);
                this.criticalIcon.setVisible(true);
                this.okIcon.setVisible(false);
                break;
        }
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

package ru.suai.view;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jfree.chart.*;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.ShapeUtilities;
import ru.suai.Main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * It is class for visualisation the generated, smoothed and
 * predicted data on the graphic.
 */
public class Visualizator {
    // String constants
    public static final String WINDOW_TITLE = "Storage IOPS trend prediction v.1.0";
    public static final String COLUMN_TITLE = "Time (hours)";
    public static final String ROW_TITLE = "Workload value (IO per minutes)";

    public static final String OK_MESSAGE = "QoS requirements are complied";
    public static final String ATTENTION_MESSAGE = "QoS requirements may be violated";
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
     * Logger for debug.
     */
    private static final Logger logger = Logger.getLogger(Visualizator.class);

    /**
     * Class for visualisation the results of trend-prediction work
     */
    public Visualizator(XYSeries generated, XYSeries smoothed, XYSeries predicted) {
        PropertyConfigurator.configure(Visualizator.class.getClassLoader().getResource("log4j.properties"));
        JFrame frame = new JFrame(WINDOW_TITLE);
        frame.setBackground(Color.WHITE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(500, 300));
        frame.setLayout(new BorderLayout(0, 0));

        ChartPanel chartPanel = getChartPanel(generated, smoothed, predicted);

        frame.add(chartPanel, BorderLayout.CENTER);
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

        frame.add(panel, BorderLayout.PAGE_END);
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
            okPicture = ImageIO.read(Visualizator.class.getClassLoader().getResource("ok.png"));
        } catch (IOException e) {
            logger.info("Exception in reading resource (ok.png).\n" + e.getMessage());
        }
        this.okIcon = new JLabel(new ImageIcon(okPicture));

        BufferedImage criticalPicture = null;
        try {
            criticalPicture = ImageIO.read(Visualizator.class.getClassLoader().getResource("critical.png"));
        } catch (IOException e) {
            logger.info("Exception in reading resource (critical.png).\n" + e.getMessage());
        }
        this.criticalIcon = new JLabel(new ImageIcon(criticalPicture));

        BufferedImage attentionPicture = null;
        try {
            attentionPicture = ImageIO.read(Visualizator.class.getClassLoader().getResource("attention.png"));
        } catch (IOException e) {
            logger.info("Exception in reading resource (attention.png).\n" + e.getMessage());
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
                false,
                false
        );

        plot =  lineChart.getXYPlot();
        plot.setDomainCrosshairPaint(Color.RED);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRenderer(new XYLineAndShapeRenderer(false, true) {
            @Override
            public Shape getItemShape(int row, int col) {
                if (col % (Main.period) == 0) {
                    return ShapeUtilities.createDiamond(2.3f);
                } else {
                    return ShapeUtilities.createDiagonalCross(0, 0);
                }
            }
        });

        XYLineAndShapeRenderer r = (XYLineAndShapeRenderer) plot.getRenderer();

        r.setSeriesShapesVisible(0, false);
        r.setSeriesShapesVisible(1, true);
        r.setSeriesShapesVisible(2, false);

        r.setSeriesLinesVisible(0, true);
        r.setSeriesLinesVisible(1, true);
        r.setSeriesLinesVisible(2, true);

        r.setSeriesStroke(2, new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                1.0f, new float[] {6.0f, 6.0f}, 0.0f));

        r.setSeriesPaint(0, new Color(239, 70, 55));
        r.setSeriesPaint(1, new Color(0, 172, 178));
        r.setSeriesPaint(2, new Color(85, 177, 69));

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
        ValueMarker vm = new ValueMarker(qos, new Color(241, 171, 0), new BasicStroke(2.0F));
        vm.setLabel("         QoS");
        vm.setLabelOffset(new RectangleInsets(10, 0, 0, 0));
        plot.addRangeMarker(vm);
    }

    /**
     * Updates the QoS violating status.
     *
     * @param statusType .
     * @param statusText .
     * @throws IOException
     */
    public void setAlertState(int statusType, String statusText) throws IOException {
        switch (statusType) {
            case 1:
                this.alertLabel.setText(OK_MESSAGE);
                this.alertLabel.setForeground(Color.BLACK);

                this.attentionIcon.setVisible(false);
                this.criticalIcon.setVisible(false);
                this.okIcon.setVisible(true);
                break;

            case 2:
                this.alertLabel.setText(ATTENTION_MESSAGE + statusText);
                this.alertLabel.setForeground(new Color(239, 70, 55));

                this.attentionIcon.setVisible(true);
                this.criticalIcon.setVisible(false);
                this.okIcon.setVisible(false);
                break;

            case 3:
                this.alertLabel.setText(CRITICAL_MESSAGE);
                this.alertLabel.setForeground(new Color(239, 70, 55));

                this.attentionIcon.setVisible(false);
                this.criticalIcon.setVisible(true);
                this.okIcon.setVisible(false);
                break;
        }
    }

    public static void showErrorMessageBox(String message) {
        JOptionPane.showMessageDialog(new JFrame(), message, WINDOW_TITLE, JOptionPane.ERROR_MESSAGE);
    }
}

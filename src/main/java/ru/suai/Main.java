package main.java.ru.suai;

import main.java.ru.suai.view.Visualisator;
import org.jfree.ui.RefineryUtilities;

/**
 * It's the main class of the project.
 */
public class Main {
    public static void main(String[] args) {
        Visualisator chart = new Visualisator();

        chart.pack( );
        RefineryUtilities.centerFrameOnScreen(chart);
        chart.setVisible(true);
    }
}

package ru.suai.computing;

import org.rrd4j.core.FetchData;
import org.rrd4j.core.FetchRequest;
import org.rrd4j.core.RrdDb;
import static org.rrd4j.ConsolFun.AVERAGE;

import java.io.IOException;


/**
 * It is a class for generation values of the real workload via Ganglia .RRD database.
 * RRD-database fills by values of monitoring metric.
 *
 * You need to check and may be change the RRD_PATH and COLLECT_TIME_PERIOD with dependency of
 * your Ganglia configuration.
 */
public class RrdGenerator {
    /**
     * Path with rrds of Ganglia.
     */
    public static final String RRD_PATH = "rrdtool://var/lib/ganglia/rrds/my cluster/localhost/";

    /**
     * File format of the Ganglia db.
     */
    public static final String RRD_DATABASE_FORMAT = ".rrd";

    /**
     * Delimiter of the current Timestamp for correct fetch from .rrd.
     */
    public static final int TIME_DELIMITER = 1000;

    /**
     * Time period of the collecting data by Ganglia for our metric.
     */
    public static final int COLLECT_TIME_PERIOD = 60;

    /**
     * Name of the monitoring metric.
     */
    private String metricName;

    /**
     * RrdDb object with Ganglia DB.
     */
    private RrdDb rrdDataBase;

    /**
     * Constructor
     *
     * @param metricName name of the monitoring metric
     * @throws IOException
     */
    public RrdGenerator(String metricName) throws IOException {
        this.metricName = metricName;

        this.rrdDataBase = new RrdDb(metricName + RRD_DATABASE_FORMAT, RRD_PATH + metricName + RRD_DATABASE_FORMAT);
    }

    /**
     * Returns the monitoring metric name.
     * @return the monitoring metric name
     */
    public String getMetricName() {
        return metricName;
    }

    /**
     * Sets the new monitoring metric name
     * @param metricName the monitoring metric name
     */
    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    /**
     * Fetch current data from the Ganglia .rrd file, parsing the data and generate new value.
     * @return the last record from the .rrd
     */
    public double getNextValue() {
        long start = System.currentTimeMillis() / TIME_DELIMITER - COLLECT_TIME_PERIOD,
                end = System.currentTimeMillis() / TIME_DELIMITER;

        double[][] fetchValues;
        double lastFetchedValue = 0;

        FetchRequest request = this.rrdDataBase.createFetchRequest(AVERAGE, start, end);

        FetchData fetchData = null;
        try {
            fetchData = request.fetchData();
        } catch (IOException e) {
            e.printStackTrace();
        }

        fetchValues = fetchData.getValues();

        for(int i = 0; i < fetchData.getColumnCount(); ++i) {
            for (int j = 0; j < fetchData.getRowCount(); j++) {
                if(!Double.isNaN(fetchValues[i][j]))
                    lastFetchedValue = fetchValues[i][j];
            }
        }

        return lastFetchedValue;
    }
}

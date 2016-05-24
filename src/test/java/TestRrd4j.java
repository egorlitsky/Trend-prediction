import org.junit.Before;
import org.junit.Test;
import org.rrd4j.ConsolFun;
import org.rrd4j.DsType;
import org.rrd4j.core.*;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static org.rrd4j.ConsolFun.AVERAGE;

public class TestRrd4j {
    public static final String TEST_VALUE_NAME = "total";

    public static final int ONE_SECOND = 60;

    public static final double TEST_VALUE = 5.0;

    private long startTime;

    private RrdDb rrdDb;

    @Before
    public void setUp() throws Exception {
        this.startTime = Util.normalize(Util.getTimestamp(new Date()), ONE_SECOND);

        File rrd = new File(System.getProperty("user.dir"), "testAggregator.rrd");
        RrdDef rrdDef = new RrdDef(rrd.getAbsolutePath(), startTime, ONE_SECOND);
        rrdDef.addArchive(ConsolFun.AVERAGE, 0, 1, 10);
        rrdDef.addDatasource(TEST_VALUE_NAME, DsType.GAUGE, ONE_SECOND, 0, Double.NaN);
        this.rrdDb = new RrdDb(rrdDef);
    }

    @Test
    public void testReadingFromRrdDataBase() throws IOException {
        Sample sample = rrdDb.createSample();
        sample.setTime(startTime + ONE_SECOND);
        sample.setValue(TEST_VALUE_NAME, TEST_VALUE);
        sample.update();

        FetchRequest request = rrdDb.createFetchRequest(AVERAGE, startTime, startTime + ONE_SECOND);

        FetchData fetchData = null;
        try {
            fetchData = request.fetchData();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert fetchData != null;
        double[][] fetchValues = fetchData.getValues();

        assertEquals(fetchValues[0][1], TEST_VALUE);
    }
}

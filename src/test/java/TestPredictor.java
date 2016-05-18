import org.junit.Before;
import org.junit.Test;

import ru.suai.computing.DataSmoothing;
import ru.suai.computing.Predictor;
import ru.suai.generators.ArtificialGenerator;
import ru.suai.generators.DiurnalGenerator;

import java.util.HashMap;

import static org.junit.Assert.assertTrue;

public class TestPredictor {
    private ArtificialGenerator artificialGenerator;

    private Predictor predictor;

    private double a;

    private double b;

    private int testElementsCount;

    @Before
    public void setUp() throws Exception {
        this.a = 10;
        this.b = 3;
        this.testElementsCount = 10;
        this.artificialGenerator = new ArtificialGenerator(a, b, 0, Predictor.LINEAR_FUNCTION_TYPE);
        this.predictor = new Predictor(0, 10, 0, 0);
    }

    @Test
    public void testLinearCoefficientsComputing() {
        this.artificialGenerator.setFunctionType(Predictor.LINEAR_FUNCTION_TYPE);
        this.predictor = new Predictor(0, 10, 0, 0);
        for (int i = 1; i <= this.testElementsCount; i++) {
            predictor.addValue((double)this.artificialGenerator.getValue(i));
        }

        predictor.computeLinearCoefficients();

        assertTrue(this.a == this.predictor.getLinearCoefficientA() && this.b == this.predictor.getLinearCoefficientB());
    }

    @Test
    public void testDegreeCoefficientsComputing() {
        this.artificialGenerator.setFunctionType(Predictor.DEGREE_FUNCTION_TYPE);
        this.predictor = new Predictor(0, 10, 0, 0);

        for (int i = 1; i <= this.testElementsCount; i++) {
            this.predictor.addValue((double) this.artificialGenerator.getValue(i));
        }

        this.predictor.computeDegreeCoefficients();

        assertTrue(this.a == this.predictor.getDegreeCoefficientA() && this.b == this.predictor.getDegreeCoefficientB());
    }

    @Test
    public void testExponentialCoefficientsComputing() {
        this.artificialGenerator.setFunctionType(Predictor.EXPONENTIAL_FUNCTION_TYPE);
        this.predictor = new Predictor(0, 10, 0, 0);

        for (int i = 1; i <= testElementsCount; i++) {
            this.predictor.addValue((double) this.artificialGenerator.getValue(i));
        }

        this.predictor.computeExponentialCoefficients();

        assertTrue(this.a == this.predictor.getExponentialCoefficientA() && this.b == this.predictor.getExponentialCoefficientB());
    }

    @Test
    public void testLinearFunctionTypeDetermination() {
        this.artificialGenerator.setFunctionType(Predictor.LINEAR_FUNCTION_TYPE);
        this.predictor = new Predictor(0, 10, 0, 0);

        for (int i = 1; i <= testElementsCount; i++) {
            this.predictor.addValue((double) this.artificialGenerator.getValue(i));
        }

        this.predictor.getPredict();

        assertTrue(this.a == this.predictor.getLinearCoefficientA()
                && this.b == this.predictor.getLinearCoefficientB() &&
                this.predictor.getCurrentFunctionType().equals(Predictor.LINEAR_FUNCTION_TYPE));
    }

    @Test
    public void testDegreeFunctionTypeDetermination() {
        this.artificialGenerator.setFunctionType(Predictor.DEGREE_FUNCTION_TYPE);
        this.predictor = new Predictor(0, 10, 0, 0);

        for (int i = 1; i <= testElementsCount; i++) {
            this.predictor.addValue((double) this.artificialGenerator.getValue(i));
        }

        this.predictor.getPredict();

        assertTrue(this.a == this.predictor.getDegreeCoefficientA()
                && this.b == this.predictor.getDegreeCoefficientB() &&
                this.predictor.getCurrentFunctionType().equals(Predictor.DEGREE_FUNCTION_TYPE));
    }

    @Test
    public void testExponentialFunctionTypeDetermination() {
        this.artificialGenerator.setFunctionType(Predictor.EXPONENTIAL_FUNCTION_TYPE);
        this.predictor = new Predictor(0, 10, 0, 0);

        for (int i = 1; i <= testElementsCount; i++) {
            this.predictor.addValue((double) this.artificialGenerator.getValue(i));
        }

        this.predictor.getPredict();

        assertTrue(this.a == this.predictor.getExponentialCoefficientA()
                && this.b == this.predictor.getExponentialCoefficientB() &&
                this.predictor.getCurrentFunctionType().equals(Predictor.EXPONENTIAL_FUNCTION_TYPE));
    }

    @Test
    public void testPredictionByLinearFunction() {
        this.artificialGenerator.setFunctionType(Predictor.LINEAR_FUNCTION_TYPE);
        this.predictor = new Predictor(0, 10, 0, 0);

        for (int i = 1; i <= testElementsCount; i++) {
            this.predictor.addValue((double) this.artificialGenerator.getValue(i));
        }

        assertTrue((double) this.artificialGenerator.getValue(testElementsCount + 1) == this.predictor.getPredict());
    }

    @Test
    public void testPredictionByDegreeFunction() {
        this.artificialGenerator.setFunctionType(Predictor.DEGREE_FUNCTION_TYPE);
        this.predictor = new Predictor(0, 10, 0, 0);

        for (int i = 1; i <= testElementsCount; i++) {
            this.predictor.addValue((double) this.artificialGenerator.getValue(i));
        }

        assertTrue((double) this.artificialGenerator.getValue(testElementsCount + 1) == this.predictor.getPredict());
    }

    @Test
    public void testPredictionByExponentialFunction() {
        this.artificialGenerator.setFunctionType(Predictor.EXPONENTIAL_FUNCTION_TYPE);
        this.predictor = new Predictor(0, 10, 0, 0);

        for (int i = 1; i <= testElementsCount; i++) {
            this.predictor.addValue((double) this.artificialGenerator.getValue(i));
        }

        assertTrue((double) this.artificialGenerator.getValue(testElementsCount + 1) == this.predictor.getPredict());
    }

    @Test
    public void testLinearFunctionTypeDeterminationByDiurnal() {
        int w = 15,
                i = 1,
                qos = 100,
                futurePredicts = 3;

        double p = 0.8,
                currentSmoothValue,
                generatedNumber;

        HashMap<DiurnalGenerator.modulation, Double> modulation = new HashMap<>();
        modulation.put(DiurnalGenerator.modulation.AMPLITUDE, 5.0);
        modulation.put(DiurnalGenerator.modulation.PERIOD, 15.0);
        modulation.put(DiurnalGenerator.modulation.PHASE, 0.0);

        HashMap<DiurnalGenerator.distribution, String> distribution = new HashMap<>();
        distribution.put(DiurnalGenerator.distribution.DISTRIBUTION_TYPE, DiurnalGenerator.POISSON_DISTRIBUTION_TYPE);
        distribution.put(DiurnalGenerator.distribution.SHAPE_TYPE, Predictor.LINEAR_FUNCTION_TYPE);
        distribution.put(DiurnalGenerator.distribution.COEFFICIENT_A, "3");
        distribution.put(DiurnalGenerator.distribution.COEFFICIENT_B, "2");

        DataSmoothing ds = new DataSmoothing(w, p);
        this.predictor = new Predictor(w, w, futurePredicts, qos);
        DiurnalGenerator diurnalGenerator = new DiurnalGenerator(modulation, distribution, 50);

        for (int j = 1; j <= 125; j++) {

            generatedNumber = diurnalGenerator.getValue(i);

            ds.addValue(generatedNumber);

            if (i % w == 0) {
                currentSmoothValue = ds.getHybridSmoothValue();
                this.predictor.addValue(currentSmoothValue);
            }
            ++i;
        }

        this.predictor.getPredict();
        assertTrue(this.predictor.getCurrentFunctionType().equals(Predictor.LINEAR_FUNCTION_TYPE));
    }

    @Test
    public void testDegreeFunctionTypeDeterminationByDiurnal() {
        int w = 15,
                i = 1,
                qos = 100,
                futurePredicts = 3;

        double p = 0.8,
                currentSmoothValue,
                generatedNumber;

        HashMap<DiurnalGenerator.modulation, Double> modulation = new HashMap<>();
        modulation.put(DiurnalGenerator.modulation.AMPLITUDE, 5.0);
        modulation.put(DiurnalGenerator.modulation.PERIOD, 15.0);
        modulation.put(DiurnalGenerator.modulation.PHASE, 0.0);

        HashMap<DiurnalGenerator.distribution, String> distribution = new HashMap<>();
        distribution.put(DiurnalGenerator.distribution.DISTRIBUTION_TYPE, DiurnalGenerator.POISSON_DISTRIBUTION_TYPE);
        distribution.put(DiurnalGenerator.distribution.SHAPE_TYPE, Predictor.DEGREE_FUNCTION_TYPE);
        distribution.put(DiurnalGenerator.distribution.COEFFICIENT_A, "3");
        distribution.put(DiurnalGenerator.distribution.COEFFICIENT_B, "4");

        DataSmoothing ds = new DataSmoothing(w, p);
        this.predictor = new Predictor(w, w, futurePredicts, qos);
        DiurnalGenerator diurnalGenerator = new DiurnalGenerator(modulation, distribution, 50);

        for (int j = 1; j <= 125; j++) {

            generatedNumber = diurnalGenerator.getValue(i);

            ds.addValue(generatedNumber);

            if (i % w == 0) {
                currentSmoothValue = ds.getHybridSmoothValue();
                this.predictor.addValue(currentSmoothValue);
            }
            ++i;
        }

        this.predictor.getPredict();
        assertTrue(this.predictor.getCurrentFunctionType().equals(Predictor.DEGREE_FUNCTION_TYPE));
    }

    @Test
    public void testExponentialFunctionTypeDeterminationByDiurnal() {
        int w = 15,
                i = 1,
                qos = 100,
                futurePredicts = 3;

        double p = 0.8, // proportion of maximum values for averaging
                currentSmoothValue,
                generatedNumber;

        HashMap<DiurnalGenerator.modulation, Double> modulation = new HashMap<>();
        modulation.put(DiurnalGenerator.modulation.AMPLITUDE, 5.0);
        modulation.put(DiurnalGenerator.modulation.PERIOD, 15.0);
        modulation.put(DiurnalGenerator.modulation.PHASE, 0.0);

        HashMap<DiurnalGenerator.distribution, String> distribution = new HashMap<>();
        distribution.put(DiurnalGenerator.distribution.DISTRIBUTION_TYPE, DiurnalGenerator.POISSON_DISTRIBUTION_TYPE);
        distribution.put(DiurnalGenerator.distribution.SHAPE_TYPE, Predictor.EXPONENTIAL_FUNCTION_TYPE);
        distribution.put(DiurnalGenerator.distribution.COEFFICIENT_A, "2");
        distribution.put(DiurnalGenerator.distribution.COEFFICIENT_B, "4");

        DataSmoothing ds = new DataSmoothing(w, p);
        this.predictor = new Predictor(w, w, futurePredicts, qos);
        DiurnalGenerator diurnalGenerator = new DiurnalGenerator(modulation, distribution, 50);

        for (int j = 1; j <= 125; j++) {
            generatedNumber = diurnalGenerator.getValue(i);

            ds.addValue(generatedNumber);

            if (i % w == 0) {
                currentSmoothValue = ds.getHybridSmoothValue();
                this.predictor.addValue(currentSmoothValue);
            }
            ++i;
        }

        this.predictor.getPredict();

        assertTrue(this.predictor.getCurrentFunctionType().equals(Predictor.EXPONENTIAL_FUNCTION_TYPE));
    }
}
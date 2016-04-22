import org.junit.Before;
import org.junit.Test;

import ru.suai.computing.Predictor;
import ru.suai.generators.ArtificialGenerator;

import static org.junit.Assert.assertTrue;

public class TestPredictor {
    private ArtificialGenerator artificialGenerator;

    private Predictor predictor;

    private double a;

    private double b;

    private int testElementsCount;

    @Before
    public void setUp() throws Exception { // TODO:add to pom
        this.a = 10;
        this.b = 3;
        this.testElementsCount = 10;
        this.artificialGenerator = new ArtificialGenerator(a, b, 0);
        this.predictor = new Predictor(0, 10, 0, 0);
    }

    @Test
    public void testLinearCoefficientsComputing() {
        this.predictor = new Predictor(0, 10, 0, 0);
        for (int i = 1; i <= this.testElementsCount; i++) {
            predictor.addValue((double)this.artificialGenerator.getLinearValue(i));
        }

        predictor.computeLinearCoefficients();

        assertTrue(this.a == this.predictor.getLinearCoefficientA() && this.b == this.predictor.getLinearCoefficientB());
    }

    @Test
    public void testDegreeCoefficientsComputing() {
        this.predictor = new Predictor(0, 10, 0, 0);

        for (int i = 1; i <= this.testElementsCount; i++) {
            this.predictor.addValue((double) this.artificialGenerator.getDegreeValue(i));
        }

        this.predictor.computeDegreeCoefficients();

        assertTrue(this.a == this.predictor.getDegreeCoefficientA() && this.b == this.predictor.getDegreeCoefficientB());
    }

    @Test
    public void testExponentialCoefficientsComputing() {
        this.predictor = new Predictor(0, 10, 0, 0);

        for (int i = 1; i <= testElementsCount; i++) {
            this.predictor.addValue((double) this.artificialGenerator.getExponentialValue(i));
        }

        this.predictor.computeExponentialCoefficients();

        assertTrue(this.a == this.predictor.getExponentialCoefficientA() && this.b == this.predictor.getExponentialCoefficientB());
    }

    @Test
    public void testLinearFunctionTypeDetermination() {
        this.predictor = new Predictor(0, 10, 0, 0);

        for (int i = 1; i <= testElementsCount; i++) {
            this.predictor.addValue((double) this.artificialGenerator.getLinearValue(i));
        }
          
        this.predictor.getPredict();

        assertTrue(this.a == this.predictor.getLinearCoefficientA()
                && this.b == this.predictor.getLinearCoefficientB() &&
                this.predictor.getCurrentFunctionType().equals(Predictor.LINEAR_FUNCTION_TYPE));
    }

    @Test
    public void testDegreeFunctionTypeDetermination() {
        this.predictor = new Predictor(0, 10, 0, 0);

        for (int i = 1; i <= testElementsCount; i++) {
            this.predictor.addValue((double) this.artificialGenerator.getDegreeValue(i));
        }

        this.predictor.getPredict();

        assertTrue(this.a == this.predictor.getDegreeCoefficientA()
                && this.b == this.predictor.getDegreeCoefficientB() &&
                this.predictor.getCurrentFunctionType().equals(Predictor.DEGREE_FUNCTION_TYPE));
    }

    @Test
    public void testExponentialFunctionTypeDetermination() {
        this.predictor = new Predictor(0, 10, 0, 0);

        for (int i = 1; i <= testElementsCount; i++) {
            this.predictor.addValue((double) this.artificialGenerator.getExponentialValue(i));
        }

        this.predictor.getPredict();

        assertTrue(this.a == this.predictor.getExponentialCoefficientA()
                && this.b == this.predictor.getExponentialCoefficientB() &&
                this.predictor.getCurrentFunctionType().equals(Predictor.EXPONENTIAL_FUNCTION_TYPE));
    }
}
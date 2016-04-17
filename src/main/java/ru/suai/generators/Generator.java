package ru.suai.generators;

/**
 * Interface for generating data of different types
 */
public interface Generator {
    Object getLinearValue(double x);

    Object getDegreeValue(double x);

    Object getExponentialValue(double x);
}

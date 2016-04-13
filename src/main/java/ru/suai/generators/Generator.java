package ru.suai.generators;

/**
 * Interface for generating data of different types
 */
public interface Generator {
    Object getNextValue(int x);
}

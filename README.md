# Trend-prediction

## Introduction

Trend-prediction - it is a project that implements the logic of computing operations for smoothing and data prediction. This project is focused on the prediction of the workload metrics in the storage systems.

Computational logic can be used for prediction of computer resources utilization and metrics of the other devices. It is also forecasting is widely used in economics and business.

The project includes:
* Prediction module (Predictor.java class)
* Data smoothing module (DataSmoothing.java class)
* Stand for simulate workload on the storage system (generators packages, monitoring)
* View to display the result of the work of the trend prediction (view package)

## Build

To build the project from source, clone the project from github:

    $ git clone git://github.com/egorlitsky/Trend-prediction.git

Build the project requires a maven. For build please use

    $ cd Trend-prediction

    $ mvn package
    
## Run

For run the project after build:

    $ cd target
    
    $ java -jar Trend-pred-1.0-SNAPSHOT-jar-with-dependencies.jar

To run the program in different modes and to change the storage system simulation metrics parameters need to change the file _settings.properties_ in _target_ directory.

## Usage

You can use classes DataSmoothing and Predictor to average your metric values and for prediction of values of your metrics.

## Authors

This project was developed and designed by Egorlitsky V. and Linsky E. 
Information Security Department, St. Petersburg State University of Aerospace Instrumentation (SPb SUAI).

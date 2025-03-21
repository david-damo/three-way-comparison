package com.example.testing;

public class MainApplication {
    public static void main(String[] args) {
        try {
            ComparisonService comparisonService = new ComparisonService();
            comparisonService.evaluateTestCoverage("DEMO");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

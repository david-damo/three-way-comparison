package com.example.testing;

public class MainApplication {
    public static void main(String[] args) {
        try {
            ComparisonService comparisonService = new ComparisonService();
            ComparisonServiceNew comparisonServiceNew = new ComparisonServiceNew();
            System.out.println("********************Analysis Table**************************");
            comparisonService.evaluateTestCoverage("DEMO");
            comparisonServiceNew.generateComparisonTable("DEMO");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.example.testing;


import java.util.List;
import java.util.stream.Collectors;

public class ComparisonService {
    private JiraService jiraService;
    private CodeAnalyzer codeAnalyzer;
    private TestCoverageAnalyzer testAnalyzer;

    public ComparisonService() {
        this.jiraService = new JiraService();
        this.codeAnalyzer = new CodeAnalyzer();
        this.testAnalyzer = new TestCoverageAnalyzer();
    }

    public void evaluateTestCoverage(String projectKey) throws Exception {
        List<String> businessScenarios = jiraService.getBusinessScenarios(projectKey);
        List<String> developedScenarios = codeAnalyzer.extractBusinessScenarios();
        List<String> testCases = testAnalyzer.getAutomatedTests();

        System.out.println("===== Business Scenarios (Jira) =====");
        businessScenarios.forEach(System.out::println);

        System.out.println("\n===== Developed Features (Code) =====");
        developedScenarios.forEach(System.out::println);

        System.out.println("\n===== Automated Test Cases =====");
        testCases.forEach(System.out::println);

        List<String> missingTests = developedScenarios.stream()
                .filter(dev -> !testCases.contains(dev))
                .collect(Collectors.toList());

        System.out.println("\n===== Missing Test Cases =====");
        missingTests.forEach(System.out::println);
    }
}

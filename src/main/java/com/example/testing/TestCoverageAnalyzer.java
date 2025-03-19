package com.example.testing;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class TestCoverageAnalyzer {
    private static final String TEST_DIRECTORY = "https://github.com/david-damo/biryani-bdd-test";

    public List<String> getAutomatedTests() throws Exception {
        List<String> testCases = new ArrayList<>();
        File testDir = new File(TEST_DIRECTORY);

        for (File file : testDir.listFiles()) {
            if (file.getName().endsWith(".feature") || file.getName().endsWith(".java")) {
                try (Stream<String> stream = Files.lines(file.toPath())) {
                    stream.filter(line -> line.contains("Scenario:"))
                          .forEach(line -> testCases.add(line.replace("Scenario:", "").trim()));
                }
            }
        }

        return testCases;
    }
}

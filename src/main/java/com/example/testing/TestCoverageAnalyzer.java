package com.example.testing;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

public class TestCoverageAnalyzer {
    //private static final String TEST_DIRECTORY = "https://github.com/david-damo/biryani-bdd-test/tree/master/src/test/resources/features";

    
    private static final String TEST_DIRECTORY = "https://github.com/david-damo/biryani-order-app";
    private static final String LOCAL_REPO_PATH = "C:/Users/Sanjeev jha/Tutorial-2024/TestInSight-Workspace/biryani-bdd-tests";
    
    public void cloneRepository() throws GitAPIException {
        File localPath = new File(LOCAL_REPO_PATH);

        // If repo already exists, delete it (optional)
        if (localPath.exists()) {
            deleteDirectory(localPath);
        }

        // Clone the repository
        System.out.println("Cloning repository from " + TEST_DIRECTORY + " to " + LOCAL_REPO_PATH);
        Git.cloneRepository()
            .setURI(TEST_DIRECTORY)
            .setDirectory(localPath)
            .call();
        
        System.out.println("Repository cloned successfully.");
    }
    
    public List<String> getAutomatedTests() throws Exception {
        List<String> testCases = new ArrayList<>();
        File testDir = new File(LOCAL_REPO_PATH);
        if (!testDir.exists()) {
            System.out.println("Error: Directory does not exist - " + LOCAL_REPO_PATH);
        } else if (!testDir.isDirectory()) {
            System.out.println("Error: Path is not a directory - " + LOCAL_REPO_PATH);
        } else {
            File[] files = testDir.listFiles();
            if (files == null) {
                System.out.println("Error: Unable to read files from directory - " + LOCAL_REPO_PATH);
            } else {
                System.out.println("Files found: " + files.length);
            }
        }

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
    
    private void deleteDirectory(File directory) {
        if (directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                deleteDirectory(file);
            }
        }
        directory.delete();
    }
}

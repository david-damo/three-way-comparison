package com.example.testing;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

public class TestCoverageAnalyzer {
	private static final String REMOTE_REPO_URL = "https://github.com/abc/biryani-bdd-test.git";
    private static final String LOCAL_REPO_PATH = "C:/Users/public/ThreeWayTest/Automation";

    private static final String GITHUB_USERNAME = "abc@rediffmail.com";
    private static final String GITHUB_TOKEN = "token";  
    public void cloneOrInitializeRepository() {
        File localPath = new File(LOCAL_REPO_PATH);
        System.out.println("Cloning repository for automation");
        // Delete old repository if it's invalid (missing .git folder)
        if (localPath.exists() && !new File(localPath, ".git").exists()) {
            System.out.println("Deleting incomplete repository at " + LOCAL_REPO_PATH);
            deleteDirectory(localPath);
        }

        try {
            // Step 1: Clone from GitHub if not already cloned
            if (!localPath.exists() || !new File(localPath, ".git").exists()) {
                System.out.println("Cloning repository from " + REMOTE_REPO_URL + " to " + LOCAL_REPO_PATH);
                Git.cloneRepository()
                    .setURI(REMOTE_REPO_URL)
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(GITHUB_USERNAME, GITHUB_TOKEN)) // 🔹 Pass credentials
                    .setBranch("master") 
                    .setDirectory(localPath)
                    .call();
                System.out.println("Repository cloned successfully.");
            } else {
                System.out.println("Repository already exists at " + LOCAL_REPO_PATH);
            }
        } catch (GitAPIException e) {
            System.err.println("Error cloning repository: " + e.getMessage());
            e.printStackTrace();
            System.out.println("Cloning failed. Attempting to initialize a new Git repository...");
            initializeNewGitRepository(localPath);
        }
    }

    private void initializeNewGitRepository(File repoDir) {
        try {
            System.out.println("Initializing new Git repository at " + repoDir.getAbsolutePath());
            Git.init().setDirectory(repoDir).call();
            System.out.println("New repository initialized successfully.");
        } catch (GitAPIException e) {
            System.err.println("Failed to initialize new Git repository: " + e.getMessage());
            e.printStackTrace();
        }
    }
	
	//////////////////////////////////////////////////////////////////////////////////////
    
    public List<String> extractAutomationScenarios() {
        List<String> automationScenarios = new ArrayList<>();
        File repoDir = new File(LOCAL_REPO_PATH);
        cloneOrInitializeRepository();
        if (!repoDir.exists()) {
            System.err.println("Error: Repository does not exist at " + LOCAL_REPO_PATH);
            return automationScenarios;
        }

        try (Stream<Path> paths = Files.walk(Paths.get(LOCAL_REPO_PATH))) {
            paths.filter(Files::isRegularFile)
                 .filter(path -> path.toString().endsWith(".feature")) // Read only feature files
                 .forEach(path -> extractScenariosFromFeatureFile(path, automationScenarios));
        } catch (IOException e) {
            System.err.println("Error reading automation files: " + e.getMessage());
            e.printStackTrace();
        }

        return automationScenarios;
    }

    private void extractScenariosFromFeatureFile(Path filePath, List<String> scenarios) {
        try {
            List<String> lines = Files.readAllLines(filePath);
            for (String line : lines) {
                if (line.trim().startsWith("Scenario:")) {
                    scenarios.add(line.replace("Scenario:", "").trim());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + filePath + " - " + e.getMessage());
        }
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

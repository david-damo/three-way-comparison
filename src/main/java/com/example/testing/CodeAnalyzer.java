package com.example.testing;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class CodeAnalyzer {
	//File directory = new File("C:/Users/Sanjeev jha/Tutorial-2024/TestInSight-Workspace/TempClone".replace(" ", "%20"));
    private static final String REMOTE_REPO_URL = "https://github.com/david-damo/biryani-order-app.git";
    private static final String LOCAL_REPO_PATH = "C:/Users/public/ThreeWayTest/TempClone";
    //private static final String LOCAL_REPO_PATH = directory.;

    private static final String GITHUB_USERNAME = "GITHUB_USERNAME";
    private static final String GITHUB_TOKEN = "GitHub Token";  
    public void cloneOrInitializeRepository() {
        File localPath = new File(LOCAL_REPO_PATH);

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

    public List<String> extractBusinessScenarios() {
        List<String> scenarios = new ArrayList<>();
        File repoDir = new File(LOCAL_REPO_PATH);

        // Ensure the repository exists before analyzing
        if (!repoDir.exists() || !new File(repoDir, ".git").exists()) {
            System.err.println("Error: Repository does not exist at " + LOCAL_REPO_PATH);
            return scenarios;
        }

        try (Stream<Path> paths = Files.walk(Paths.get(LOCAL_REPO_PATH))) {
            paths.filter(Files::isRegularFile)
                 .filter(path -> path.toString().endsWith(".java"))  // Scan only Java files
                 .forEach(path -> extractScenariosFromFile(path, scenarios));
        } catch (IOException e) {
            System.err.println("Error reading source code files: " + e.getMessage());
            e.printStackTrace();
        }

        return scenarios;
    }

    private void extractScenariosFromFile(Path filePath, List<String> scenarios) {
        try {
            List<String> lines = Files.readAllLines(filePath);
            StringBuilder fileContent = new StringBuilder();

            for (String line : lines) {
                fileContent.append(line).append("\n");
            }

            String content = fileContent.toString();

            // Extract class name (potential business scenario)
            Pattern classPattern = Pattern.compile("public\\s+class\\s+(\\w+)");
            Matcher classMatcher = classPattern.matcher(content);
            while (classMatcher.find()) {
                scenarios.add("Business Scenario: " + classMatcher.group(1));
            }

            // Extract method names (potential actions in scenarios)
            Pattern methodPattern = Pattern.compile("public\\s+(\\w+\\s+)?(\\w+)\\s*\\(");
            Matcher methodMatcher = methodPattern.matcher(content);
            while (methodMatcher.find()) {
                scenarios.add("Method: " + methodMatcher.group(2));
            }

            // Extract Javadoc comments as descriptions
            Pattern javadocPattern = Pattern.compile("/\\*\\*(.*?)\\*/", Pattern.DOTALL);
            Matcher javadocMatcher = javadocPattern.matcher(content);
            while (javadocMatcher.find()) {
                String docComment = javadocMatcher.group(1).replaceAll("\\s+", " ").trim();
                scenarios.add("Javadoc: " + docComment);
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

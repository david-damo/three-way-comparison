package com.example.testing;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CodeAnalyzer {
    private static final String REMOTE_REPO_URL = "https://github.com/david-damo/biryani-order-app";
    private static final String LOCAL_REPO_PATH = "C:/Users/Sanjeev jha/Tutorial-2024/TestInSight-Workspace/three-way-comparison";
    
    public void cloneRepository() throws GitAPIException {
        File localPath = new File(LOCAL_REPO_PATH);

        // If repo already exists, delete it (optional)
        if (localPath.exists()) {
            deleteDirectory(localPath);
        }

        // Clone the repository
        System.out.println("Cloning repository from " + REMOTE_REPO_URL + " to " + LOCAL_REPO_PATH);
        Git.cloneRepository()
            .setURI(REMOTE_REPO_URL)
            .setDirectory(localPath)
            .call();
        
        System.out.println("Repository cloned successfully.");
    }

    public List<String> getDevelopedScenarios() throws Exception {
        List<String> developedScenarios = new ArrayList<>();
        Git git = Git.open(new File(LOCAL_REPO_PATH));
        
        Iterable<RevCommit> commits = git.log().call();
        for (RevCommit commit : commits) {
            developedScenarios.add(commit.getShortMessage());
        }

        git.close();
        return developedScenarios;
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

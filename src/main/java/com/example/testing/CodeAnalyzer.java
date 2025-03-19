package com.example.testing;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CodeAnalyzer {
    private static final String REPO_PATH = "https:///github.com/david-damo/biryani-order-app";

    public List<String> getDevelopedScenarios() throws Exception {
        List<String> developedScenarios = new ArrayList<>();
        Git git = Git.open(new File(REPO_PATH));
        
        Iterable<RevCommit> commits = git.log().call();
        for (RevCommit commit : commits) {
            developedScenarios.add(commit.getShortMessage());
        }

        git.close();
        return developedScenarios;
    }
}

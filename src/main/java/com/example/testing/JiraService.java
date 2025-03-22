package com.example.testing;

import java.net.URI;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class JiraService {
    private static final String JIRA_URL = "https://davidka-home.atlassian.net/rest/api/2/search";
    private static final String JIRA_USERNAME = "username";
    private static final String JIRA_API_TOKEN = "token";

    public List<String> getBusinessScenarios(String projectKey) throws Exception {
        List<String> scenarios = new ArrayList<>();
        String jqlQuery = "project=" + projectKey + " AND issuetype=Story";
        String encodedJql = URLEncoder.encode(jqlQuery, StandardCharsets.UTF_8);
        String queryUrlNew = JIRA_URL + "?jql=" + encodedJql;
        System.out.println("Encoded JIRA URL: " + queryUrlNew);
        String auth = JIRA_USERNAME + ":" + JIRA_API_TOKEN;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(new URI(queryUrlNew));
        request.addHeader("Authorization", "Basic " + encodedAuth);
        request.addHeader("Content-Type", "application/json");

        CloseableHttpResponse response = client.execute(request);
        String jsonResponse = EntityUtils.toString(response.getEntity());
        System.out.println("*************************"+jsonResponse);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(jsonResponse);
        for (JsonNode issue : rootNode.path("issues")) {
        	String summary = issue.path("fields").path("summary").asText();
            scenarios.add(summary);
        }

        client.close();
        return scenarios;
    }
}

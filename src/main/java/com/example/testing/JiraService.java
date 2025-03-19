package com.example.testing;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class JiraService {
    private static final String JIRA_URL = "https://davidka-home.atlassian.net/rest/api/2/search";
    private static final String JIRA_USERNAME = "sanjeev.jha77@gmail.com";
    private static final String JIRA_API_TOKEN = "ATATT3xFfGF0BRumnmH5XinzDOv3-sNNS8jNoFndr9Dw2u5TGnnF5KKz2V4VCaoVv6fsqpgJFT2ZAukndc2N2o1AYdDGn2p9we6VRye4vdpAUUYLesG4UiH6kH4IxUXJxpN8jKGZGGmGFQwklXNKB4XhpcefB9hB6aK576eqIG4lZWKaj5X2Z1Y=7658591F";

    public List<String> getBusinessScenarios(String projectKey) throws Exception {
        List<String> scenarios = new ArrayList<>();
        String queryUrl = JIRA_URL + "?jql=project=" + projectKey + "%20AND%20issuetype=Story";

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(new URI(queryUrl));
        request.addHeader("Authorization", "Basic " + JIRA_API_TOKEN);
        request.addHeader("Content-Type", "application/json");

        CloseableHttpResponse response = client.execute(request);
        String jsonResponse = EntityUtils.toString(response.getEntity());

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

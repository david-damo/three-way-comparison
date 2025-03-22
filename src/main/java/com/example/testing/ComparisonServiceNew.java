package com.example.testing;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

public class ComparisonServiceNew {
    private JiraService jiraService;
    private CodeAnalyzer codeAnalyzer;
    private TestCoverageAnalyzer testAnalyzer;

    public ComparisonServiceNew() {
        this.jiraService = new JiraService();
        this.codeAnalyzer = new CodeAnalyzer();
        this.testAnalyzer = new TestCoverageAnalyzer();
    }

    public void generateComparisonTable(String projectKey) throws Exception {
        List<String> jiraScenarios = jiraService.getBusinessScenarios(projectKey);
        List<String> developedScenarios = codeAnalyzer.extractBusinessScenarios();
        List<String> automationScenarios = testAnalyzer.extractAutomationScenarios();
        
        Set<String> jiraSet = normalizeSet(jiraScenarios);
        Set<String> developedSet = normalizeSet(developedScenarios);
        Set<String> automationSet = normalizeSet(automationScenarios);

        //Set<String> jiraSet = new HashSet<>(jiraScenarios);
        //Set<String> developedSet = new HashSet<>(developedScenarios);
        //Set<String> automationSet = new HashSet<>(automationScenarios);
        Set<String> missingScenarios = new HashSet<>(jiraSet);
        missingScenarios.removeAll(automationSet);

        List<Map<String, String>> tableData = new ArrayList<>();

        for (String jiraScenario : jiraSet) {
            String developedMatch = findClosestMatch(jiraScenario, developedSet);
            String automatedMatch = findClosestMatch(jiraScenario, automationSet);
            boolean isMissing = !automationSet.contains(jiraScenario);

            Map<String, String> row = new LinkedHashMap<>();
            row.put("Jira Acceptance Criteria", jiraScenario);
            row.put("Developed Code Business Scenario", developedMatch.isEmpty() ? "✘" : "✔ (" + developedMatch + ")");
            row.put("Automation Code Scenario", automatedMatch.isEmpty() ? "✘" : "✔ (" + automatedMatch + ")");
            row.put("Missing", isMissing ? "✔" : "");
            tableData.add(row);
        }

        writeToExcel(tableData);
        writeToJson(tableData);
    }
    
    private Set<String> normalizeSet(List<String> scenarios) {
        Set<String> normalizedSet = new HashSet<>();
        for (String scenario : scenarios) {
            normalizedSet.add(scenario.trim().toLowerCase());  // Normalize text
        }
        return normalizedSet;
    }

    private String findClosestMatch(String target, Set<String> sourceSet) {
        for (String source : sourceSet) {
            if (target.equalsIgnoreCase(source) || target.contains(source) || source.contains(target)) {
                return source; // Return best match
            }
        }
        return ""; // No match found
    }

    private void writeToExcel(List<Map<String, String>> tableData) {
        String[] columns = {"Jira Acceptance Criteria", "Developed Code Business Scenario", "Automation Code Scenario", "Missing"};
        String filePath = "C:\\Users\\Public\\ThreeWayTest\\ComparisonReport.xlsx";

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Test Coverage");
            CellStyle wrapStyle = workbook.createCellStyle();
            wrapStyle.setWrapText(true);
            wrapStyle.setAlignment(HorizontalAlignment.LEFT);
            wrapStyle.setVerticalAlignment(VerticalAlignment.TOP);

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (Map<String, String> rowData : tableData) {
                Row row = sheet.createRow(rowNum++);
                int colNum = 0;
                for (String col : columns) {
                    Cell cell = row.createCell(colNum++);
                    cell.setCellValue(rowData.get(col));
                    cell.setCellStyle(wrapStyle);
                }
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            FileOutputStream fileOut = new FileOutputStream(filePath);
            workbook.write(fileOut);
            fileOut.close();
            System.out.println("Excel report saved as: " + filePath);

        } catch (Exception e) {
            System.err.println("Error writing to Excel: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void writeToJson(List<Map<String, String>> tableData) {
        String filePath = "ComparisonReport.json";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), tableData);
            System.out.println("JSON report saved as: " + filePath);
        } catch (Exception e) {
            System.err.println("Error writing to JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

}

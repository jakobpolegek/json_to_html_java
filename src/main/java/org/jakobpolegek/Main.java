package org.jakobpolegek;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jakobpolegek.model.HtmlElement;
import org.jakobpolegek.service.HtmlRenderService;
import org.jakobpolegek.service.JsonAdapterService;
import org.jakobpolegek.util.FileUtils;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java -jar json-to-html-converter.jar input_file_name.json");
            System.exit(1);
        }

        String inputFileName = args[0];
        String outputFileName = inputFileName.replaceAll("\\.json$", ".html");

        System.out.println("Converting " + inputFileName + " to " + outputFileName);

        try {
            String jsonContent = FileUtils.readFileAsString(inputFileName);
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> jsonMap = objectMapper.readValue(jsonContent, new TypeReference<LinkedHashMap<String, Object>>() {});

            JsonAdapterService adapter = new JsonAdapterService();
            String doctype = (String) jsonMap.remove("doctype");
            HtmlElement rootHtmlElement = adapter.adaptRoot(jsonMap);

            HtmlRenderService renderer = new HtmlRenderService();
            String renderedHtml = renderer.render(Collections.singletonList(rootHtmlElement));

            StringBuilder finalHtml = new StringBuilder();
            if (doctype != null) {
                finalHtml.append("<!DOCTYPE ").append(doctype).append(">\n");
            }
            finalHtml.append(renderedHtml);

            FileUtils.writeStringToFile(outputFileName, finalHtml.toString());
            System.out.println("\nFinished converting! File '" + outputFileName + "' created.");
        } catch (IOException e) {
            System.err.println("\nAn error occurred during the file processing: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("\nAn unexpected error occurred during the conversion: " + e.getMessage());
        }
    }
}
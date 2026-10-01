package com.aidocument.assistant.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.json.JSONObject;
import org.json.JSONArray;

@Service
public class AiService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash-lite:generateContent";

    public String generateSummary(String text) throws Exception {
        String prompt = "Summarize the following document in a clear and concise manner:\n\n" + text;
        return callGeminiApi(prompt);
    }

    public String generateKeyPoints(String text) throws Exception {
        String prompt = "Extract the main key points from the following document. Format them as a bulleted list:\n\n" + text;
        return callGeminiApi(prompt);
    }

    public String generateSimpleExplanation(String text) throws Exception {
        String prompt = "Explain the following document in simple terms, as if explaining to a beginner:\n\n" + text;
        return callGeminiApi(prompt);
    }

    public String generateImportantTopics(String text) throws Exception {
        String prompt = "Identify the most important topics discussed in the following document. List them simply:\n\n" + text;
        return callGeminiApi(prompt);
    }

    private String callGeminiApi(String prompt) throws Exception {
        if (geminiApiKey == null || geminiApiKey.isEmpty() || geminiApiKey.equals("YOUR_API_KEY_HERE")) {
            throw new Exception("Gemini API key is missing or invalid. Please configure it in application.properties.");
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = GEMINI_API_URL + "?key=" + geminiApiKey;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            JSONObject part = new JSONObject();
            part.put("text", prompt);

            JSONArray parts = new JSONArray();
            parts.put(part);

            JSONObject content = new JSONObject();
            content.put("parts", parts);

            JSONArray contents = new JSONArray();
            contents.put(content);

            JSONObject requestBody = new JSONObject();
            requestBody.put("contents", contents);

            HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            
            return extractTextFromResponse(response.getBody());
        } catch (Exception e) {
            throw new Exception("Failed to communicate with AI API: " + e.getMessage());
        }
    }

    private String extractTextFromResponse(String responseBody) {
        JSONObject jsonObject = new JSONObject(responseBody);
        JSONArray candidates = jsonObject.getJSONArray("candidates");
        JSONObject firstCandidate = candidates.getJSONObject(0);
        JSONObject content = firstCandidate.getJSONObject("content");
        JSONArray parts = content.getJSONArray("parts");
        return parts.getJSONObject(0).getString("text");
    }
}

package com.aidocument.assistant.controller;

import com.aidocument.assistant.model.AiResponse;
import com.aidocument.assistant.model.Document;
import com.aidocument.assistant.service.AiService;
import com.aidocument.assistant.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*") // For development purposes
public class AiController {

    @Autowired
    private AiService aiService;

    @Autowired
    private DocumentService documentService;

    @PostMapping("/summarize/{documentId}")
    public ResponseEntity<?> summarize(@PathVariable Long documentId) {
        return processRequest(documentId, "summary");
    }

    @PostMapping("/key-points/{documentId}")
    public ResponseEntity<?> keyPoints(@PathVariable Long documentId) {
        return processRequest(documentId, "keypoints");
    }

    @PostMapping("/explain/{documentId}")
    public ResponseEntity<?> explain(@PathVariable Long documentId) {
        return processRequest(documentId, "explain");
    }

    @PostMapping("/topics/{documentId}")
    public ResponseEntity<?> topics(@PathVariable Long documentId) {
        return processRequest(documentId, "topics");
    }

    private ResponseEntity<?> processRequest(Long documentId, String type) {
        try {
            Document doc = documentService.getDocumentById(documentId);
            String text = doc.getExtractedText();
            String result = "";

            switch (type) {
                case "summary":
                    result = aiService.generateSummary(text);
                    break;
                case "keypoints":
                    result = aiService.generateKeyPoints(text);
                    break;
                case "explain":
                    result = aiService.generateSimpleExplanation(text);
                    break;
                case "topics":
                    result = aiService.generateImportantTopics(text);
                    break;
            }

            return ResponseEntity.ok(new AiResponse(result));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}

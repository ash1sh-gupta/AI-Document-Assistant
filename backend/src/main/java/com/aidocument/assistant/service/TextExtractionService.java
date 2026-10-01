package com.aidocument.assistant.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Service
public class TextExtractionService {

    public String extractText(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new Exception("File name is null");
        }

        String lowerCaseName = fileName.toLowerCase();
        
        if (lowerCaseName.endsWith(".txt")) {
            return extractTextFromTxt(file);
        } else if (lowerCaseName.endsWith(".pdf")) {
            return extractTextFromPdf(file);
        } else if (lowerCaseName.endsWith(".docx")) {
            return extractTextFromDocx(file);
        } else {
            throw new Exception("Unsupported file format: " + fileName);
        }
    }

    private String extractTextFromTxt(MultipartFile file) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }

    private String extractTextFromPdf(MultipartFile file) throws Exception {
        try (PDDocument document = org.apache.pdfbox.Loader.loadPDF(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } catch (Exception e) {
            throw new Exception("Error reading PDF file. It might be corrupted.", e);
        }
    }

    private String extractTextFromDocx(MultipartFile file) throws Exception {
        try (XWPFDocument doc = new XWPFDocument(file.getInputStream());
             XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {
            return extractor.getText();
        } catch (Exception e) {
            throw new Exception("Error reading DOCX file. It might be corrupted.", e);
        }
    }
}

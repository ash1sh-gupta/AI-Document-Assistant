package com.aidocument.assistant.service;

import com.aidocument.assistant.model.Document;
import com.aidocument.assistant.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private TextExtractionService textExtractionService;

    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

    public DocumentService() {
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public Document uploadDocument(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new Exception("Failed to store empty file.");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new Exception("Invalid file name.");
        }

        // Validate size (e.g., max 10MB) - though Spring Boot handles max-file-size via properties
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new Exception("File size exceeds 10MB limit.");
        }

        // Extract text
        String extractedText = textExtractionService.extractText(file);
        
        if (extractedText == null || extractedText.trim().isEmpty()) {
            throw new Exception("Could not extract any readable text from the document.");
        }

        // Save file locally (optional but good for history)
        Path targetLocation = this.fileStorageLocation.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        // Save metadata and text to database
        Document doc = new Document();
        doc.setFileName(fileName);
        doc.setFileType(fileName.substring(fileName.lastIndexOf(".") + 1));
        doc.setFileSize(file.getSize());
        doc.setFilePath(targetLocation.toString());
        doc.setExtractedText(extractedText);

        return documentRepository.save(doc);
    }

    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    public Document getDocumentById(Long id) throws Exception {
        return documentRepository.findById(id).orElseThrow(() -> new Exception("Document not found"));
    }

    public void deleteDocument(Long id) throws Exception {
        Document doc = getDocumentById(id);
        
        try {
            Path fileToDelete = Paths.get(doc.getFilePath());
            Files.deleteIfExists(fileToDelete);
        } catch (Exception e) {
            // Ignore if file is already deleted
        }

        documentRepository.delete(doc);
    }
}

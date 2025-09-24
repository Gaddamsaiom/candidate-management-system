package com.candidatemanagement.util;

import com.candidatemanagement.entity.Candidate;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Component
@Slf4j
public class FileUtil {
    
    private final ObjectMapper objectMapper;
    
    public FileUtil() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }
    
    /**
     * Convert MultipartFile to Base64 string
     */
    public String convertToBase64(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        
        byte[] fileBytes = file.getBytes();
        return Base64.getEncoder().encodeToString(fileBytes);
    }
    
    /**
     * Convert Base64 string to byte array
     */
    public byte[] convertFromBase64(String base64String) {
        if (base64String == null || base64String.isEmpty()) {
            return new byte[0];
        }
        
        try {
            return Base64.getDecoder().decode(base64String);
        } catch (IllegalArgumentException e) {
            log.error("Invalid Base64 string: {}", e.getMessage());
            return new byte[0];
        }
    }
    
    /**
     * Validate file type for resume uploads
     */
    public boolean isValidResumeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        
        String contentType = file.getContentType();
        String filename = file.getOriginalFilename();
        
        if (contentType == null || filename == null) {
            return false;
        }
        
        // Allow PDF, DOC, DOCX files
        return contentType.equals("application/pdf") ||
               contentType.equals("application/msword") ||
               contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ||
               filename.toLowerCase().endsWith(".pdf") ||
               filename.toLowerCase().endsWith(".doc") ||
               filename.toLowerCase().endsWith(".docx");
    }
    
    /**
     * Get file extension from filename
     */
    public String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        
        return filename.substring(lastDotIndex);
    }
    
    /**
     * Convert list of candidates to JSON string
     */
    public String convertCandidatesToJson(List<Candidate> candidates) throws IOException {
        return objectMapper.writeValueAsString(candidates);
    }
    
    /**
     * Convert JSON string to list of candidates
     */
    public List<Candidate> convertJsonToCandidates(String jsonString) throws IOException {
        return objectMapper.readValue(jsonString, new TypeReference<List<Candidate>>() {});
    }
    
    /**
     * Validate JSON format for candidate import
     */
    public boolean isValidCandidateJson(String jsonString) {
        try {
            List<Candidate> candidates = convertJsonToCandidates(jsonString);
            return candidates != null && !candidates.isEmpty();
        } catch (Exception e) {
            log.error("Invalid JSON format: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Generate safe filename for resume download
     */
    public String generateSafeFilename(String candidateName, String originalFilename) {
        String safeName = candidateName.replaceAll("[^a-zA-Z0-9]", "_");
        String extension = getFileExtension(originalFilename);
        return safeName + "_resume" + extension;
    }
    
    /**
     * Get MIME type based on file extension
     */
    public String getMimeType(String filename) {
        if (filename == null) {
            return "application/octet-stream";
        }
        
        String extension = getFileExtension(filename).toLowerCase();
        switch (extension) {
            case ".pdf":
                return "application/pdf";
            case ".doc":
                return "application/msword";
            case ".docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            default:
                return "application/octet-stream";
        }
    }
}

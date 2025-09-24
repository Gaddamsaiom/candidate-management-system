package com.candidatemanagement.service;

import com.candidatemanagement.dto.CandidateResponse;
import com.candidatemanagement.dto.CandidateSubmissionRequest;
import com.candidatemanagement.entity.Candidate;
import com.candidatemanagement.entity.CandidateStatus;
import com.candidatemanagement.exception.CandidateNotFoundException;
import com.candidatemanagement.exception.DuplicateEmailException;
import com.candidatemanagement.repository.CandidateRepository;
import com.candidatemanagement.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CandidateService {
    
    private final CandidateRepository candidateRepository;
    private final FileUtil fileUtil;
    
    /**
     * Submit a new candidate application
     */
    public CandidateResponse submitCandidate(CandidateSubmissionRequest request, MultipartFile resumeFile) throws IOException {
        log.info("Submitting candidate: {}", request.getEmail());
        
        // Check if email already exists
        if (candidateRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("Candidate with email " + request.getEmail() + " already exists");
        }
        
        // Validate resume file if provided
        if (resumeFile != null && !resumeFile.isEmpty() && !fileUtil.isValidResumeFile(resumeFile)) {
            throw new IllegalArgumentException("Invalid resume file format. Only PDF, DOC, and DOCX files are allowed.");
        }
        
        // Build candidate entity
        Candidate candidate = Candidate.builder()
                .role(request.getRole().toUpperCase())
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .qualification(request.getQualification())
                .experience(request.getExperience())
                .skills(request.getSkills())
                .status(CandidateStatus.SUBMITTED)
                .build();
        
        // Handle resume upload
        if (resumeFile != null && !resumeFile.isEmpty()) {
            String base64Resume = fileUtil.convertToBase64(resumeFile);
            candidate.setResume(base64Resume);
            candidate.setResumeFilename(resumeFile.getOriginalFilename());
        }
        
        // Save candidate
        Candidate savedCandidate = candidateRepository.save(candidate);
        log.info("Candidate submitted successfully with ID: {}", savedCandidate.getId());
        
        return convertToResponse(savedCandidate);
    }
    
    /**
     * Update candidate status
     */
    public CandidateResponse updateCandidateStatus(Long candidateId, CandidateStatus newStatus) {
        log.info("Updating candidate {} status to {}", candidateId, newStatus);
        
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new CandidateNotFoundException("Candidate not found with ID: " + candidateId));
        
        candidate.setStatus(newStatus);
        Candidate updatedCandidate = candidateRepository.save(candidate);
        
        log.info("Candidate status updated successfully");
        return convertToResponse(updatedCandidate);
    }
    
    /**
     * Get candidate by ID
     */
    @Transactional(readOnly = true)
    public CandidateResponse getCandidateById(Long candidateId) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new CandidateNotFoundException("Candidate not found with ID: " + candidateId));
        
        return convertToResponse(candidate);
    }
    
    /**
     * Get all candidates
     */
    @Transactional(readOnly = true)
    public List<CandidateResponse> getAllCandidates() {
        return candidateRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Search candidates by criteria
     */
    @Transactional(readOnly = true)
    public List<CandidateResponse> searchCandidates(String role, CandidateStatus status, String searchTerm) {
        List<Candidate> candidates = candidateRepository.findCandidatesByCriteria(role, status, searchTerm);
        return candidates.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get candidates by role
     */
    @Transactional(readOnly = true)
    public List<CandidateResponse> getCandidatesByRole(String role) {
        return candidateRepository.findByRole(role.toUpperCase()).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get candidates by status
     */
    @Transactional(readOnly = true)
    public List<CandidateResponse> getCandidatesByStatus(CandidateStatus status) {
        return candidateRepository.findByStatus(status).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Delete candidate
     */
    public void deleteCandidate(Long candidateId) {
        log.info("Deleting candidate with ID: {}", candidateId);
        
        if (!candidateRepository.existsById(candidateId)) {
            throw new CandidateNotFoundException("Candidate not found with ID: " + candidateId);
        }
        
        candidateRepository.deleteById(candidateId);
        log.info("Candidate deleted successfully");
    }
    
    /**
     * Get candidate resume
     */
    @Transactional(readOnly = true)
    public byte[] getCandidateResume(Long candidateId) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new CandidateNotFoundException("Candidate not found with ID: " + candidateId));
        
        if (candidate.getResume() == null || candidate.getResume().isEmpty()) {
            throw new IllegalArgumentException("No resume found for candidate");
        }
        
        return fileUtil.convertFromBase64(candidate.getResume());
    }
    
    /**
     * Export candidates to JSON
     */
    @Transactional(readOnly = true)
    public String exportCandidatesToJson() throws IOException {
        List<Candidate> candidates = candidateRepository.findAll();
        return fileUtil.convertCandidatesToJson(candidates);
    }
    
    /**
     * Import candidates from JSON
     */
    public List<CandidateResponse> importCandidatesFromJson(String jsonData) throws IOException {
        log.info("Importing candidates from JSON");
        
        if (!fileUtil.isValidCandidateJson(jsonData)) {
            throw new IllegalArgumentException("Invalid JSON format for candidate import");
        }
        
        List<Candidate> candidates = fileUtil.convertJsonToCandidates(jsonData);
        List<Candidate> savedCandidates = candidateRepository.saveAll(candidates);
        
        log.info("Imported {} candidates successfully", savedCandidates.size());
        return savedCandidates.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get candidate statistics
     */
    @Transactional(readOnly = true)
    public CandidateStatistics getCandidateStatistics() {
        long totalCandidates = candidateRepository.count();
        long freshers = candidateRepository.countByRole("FRESHER");
        long experienced = candidateRepository.countByRole("EXPERIENCED");
        
        return CandidateStatistics.builder()
                .totalCandidates(totalCandidates)
                .freshers(freshers)
                .experienced(experienced)
                .submitted(candidateRepository.countByStatus(CandidateStatus.SUBMITTED))
                .underReview(candidateRepository.countByStatus(CandidateStatus.UNDER_REVIEW))
                .shortlisted(candidateRepository.countByStatus(CandidateStatus.SHORTLISTED))
                .selected(candidateRepository.countByStatus(CandidateStatus.SELECTED))
                .rejected(candidateRepository.countByStatus(CandidateStatus.REJECTED))
                .build();
    }
    
    /**
     * Convert Candidate entity to CandidateResponse DTO
     */
    private CandidateResponse convertToResponse(Candidate candidate) {
        return CandidateResponse.builder()
                .id(candidate.getId())
                .role(candidate.getRole())
                .name(candidate.getName())
                .email(candidate.getEmail())
                .phone(candidate.getPhone())
                .qualification(candidate.getQualification())
                .experience(candidate.getExperience())
                .skills(candidate.getSkills())
                .status(candidate.getStatus())
                .resumeFilename(candidate.getResumeFilename())
                .hasResume(candidate.getResume() != null && !candidate.getResume().isEmpty())
                .createdAt(candidate.getCreatedAt())
                .updatedAt(candidate.getUpdatedAt())
                .build();
    }
}

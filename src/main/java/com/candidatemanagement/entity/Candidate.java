package com.candidatemanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "candidates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Candidate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Role is required")
    @Column(nullable = false)
    private String role; // "FRESHER" or "EXPERIENCED"
    
    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;
    
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    @Column(nullable = false, unique = true)
    private String email;
    
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Phone number should be valid")
    @NotBlank(message = "Phone is required")
    @Column(nullable = false)
    private String phone;
    
    @Column(columnDefinition = "TEXT")
    private String qualification; // For freshers
    
    @Column(columnDefinition = "TEXT")
    private String experience; // For experienced candidates
    
    @Column(columnDefinition = "TEXT")
    private String skills;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CandidateStatus status = CandidateStatus.SUBMITTED;
    
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String resume; // Base64 encoded resume
    
    @Column(name = "resume_filename")
    private String resumeFilename;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

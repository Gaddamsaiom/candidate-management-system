package com.candidatemanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateSubmissionRequest {
    
    @NotBlank(message = "Role is required")
    private String role; // "FRESHER" or "EXPERIENCED"
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;
    
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Phone number should be valid")
    @NotBlank(message = "Phone is required")
    private String phone;
    
    private String qualification; // For freshers
    
    private String experience; // For experienced candidates
    
    private String skills;
}

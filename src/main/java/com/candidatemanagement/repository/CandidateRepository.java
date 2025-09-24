package com.candidatemanagement.repository;

import com.candidatemanagement.entity.Candidate;
import com.candidatemanagement.entity.CandidateStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    
    // Find by email
    Optional<Candidate> findByEmail(String email);
    
    // Find by role
    List<Candidate> findByRole(String role);
    
    // Find by status
    List<Candidate> findByStatus(CandidateStatus status);
    
    // Find by role and status
    List<Candidate> findByRoleAndStatus(String role, CandidateStatus status);
    
    // Search candidates by name, email, or skills
    @Query("SELECT c FROM Candidate c WHERE " +
           "LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.skills) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Candidate> searchCandidates(@Param("searchTerm") String searchTerm);
    
    // Search candidates by multiple criteria
    @Query("SELECT c FROM Candidate c WHERE " +
           "(:role IS NULL OR c.role = :role) AND " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:searchTerm IS NULL OR " +
           "LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.skills) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Candidate> findCandidatesByCriteria(
            @Param("role") String role,
            @Param("status") CandidateStatus status,
            @Param("searchTerm") String searchTerm
    );
    
    // Check if email exists
    boolean existsByEmail(String email);
    
    // Count candidates by status
    long countByStatus(CandidateStatus status);
    
    // Count candidates by role
    long countByRole(String role);
}

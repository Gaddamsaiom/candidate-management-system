package com.candidatemanagement.repository;

import com.candidatemanagement.entity.Candidate;
import com.candidatemanagement.entity.CandidateStatus;

import java.util.List;
import java.util.Optional;

/**
 * Storage abstraction for Candidate persistence.
 * Implementations:
 * - JpaCandidateStorage (default)
 * - JsonCandidateStorage (jsondb profile)
 */
public interface CandidateStorage {

    Optional<Candidate> findById(Long id);

    List<Candidate> findAll();

    Candidate save(Candidate candidate);

    List<Candidate> saveAll(List<Candidate> candidates);

    void deleteById(Long id);

    boolean existsById(Long id);

    boolean existsByEmail(String email);

    List<Candidate> findByRole(String role);

    List<Candidate> findByStatus(CandidateStatus status);

    List<Candidate> findByRoleAndStatus(String role, CandidateStatus status);

    List<Candidate> searchCandidates(String searchTerm);

    List<Candidate> findCandidatesByCriteria(String role, CandidateStatus status, String searchTerm);

    long count();

    long countByStatus(CandidateStatus status);

    long countByRole(String role);
}

package com.candidatemanagement.repository;

import com.candidatemanagement.entity.Candidate;
import com.candidatemanagement.entity.CandidateStatus;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("!jsondb")
public class JpaCandidateStorage implements CandidateStorage {

    private final CandidateRepository repo;

    public JpaCandidateStorage(CandidateRepository repo) {
        this.repo = repo;
    }

    @Override
    public Optional<Candidate> findById(Long id) {
        return repo.findById(id);
    }

    @Override
    public List<Candidate> findAll() {
        return repo.findAll();
    }

    @Override
    public Candidate save(Candidate candidate) {
        return repo.save(candidate);
    }

    @Override
    public List<Candidate> saveAll(List<Candidate> candidates) {
        return repo.saveAll(candidates);
    }

    @Override
    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return repo.existsById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repo.findByEmail(email).isPresent();
    }

    @Override
    public List<Candidate> findByRole(String role) {
        return repo.findByRole(role);
    }

    @Override
    public List<Candidate> findByStatus(CandidateStatus status) {
        return repo.findByStatus(status);
    }

    @Override
    public List<Candidate> findByRoleAndStatus(String role, CandidateStatus status) {
        return repo.findByRoleAndStatus(role, status);
    }

    @Override
    public List<Candidate> searchCandidates(String searchTerm) {
        return repo.searchCandidates(searchTerm);
    }

    @Override
    public List<Candidate> findCandidatesByCriteria(String role, CandidateStatus status, String searchTerm) {
        return repo.findCandidatesByCriteria(role, status, searchTerm);
    }

    @Override
    public long count() {
        return repo.count();
    }

    @Override
    public long countByStatus(CandidateStatus status) {
        return repo.countByStatus(status);
    }

    @Override
    public long countByRole(String role) {
        return repo.countByRole(role);
    }
}

package com.candidatemanagement.repository;

import com.candidatemanagement.entity.Candidate;
import com.candidatemanagement.entity.CandidateStatus;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Repository
@Profile("jsondb")
public class JsonCandidateStorage implements CandidateStorage {

    private final Path dbPath;
    private final ObjectMapper mapper;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public JsonCandidateStorage(@Value("${app.jsondb.path:./data/candidates.json}") String jsonDbPath) {
        this.dbPath = Path.of(jsonDbPath);
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        ensureFile();
    }

    private void ensureFile() {
        lock.writeLock().lock();
        try {
            File file = dbPath.toFile();
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            if (!file.exists()) {
                try {
                    Files.writeString(dbPath, "[]");
                } catch (IOException e) {
                    throw new RuntimeException("Failed to initialize JSON DB file: " + dbPath, e);
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    private List<Candidate> readAllInternal() {
        lock.readLock().lock();
        try {
            String json = Files.readString(dbPath);
            List<Candidate> list = mapper.readValue(json, new TypeReference<List<Candidate>>() {});
            return list != null ? list : new ArrayList<>();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read JSON DB file: " + dbPath, e);
        } finally {
            lock.readLock().unlock();
        }
    }

    private void writeAllInternal(List<Candidate> candidates) {
        lock.writeLock().lock();
        try {
            String json = mapper.writeValueAsString(candidates);
            Files.writeString(dbPath, json);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write JSON DB file: " + dbPath, e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private long nextId(List<Candidate> list) {
        return list.stream().map(Candidate::getId).filter(Objects::nonNull).mapToLong(Long::longValue).max().orElse(0L) + 1;
    }

    @Override
    public Optional<Candidate> findById(Long id) {
        return readAllInternal().stream().filter(c -> Objects.equals(c.getId(), id)).findFirst();
    }

    @Override
    public List<Candidate> findAll() {
        return readAllInternal();
    }

    @Override
    public Candidate save(Candidate candidate) {
        lock.writeLock().lock();
        try {
            List<Candidate> list = readAllInternal();
            if (candidate.getId() == null) {
                candidate.setId(nextId(list));
                candidate.setCreatedAt(Optional.ofNullable(candidate.getCreatedAt()).orElse(LocalDateTime.now()));
            }
            candidate.setUpdatedAt(LocalDateTime.now());
            // replace if exists else add
            boolean replaced = false;
            for (int i = 0; i < list.size(); i++) {
                if (Objects.equals(list.get(i).getId(), candidate.getId())) {
                    list.set(i, candidate);
                    replaced = true;
                    break;
                }
            }
            if (!replaced) {
                list.add(candidate);
            }
            writeAllInternal(list);
            return candidate;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public List<Candidate> saveAll(List<Candidate> candidates) {
        lock.writeLock().lock();
        try {
            List<Candidate> list = readAllInternal();
            for (Candidate c : candidates) {
                if (c.getId() == null) {
                    c.setId(nextId(list));
                    c.setCreatedAt(Optional.ofNullable(c.getCreatedAt()).orElse(LocalDateTime.now()));
                }
                c.setUpdatedAt(LocalDateTime.now());
                boolean replaced = false;
                for (int i = 0; i < list.size(); i++) {
                    if (Objects.equals(list.get(i).getId(), c.getId())) {
                        list.set(i, c);
                        replaced = true;
                        break;
                    }
                }
                if (!replaced) {
                    list.add(c);
                }
            }
            writeAllInternal(list);
            return candidates;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void deleteById(Long id) {
        lock.writeLock().lock();
        try {
            List<Candidate> list = readAllInternal();
            List<Candidate> updated = list.stream().filter(c -> !Objects.equals(c.getId(), id)).collect(Collectors.toList());
            writeAllInternal(updated);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }

    @Override
    public boolean existsByEmail(String email) {
        return readAllInternal().stream().anyMatch(c -> email.equalsIgnoreCase(c.getEmail()));
    }

    @Override
    public List<Candidate> findByRole(String role) {
        String r = role == null ? null : role.toUpperCase();
        return readAllInternal().stream().filter(c -> Objects.equals(c.getRole(), r)).collect(Collectors.toList());
    }

    @Override
    public List<Candidate> findByStatus(CandidateStatus status) {
        return readAllInternal().stream().filter(c -> c.getStatus() == status).collect(Collectors.toList());
    }

    @Override
    public List<Candidate> findByRoleAndStatus(String role, CandidateStatus status) {
        String r = role == null ? null : role.toUpperCase();
        return readAllInternal().stream().filter(c -> Objects.equals(c.getRole(), r) && c.getStatus() == status).collect(Collectors.toList());
    }

    @Override
    public List<Candidate> searchCandidates(String searchTerm) {
        if (searchTerm == null || searchTerm.isBlank()) return findAll();
        String q = searchTerm.toLowerCase();
        return readAllInternal().stream().filter(c ->
                (c.getName() != null && c.getName().toLowerCase().contains(q)) ||
                (c.getEmail() != null && c.getEmail().toLowerCase().contains(q)) ||
                (c.getSkills() != null && c.getSkills().toLowerCase().contains(q))
        ).collect(Collectors.toList());
    }

    @Override
    public List<Candidate> findCandidatesByCriteria(String role, CandidateStatus status, String searchTerm) {
        String r = role == null ? null : role.toUpperCase();
        String q = searchTerm == null ? null : searchTerm.toLowerCase();
        return readAllInternal().stream().filter(c ->
                (r == null || Objects.equals(c.getRole(), r)) &&
                (status == null || c.getStatus() == status) &&
                (q == null ||
                        (c.getName() != null && c.getName().toLowerCase().contains(q)) ||
                        (c.getEmail() != null && c.getEmail().toLowerCase().contains(q)) ||
                        (c.getSkills() != null && c.getSkills().toLowerCase().contains(q))
                )
        ).collect(Collectors.toList());
    }

    @Override
    public long count() {
        return readAllInternal().size();
    }

    @Override
    public long countByStatus(CandidateStatus status) {
        return findByStatus(status).size();
    }

    @Override
    public long countByRole(String role) {
        return findByRole(role).size();
    }
}

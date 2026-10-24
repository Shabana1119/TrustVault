
package com.trustvault;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
public class DocumentController {

    private final DocumentRepository documentRepository;
    private final AuditLogRepository auditLogRepository;

    public DocumentController(
            DocumentRepository documentRepository,
            AuditLogRepository auditLogRepository) {
        this.documentRepository = documentRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping("/api/status")
    public Map<String, String> getStatus() {
        return Map.of(
                "application", "TrustVault",
                "status", "running"
        );
    }

    @GetMapping("/api/documents")
    public List<Map<String, String>> getDocuments(
            Authentication authentication) {

        String username = authentication.getName();

        return documentRepository.findByOwner(username)
                .stream()
                .map(document -> {
                    Map<String, String> result =
                            new LinkedHashMap<>();

                    result.put("id",
                            String.valueOf(document.getId()));
                    result.put("name", document.getName());
                    result.put("size",
                            String.valueOf(document.getSize()));

                    return result;
                })
                .toList();
    }

    @PostMapping("/api/documents")
    public Map<String, String> uploadDocument(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Please select a file");
        }

        if (file.getSize() > 5L * 1024 * 1024) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "File must be 5 MB or smaller");
        }

        String originalName = file.getOriginalFilename();

        if (originalName == null || originalName.isBlank()) {
            originalName = "document";
        }

        originalName = originalName.replace('\\', '/');
        originalName = originalName.substring(
                originalName.lastIndexOf('/') + 1);

        if (originalName.isBlank()
                || originalName.equals(".")
                || originalName.equals("..")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid filename");
        }

        Document document = new Document();
        document.setName(originalName);
        document.setOwner(authentication.getName());
        document.setSize(file.getSize());
        document.setFileData(file.getBytes());

        Document savedDocument =
                documentRepository.save(document);

        AuditLog log = new AuditLog();
        log.setUsername(authentication.getName());
        log.setAction("UPLOAD");
        log.setDocumentName(savedDocument.getName());
        log.setTimestamp(LocalDateTime.now());

        auditLogRepository.save(log);

        Map<String, String> result = new LinkedHashMap<>();
        result.put("id",
                String.valueOf(savedDocument.getId()));
        result.put("name", savedDocument.getName());
        result.put("size", String.valueOf(savedDocument.getSize()));

        return result;
    }

    @GetMapping("/api/audit")
    public List<Map<String, String>> getAuditLogs(
            Authentication authentication) {

        return auditLogRepository
                .findAllByOrderByTimestampDesc()
                .stream()
                .map(log -> {
                    Map<String, String> result =
                            new LinkedHashMap<>();

                    result.put("username", log.getUsername());
                    result.put("action", log.getAction());
                    result.put("documentName", log.getDocumentName());
                    result.put("timestamp",
                            log.getTimestamp().toString());

                    return result;
                })
                .toList();
    }
}

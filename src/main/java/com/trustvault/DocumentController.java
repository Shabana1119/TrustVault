
package com.trustvault;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
public class DocumentController {

    private final List<Map<String, String>> documents = new ArrayList<>();
    private final List<Map<String, String>> auditLogs = new ArrayList<>();

    private int nextId = 1;

    @GetMapping("/api/status")
    public Map<String, String> getStatus() {
        return Map.of(
            "application", "TrustVault",
            "status", "running"
        );
    }

    @GetMapping("/api/documents")
    public List<Map<String, String>> getDocuments() {
        return documents;
    }

    @PostMapping("/api/documents")
    public Map<String, String> uploadDocument(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        if (file.isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Please select a file"
            );
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "File must be 5 MB or smaller"
            );
        }

        String originalName = file.getOriginalFilename();

        // Avoid displaying a missing filename.
        if (originalName == null || originalName.isBlank()) {
            originalName = "document";
        }

        Map<String, String> document = new LinkedHashMap<>();
        document.put("id", String.valueOf(nextId++));
        document.put("name", originalName);
        document.put("size", String.valueOf(file.getSize()));

        documents.add(document);

        // Record who uploaded the document and when.
        Map<String, String> log = new LinkedHashMap<>();
        log.put("action", "UPLOAD");
        log.put("documentName", originalName);
        log.put("username", authentication.getName());
        log.put("time", LocalDateTime.now().toString());

        auditLogs.add(log);

        return document;
    }

    @GetMapping("/api/audit")
    public List<Map<String, String>> getAuditLogs() {
        return auditLogs;
    }
}

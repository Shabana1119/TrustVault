
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

    private final List<Map<String, String>> documents =
            new ArrayList<>();

    private final List<Map<String, String>> auditLogs =
            new ArrayList<>();

    private int nextId = 1;

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

        synchronized (documents) {
            return documents.stream()
                    .filter(document ->
                            username.equals(document.get("owner")))
                    .map(document -> {
                        Map<String, String> result =
                                new LinkedHashMap<>(document);
                        result.remove("owner");
                        return result;
                    })
                    .toList();
        }
    }

    @PostMapping("/api/documents")
    public Map<String, String> uploadDocument(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Please select a file"
            );
        }

        if (file.getSize() > 5L * 1024 * 1024) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "File must be 5 MB or smaller"
            );
        }

        String originalName = file.getOriginalFilename();

        if (originalName == null || originalName.isBlank()) {
            originalName = "document";
        }

        // Keep only the filename, not any supplied directory path.
        originalName = originalName.replace('\\', '/');
        originalName = originalName.substring(
                originalName.lastIndexOf('/') + 1
        );

        if (originalName.isBlank()
                || originalName.equals(".")
                || originalName.equals("..")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid filename"
            );
        }

        String username = authentication.getName();
        Map<String, String> document = new LinkedHashMap<>();

        synchronized (documents) {
            document.put("id", String.valueOf(nextId++));
            document.put("name", originalName);
            document.put("size", String.valueOf(file.getSize()));
            document.put("owner", username);

            documents.add(document);
        }

        Map<String, String> log = new LinkedHashMap<>();
        log.put("action", "UPLOAD");
        log.put("documentName", originalName);
        log.put("username", username);
        log.put("time", LocalDateTime.now().toString());

        synchronized (auditLogs) {
            auditLogs.add(log);
        }

        Map<String, String> result =
                new LinkedHashMap<>(document);
        result.remove("owner");

        return result;
    }

    @GetMapping("/api/audit")
    public List<Map<String, String>> getAuditLogs(
            Authentication authentication) {

        String username = authentication.getName();

        synchronized (auditLogs) {
            return auditLogs.stream()
                    .filter(log ->
                            username.equals(log.get("username")))
                    .map(LinkedHashMap::new)
                    .map(log -> (Map<String, String>) log)
                    .toList();
        }
    }
}

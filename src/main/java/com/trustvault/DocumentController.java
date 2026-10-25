
package com.trustvault;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    private static final String DEMO_USER = "demo";

    private final DocumentRepository documentRepository;
    private final AuditLogRepository auditLogRepository;

    public DocumentController(
            DocumentRepository documentRepository,
            AuditLogRepository auditLogRepository) {
        this.documentRepository = documentRepository;
        this.auditLogRepository = auditLogRepository;
    }

    private String getUsername(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated()
                ? authentication.getName()
                : DEMO_USER;
    }

    @GetMapping("/api/status")
    public Map<String, String> getStatus() {
        return Map.of("application", "TrustVault", "status", "running");
    }

    @GetMapping("/api/documents")
    public List<Map<String, String>> getDocuments(
            Authentication authentication) {

        String username = getUsername(authentication);

        return documentRepository.findByOwner(username)
                .stream()
                .map(document -> {
                    Map<String, String> result = new LinkedHashMap<>();
                    result.put("id", String.valueOf(document.getId()));
                    result.put("name", document.getName());
                    result.put("size", String.valueOf(document.getSize()));
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
                    HttpStatus.BAD_REQUEST, "Please select a file");
        }

        if (file.getSize() > 5L * 1024 * 1024) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "File must be 5 MB or smaller");
        }

        String name = file.getOriginalFilename();
        if (name == null || name.isBlank()) {
            name = "document";
        }

        name = name.replace('\\', '/');
        name = name.substring(name.lastIndexOf('/') + 1);

        if (name.isBlank() || name.equals(".") || name.equals("..")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid filename");
        }

        String username = getUsername(authentication);

        Document document = new Document();
        document.setName(name);
        document.setOwner(username);
        document.setSize(file.getSize());
        document.setFileData(file.getBytes());

        Document saved = documentRepository.save(document);
        recordAudit(username, "UPLOAD", saved.getName());

        Map<String, String> result = new LinkedHashMap<>();
        result.put("id", String.valueOf(saved.getId()));
        result.put("name", saved.getName());
        result.put("size", String.valueOf(saved.getSize()));
        return result;
    }

    @GetMapping("/api/documents/{id}/view")
    public ResponseEntity<byte[]> viewDocument(
            @PathVariable Long id,
            Authentication authentication) {

        Document document = getAuthorizedDocument(id, authentication);
        recordAudit(getUsername(authentication), "VIEW", document.getName());

        String fileName = document.getName().toLowerCase();
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;

        if (fileName.endsWith(".png")) {
            mediaType = MediaType.IMAGE_PNG;
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            mediaType = MediaType.IMAGE_JPEG;
        } else if (fileName.endsWith(".gif")) {
            mediaType = MediaType.IMAGE_GIF;
        } else if (fileName.endsWith(".pdf")) {
            mediaType = MediaType.APPLICATION_PDF;
        } else if (fileName.endsWith(".txt")) {
            mediaType = MediaType.TEXT_PLAIN;
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + safeHeaderName(document.getName()) + "\"")
                .body(document.getFileData());
    }

    @GetMapping("/api/documents/{id}/download")
    public ResponseEntity<byte[]> downloadDocument(
            @PathVariable Long id,
            Authentication authentication) {

        Document document = getAuthorizedDocument(id, authentication);
        recordAudit(getUsername(authentication), "DOWNLOAD", document.getName());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + safeHeaderName(document.getName()) + "\"")
                .body(document.getFileData());
    }

    private Document getAuthorizedDocument(
            Long id,
            Authentication authentication) {

        String username = getUsername(authentication);

        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Document not found"));

        if (!username.equals(document.getOwner())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Document not found");
        }

        return document;
    }

    private String safeHeaderName(String name) {
        return name.replace("\"", "")
                .replace("\r", "")
                .replace("\n", "");
    }

    private void recordAudit(
            String username, String action, String documentName) {

        AuditLog log = new AuditLog();
        log.setUsername(username);
        log.setAction(action);
        log.setDocumentName(documentName);
        log.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(log);
    }

    @GetMapping("/api/audit")
    public List<Map<String, String>> getAuditLogs(
            Authentication authentication) {

        String username = getUsername(authentication);

        List<AuditLog> logs = auditLogRepository
                .findByUsernameOrderByTimestampDesc(username);

        return logs.stream()
                .map(log -> {
                    Map<String, String> result = new LinkedHashMap<>();
                    result.put("username", log.getUsername());
                    result.put("action", log.getAction());
                    result.put("documentName", log.getDocumentName());
                    result.put("time", log.getTimestamp().toString());
                    return result;
                })
                .toList();
    }
}

package com.morago_backend.controller;

import com.morago_backend.entity.File;
import com.morago_backend.entity.Theme;
import com.morago_backend.entity.User;
import com.morago_backend.repository.FileRepository;
import com.morago_backend.repository.ThemeRepository;
import com.morago_backend.repository.UserRepository;
import com.morago_backend.service.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/uploads")
@PreAuthorize("hasAnyRole('ADMINISTRATOR','INTERPRETER','CLIENT')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "File Upload/Download - Any roles", description = "APIs for uploading and downloading files, avatars, and documents")
public class UploadController {

    private final StorageService storageService;
    private final ThemeRepository themeRepository;
    private final UserRepository userRepository;
    private final FileRepository fileRepository;

    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);
    
    // Allowed image types for avatars
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );
    
    // Allowed document types
    private static final List<String> ALLOWED_DOCUMENT_TYPES = Arrays.asList(
            "application/pdf", "image/jpeg", "image/jpg", "image/png",
            "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );
    
    // Max file sizes (in bytes)
    private static final long MAX_AVATAR_SIZE = 5 * 1024 * 1024; // 5MB
    private static final long MAX_DOCUMENT_SIZE = 10 * 1024 * 1024; // 10MB

    public UploadController(StorageService storageService, ThemeRepository themeRepository,
                            UserRepository userRepository, FileRepository fileRepository) {
        this.storageService = storageService;
        this.themeRepository = themeRepository;
        this.userRepository = userRepository;
        this.fileRepository = fileRepository;
    }

    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }
        if (file.getSize() > MAX_AVATAR_SIZE) {
            throw new RuntimeException("File size exceeds maximum limit of 5MB");
        }
        if (!ALLOWED_IMAGE_TYPES.contains(file.getContentType())) {
            throw new RuntimeException("Invalid file type. Only JPEG, PNG, GIF, and WebP images are allowed");
        }
    }

    private void validateDocumentFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }
        if (file.getSize() > MAX_DOCUMENT_SIZE) {
            throw new RuntimeException("File size exceeds maximum limit of 10MB");
        }
        if (!ALLOWED_DOCUMENT_TYPES.contains(file.getContentType())) {
            throw new RuntimeException("Invalid file type. Only PDF, DOC, DOCX, JPEG, and PNG documents are allowed");
        }
    }

    // ========== UPLOAD THEME ICON ==========
    @Operation(summary = "Upload theme icon")
    @PostMapping("/themes/{themeId}/icon")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','INTERPRETER')")
    public ResponseEntity<String> uploadThemeIcon(@PathVariable Long themeId,
                                                  @RequestParam("file") MultipartFile file) {
        try {
            logger.info("Uploading icon for theme id={} with filename={}", themeId, file.getOriginalFilename());
            Theme theme = themeRepository.findById(themeId)
                    .orElseThrow(() -> new RuntimeException("Theme not found"));

            String url = storageService.upload("theme-icons", file.getOriginalFilename(), file);
            File record = new File();
            record.setOriginalTitle(file.getOriginalFilename());
            record.setPath(url);
            record.setType(file.getContentType());
            record.setTheme(theme);
            File saved = fileRepository.save(record);

            theme.setIconId(saved.getId());
            themeRepository.save(theme);

            return ResponseEntity.created(URI.create(url)).body(url);
        } catch (Exception ex) {
            logger.error("Error uploading theme icon for themeId={}: {}", themeId, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ========== UPLOAD USER AVATAR ==========
    @Operation(summary = "Upload user avatar", description = "Upload avatar for any user (Clients, Interpreters, Admins)")
    @PostMapping("/users/{userId}/avatar")
    public ResponseEntity<String> uploadUserAvatar(@PathVariable Long userId,
                                                   @RequestParam("file") MultipartFile file) {
        try {
            logger.info("Uploading avatar for user id={} with filename={}", userId, file.getOriginalFilename());
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String url = storageService.upload("avatars", file.getOriginalFilename(), file);
            File saved = new File();
            saved.setOriginalTitle(file.getOriginalFilename());
            saved.setPath(url);
            saved.setType(file.getContentType());
            saved = fileRepository.save(saved);

            user.setImageId(saved.getId());
            userRepository.save(user);

            return ResponseEntity.created(URI.create(url)).body(url);
        } catch (Exception ex) {
            logger.error("Error uploading avatar for userId={}: {}", userId, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ========== UPLOAD CERTIFICATE ==========
    @Operation(summary = "Upload interpreter certificate")
    @PostMapping("/users/{userId}/certificate")
    public ResponseEntity<String> uploadCertificate(@PathVariable Long userId,
                                                    @RequestParam("file") MultipartFile file) {
        try {
            logger.info("Uploading certificate for user id={} with filename={}", userId, file.getOriginalFilename());
            userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String url = storageService.upload("certificates", file.getOriginalFilename(), file);
            File saved = new File();
            saved.setOriginalTitle(file.getOriginalFilename());
            saved.setPath(url);
            saved.setType(file.getContentType());
            fileRepository.save(saved);

            return ResponseEntity.created(URI.create(url)).body(url);
        } catch (Exception ex) {
            logger.error("Error uploading certificate for userId={}: {}", userId, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Download file by ID", description = "Download any file by its database ID")
    @GetMapping("/files/{fileId}/download")
    public ResponseEntity<Resource> downloadFileById(@PathVariable Long fileId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        Resource resource = storageService.download(file.getPath());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getOriginalTitle() + "\"")
                .body(resource);
    }

    @Operation(summary = "Download file by path", description = "Download file using its storage path")
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFileByPath(@RequestParam("path") String path) {
        if (!storageService.exists(path)) {
            throw new RuntimeException("File not found");
        }

        Resource resource = storageService.download(path);

        // Try to find file metadata
        String filename = path.substring(path.lastIndexOf("/") + 1);
        String contentType = "application/octet-stream";

        fileRepository.findAll().stream()
                .filter(f -> f.getPath().equals(path))
                .findFirst()
                .ifPresent(f -> {
                    // This is a local variable capture, we'd need to handle it differently
                });

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }

    @Operation(summary = "Delete uploaded file", description = "Delete a file from storage (Admin only)")
    @DeleteMapping("/files/{fileId}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<Void> deleteFile(@PathVariable Long fileId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        storageService.delete(file.getPath());
        fileRepository.delete(file);

        return ResponseEntity.noContent().build();
    }
}

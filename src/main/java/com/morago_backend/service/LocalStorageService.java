package com.morago_backend.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@Profile({"local", "dev", "railway"})
public class LocalStorageService implements StorageService {

    @Value("${storage.local.base-dir:uploads}")
    private String baseDir;

    @Override
    public String upload(String folder, String filename, MultipartFile file) {
        try {
            String cleanedName = StringUtils.cleanPath(filename);
            // Add UUID to prevent filename conflicts
            String uniqueFilename = UUID.randomUUID() + "-" + cleanedName;
            Path targetDir = Paths.get(baseDir, folder).toAbsolutePath().normalize();
            Files.createDirectories(targetDir);
            Path targetFile = targetDir.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), targetFile);
            return "/" + baseDir + "/" + folder + "/" + uniqueFilename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    @Override
    public void delete(String path) {
        try {
            if (path == null) return;
            Path targetFile = Paths.get(path.startsWith("/") ? path.substring(1) : path);
            if (Files.exists(targetFile)) {
                Files.delete(targetFile);
            }
        } catch (IOException e) {
            // ignore delete failures for now
        }
    }

    @Override
    public Resource download(String path) {
        try {
            Path filePath = Paths.get(path.startsWith("/") ? path.substring(1) : path).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("File not found or not readable: " + path);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error downloading file: " + path, e);
        }
    }

    @Override
    public boolean exists(String path) {
        if (path == null) return false;
        Path filePath = Paths.get(path.startsWith("/") ? path.substring(1) : path);
        return Files.exists(filePath);
    }
}



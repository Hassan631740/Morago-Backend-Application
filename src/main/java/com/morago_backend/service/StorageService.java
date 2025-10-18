package com.morago_backend.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String upload(String folder, String filename, MultipartFile file);
    void delete(String path);
    Resource download(String path);
    boolean exists(String path);
}



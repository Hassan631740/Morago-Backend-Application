package com.morago_backend.service;

import com.morago_backend.dto.dtoRequest.FileRequestDTO;
import com.morago_backend.dto.dtoResponse.FileResponseDTO;
import com.morago_backend.entity.File;
import com.morago_backend.entity.Theme;
import com.morago_backend.exception.ResourceNotFoundException;
import com.morago_backend.repository.FileRepository;
import com.corundumstudio.socketio.SocketIOServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

    private final FileRepository fileRepository;
    private final SocketIOServer socketServer;
    private final StorageService storageService;

    public FileService(FileRepository fileRepository,
                       SocketIOServer socketServer,
                       StorageService storageService) {
        this.fileRepository = fileRepository;
        this.socketServer = socketServer;
        this.storageService = storageService;
    }

    // Helper: entity -> DTO
    private FileResponseDTO toDTO(File file) {
        FileResponseDTO dto = new FileResponseDTO();
        dto.setId(file.getId());
        dto.setOriginalTitle(file.getOriginalTitle());
        dto.setPath(file.getPath());
        dto.setType(file.getType());
        dto.setThemeId(file.getTheme() != null ? file.getTheme().getId() : null);
        dto.setCreatedAt(file.getCreatedAtDatetime());
        dto.setUpdatedAt(file.getUpdatedAtDatetime());
        return dto;
    }

    // Helper: DTO -> entity
    private File fromDTO(FileRequestDTO dto) {
        File file = new File();
        file.setOriginalTitle(dto.getOriginalTitle());
        file.setType(dto.getType());
        if (dto.getThemeId() != null) {
            Theme theme = new Theme();
            theme.setId(dto.getThemeId());
            file.setTheme(theme);
        }
        return file;
    }

    // ====== CREATE ======
    public FileResponseDTO create(FileRequestDTO dto) {
        try {
            logger.info("Creating new File with title={}", dto.getOriginalTitle());
            File file = fromDTO(dto);

            MultipartFile uploadedFile = dto.getFile();
            if (uploadedFile != null && !uploadedFile.isEmpty()) {
                String path = storageService.upload("files", uploadedFile.getOriginalFilename(), uploadedFile);
                file.setPath(path);
            }

            File saved = fileRepository.save(file);
            socketServer.getBroadcastOperations().sendEvent("fileCreated", saved);
            logger.info("File created with id={}", saved.getId());
            return toDTO(saved);
        } catch (Exception e) {
            logger.error("Error creating File with title={}", dto.getOriginalTitle(), e);
            throw e;
        }
    }

    // ====== READ ALL ======
    public List<FileResponseDTO> findAll() {
        try {
            logger.info("Fetching all Files");
            return fileRepository.findAll().stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching all Files", e);
            throw e;
        }
    }

    // ====== READ BY ID ======
    public Optional<FileResponseDTO> findById(Long id) {
        try {
            logger.info("Fetching File by id={}", id);
            return fileRepository.findById(id).map(this::toDTO);
        } catch (Exception e) {
            logger.error("Error fetching File with id={}", id, e);
            throw e;
        }
    }

    // ====== UPDATE ======
    public FileResponseDTO update(Long id, FileRequestDTO dto) {
        try {
            logger.info("Updating File with id={}", id);
            File existing = fileRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("File not found with id " + id));

            existing.setOriginalTitle(dto.getOriginalTitle());
            existing.setType(dto.getType());
            if (dto.getThemeId() != null) {
                Theme theme = new Theme();
                theme.setId(dto.getThemeId());
                existing.setTheme(theme);
            } else {
                existing.setTheme(null);
            }

            MultipartFile newFile = dto.getFile();
            if (newFile != null && !newFile.isEmpty()) {
                if (existing.getPath() != null) {
                    storageService.delete(existing.getPath());
                }
                String path = storageService.upload("files", newFile.getOriginalFilename(), newFile);
                existing.setPath(path);
            }

            File saved = fileRepository.save(existing);
            socketServer.getBroadcastOperations().sendEvent("fileUpdated", saved);
            logger.info("File updated with id={}", saved.getId());
            return toDTO(saved);
        } catch (Exception e) {
            logger.error("Error updating File with id={}", id, e);
            throw e;
        }
    }

    // ====== DELETE ======
    public void delete(Long id) {
        try {
            logger.info("Deleting File with id={}", id);
            File existing = fileRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("File not found with id " + id));

            if (existing.getPath() != null) {
                storageService.delete(existing.getPath());
            }

            fileRepository.deleteById(id);
            socketServer.getBroadcastOperations().sendEvent("fileDeleted", id);
            logger.info("File deleted with id={}", id);
        } catch (Exception e) {
            logger.error("Error deleting File with id={}", id, e);
            throw e;
        }
    }
}

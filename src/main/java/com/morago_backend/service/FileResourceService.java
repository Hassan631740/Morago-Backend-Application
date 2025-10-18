package com.morago_backend.service;

import com.morago_backend.entity.FileResource;
import com.morago_backend.repository.FileResourceRepository;
import com.corundumstudio.socketio.SocketIOServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FileResourceService {

    private static final Logger logger = LoggerFactory.getLogger(FileResourceService.class);

    private final FileResourceRepository repository;
    private final SocketIOServer socketServer;

    public FileResourceService(FileResourceRepository repository, SocketIOServer socketServer) {
        this.repository = repository;
        this.socketServer = socketServer;
    }

    // ====== CREATE ======
    public FileResource create(FileResource entity) {
        try {
            logger.info("Creating new FileResource with path={}", entity.getPath());
            FileResource saved = repository.save(entity);
            socketServer.getBroadcastOperations().sendEvent("fileResourceCreated", saved);
            logger.info("FileResource created with id={}", saved.getId());
            return saved;
        } catch (Exception e) {
            logger.error("Error creating FileResource with path={}", entity.getPath(), e);
            throw e;
        }
    }

    // ====== READ ALL ======
    public List<FileResource> findAll() {
        try {
            logger.info("Fetching all FileResources");
            return repository.findAll();
        } catch (Exception e) {
            logger.error("Error fetching all FileResources", e);
            throw e;
        }
    }

    // ====== READ BY ID ======
    public Optional<FileResource> findById(Long id) {
        try {
            logger.info("Fetching FileResource by id={}", id);
            return repository.findById(id);
        } catch (Exception e) {
            logger.error("Error fetching FileResource with id={}", id, e);
            throw e;
        }
    }

    // ====== UPDATE ======
    public FileResource update(Long id, FileResource updated) {
        try {
            logger.info("Updating FileResource with id={}", id);
            FileResource existing = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("FileResource not found with id " + id));

            existing.setOriginType(updated.getOriginType());
            existing.setPath(updated.getPath());
            existing.setType(updated.getType());

            FileResource saved = repository.save(existing);
            socketServer.getBroadcastOperations().sendEvent("fileResourceUpdated", saved);
            logger.info("FileResource updated with id={}", saved.getId());
            return saved;
        } catch (Exception e) {
            logger.error("Error updating FileResource with id={}", id, e);
            throw e;
        }
    }

    // ====== DELETE ======
    public void delete(Long id) {
        try {
            logger.info("Deleting FileResource with id={}", id);
            repository.deleteById(id);
            socketServer.getBroadcastOperations().sendEvent("fileResourceDeleted", id);
            logger.info("FileResource deleted with id={}", id);
        } catch (Exception e) {
            logger.error("Error deleting FileResource with id={}", id, e);
            throw e;
        }
    }
}

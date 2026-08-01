package com.raffs.LawInsight.service.impl;

import com.raffs.LawInsight.exception.FileStorageException;
import com.raffs.LawInsight.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
public class LocalFileStorageService implements FileStorageService {

    private final Path fileStorageLocation;
    private final long maxFileSize;

    public LocalFileStorageService(
            @Value("${app.storage.location:uploads/contracts}") String uploadDir,
            @Value("${app.storage.max-file-size:10485760}") long maxFileSize) {
        this.maxFileSize = maxFileSize;
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileStorageException("Failed to store empty file.");
        }

        if (file.getSize() > maxFileSize) {
            throw new FileStorageException("File size exceeds maximum allowed size of " + maxFileSize + " bytes.");
        }

        String originalFileName = StringUtils.cleanPath(Objects.requireNonNullElse(file.getOriginalFilename(), "file"));

        if (originalFileName.contains("..")) {
            throw new FileStorageException("Sorry! Filename contains invalid relative path sequence " + originalFileName);
        }

        String storedFileName = UUID.randomUUID() + "_" + originalFileName;

        try {
            Path targetLocation = this.fileStorageLocation.resolve(storedFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return targetLocation.toString();
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + originalFileName + ". Please try again!", ex);
        }
    }

    @Override
    public Resource loadFileAsResource(String filePath) {
        try {
            Path path = Paths.get(filePath).normalize();
            if (!path.startsWith(this.fileStorageLocation)) {
                throw new FileStorageException("Access denied: File path outside storage location.");
            }
            Resource resource = new UrlResource(path.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new FileStorageException("File not found at path: " + filePath);
            }
        } catch (MalformedURLException ex) {
            throw new FileStorageException("File not found at path: " + filePath, ex);
        }
    }

    @Override
    public void deleteFile(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            return;
        }
        try {
            Path path = Paths.get(filePath).normalize();
            if (!path.startsWith(this.fileStorageLocation)) {
                throw new FileStorageException("Access denied: File path outside storage location.");
            }
            Files.deleteIfExists(path);
        } catch (IOException ex) {
            throw new FileStorageException("Could not delete file at path: " + filePath, ex);
        }
    }
}

package com.raffs.LawInsight.service;

import com.raffs.LawInsight.exception.FileStorageException;
import com.raffs.LawInsight.service.impl.LocalFileStorageService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileStorageServiceTest {

    @TempDir
    Path tempDir;

    private LocalFileStorageService fileStorageService;

    @BeforeEach
    void setUp() {
        fileStorageService = new LocalFileStorageService(tempDir.toString(), 1024 * 1024L); // 1MB limit for tests
    }

    @Test
    void shouldStoreFileSuccessfully() throws IOException {
        var file = new MockMultipartFile("file", "contract.pdf", "application/pdf", "Dummy PDF content".getBytes());

        var storedPath = fileStorageService.storeFile(file);

        assertThat(storedPath).isNotNull();
        assertThat(Files.exists(Path.of(storedPath))).isTrue();
        assertThat(Files.readString(Path.of(storedPath))).isEqualTo("Dummy PDF content");
    }

    @Test
    void shouldRejectEmptyFile() {
        var emptyFile = new MockMultipartFile("file", "empty.pdf", "application/pdf", new byte[0]);

        assertThatThrownBy(() -> fileStorageService.storeFile(emptyFile))
                .isInstanceOf(FileStorageException.class)
                .hasMessageContaining("Failed to store empty file");
    }

    @Test
    void shouldRejectPathTraversalFilename() {
        var invalidFile = new MockMultipartFile("file", "../secret.txt", "text/plain", "content".getBytes());

        assertThatThrownBy(() -> fileStorageService.storeFile(invalidFile))
                .isInstanceOf(FileStorageException.class)
                .hasMessageContaining("invalid relative path");
    }

    @Test
    void shouldRejectFileExceedingMaxSize() {
        byte[] largeContent = new byte[2 * 1024 * 1024]; // 2MB > 1MB max
        var largeFile = new MockMultipartFile("file", "large.pdf", "application/pdf", largeContent);

        assertThatThrownBy(() -> fileStorageService.storeFile(largeFile))
                .isInstanceOf(FileStorageException.class)
                .hasMessageContaining("exceeds maximum allowed size");
    }

    @Test
    void shouldLoadFileAsResource() throws IOException {
        var file = new MockMultipartFile("file", "sample.txt", "text/plain", "Sample data".getBytes());
        var storedPath = fileStorageService.storeFile(file);

        var resource = fileStorageService.loadFileAsResource(storedPath);

        assertThat(resource).isNotNull();
        assertThat(resource.exists()).isTrue();
        assertThat(resource.isReadable()).isTrue();
    }

    @Test
    void shouldThrowExceptionWhenLoadingPathTraversalFile() {
        var outsidePath = tempDir.getParent().resolve("outside.pdf").toString();

        assertThatThrownBy(() -> fileStorageService.loadFileAsResource(outsidePath))
                .isInstanceOf(FileStorageException.class)
                .hasMessageContaining("Access denied");
    }

    @Test
    void shouldThrowExceptionWhenDeletingPathTraversalFile() {
        var outsidePath = tempDir.getParent().resolve("outside.pdf").toString();

        assertThatThrownBy(() -> fileStorageService.deleteFile(outsidePath))
                .isInstanceOf(FileStorageException.class)
                .hasMessageContaining("Access denied");
    }

    @Test
    void shouldDeleteFileSuccessfully() throws IOException {
        var file = new MockMultipartFile("file", "to_delete.txt", "text/plain", "Delete me".getBytes());
        var storedPath = fileStorageService.storeFile(file);

        fileStorageService.deleteFile(storedPath);

        assertThat(Files.exists(Path.of(storedPath))).isFalse();
    }
}

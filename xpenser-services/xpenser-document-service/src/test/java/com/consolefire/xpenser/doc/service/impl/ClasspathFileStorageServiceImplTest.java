package com.consolefire.xpenser.doc.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.consolefire.xpenser.doc.config.props.FileStoreProperties;
import com.consolefire.xpenser.doc.data.dto.FileMetadataDto;
import com.consolefire.xpenser.doc.exception.FileStorageException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

class ClasspathFileStorageServiceImplTest {

    private ClasspathFileStorageServiceImpl fileStorageService;
    private FileStoreProperties fileStoreProperties;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        fileStoreProperties = new FileStoreProperties();
        fileStoreProperties.setLocation(tempDir.toString());

        // Create some dummy files in the temporary directory
        Files.createFile(tempDir.resolve("test1.txt"));
        Files.createFile(tempDir.resolve("test2.txt"));
        Files.createDirectory(tempDir.resolve("subdir"));

        fileStorageService = new ClasspathFileStorageServiceImpl(fileStoreProperties);
    }


    @Test
    void testStoreMultipartFile_Success() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);
        String filename = "upload.txt";

        when(mockFile.getOriginalFilename()).thenReturn(filename);
        when(mockFile.getInputStream()).thenReturn(new java.io.ByteArrayInputStream("test data".getBytes()));

        FileMetadataDto metadata = fileStorageService.store(mockFile);

        assertNotNull(metadata);
        assertEquals("/files/" + filename, metadata.getFileUrl());
        assertEquals(tempDir.resolve(filename), metadata.getPath());
        assertNotNull(metadata.getFileChecksum());

        assertTrue(Files.exists(tempDir.resolve(filename)));
        assertEquals("test data".getBytes().length, Files.size(tempDir.resolve(filename)));
    }

    @Test
    void testStoreMultipartFile_IOException() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("fail.txt");
        when(mockFile.getInputStream()).thenThrow(new IOException("Simulated IO error"));

        assertThrows(FileStorageException.class, () -> fileStorageService.store(mockFile));
    }


    @Test
    void testLoadPaths_Success() throws IOException {
        try (Stream<Path> paths = fileStorageService.loadPaths()) {
            // Should only list the two files, not the subdirectory
            assertEquals(2, paths.count());
        }
    }

    @Test
    void testLoadPaths_EmptyDirectory() throws IOException {
        // Use a new service instance with an empty directory
        Path emptyDir = tempDir.resolve("empty");
        Files.createDirectory(emptyDir);
        FileStoreProperties emptyProps = new FileStoreProperties();
        emptyProps.setLocation(emptyDir.toString());
        ClasspathFileStorageServiceImpl emptyService = new ClasspathFileStorageServiceImpl(emptyProps);

        try (Stream<Path> paths = emptyService.loadPaths()) {
            assertEquals(0, paths.count());
        }
    }


    @Test
    void testLoadPath_Success() {
        Path loadedPath = fileStorageService.loadPath("test1.txt");
        assertNotNull(loadedPath);
        assertEquals(tempDir.resolve("test1.txt"), loadedPath);
    }

    @Test
    void testLoadPath_FileDoesNotExist() {
        assertThrows(FileStorageException.class, () -> fileStorageService.loadPath("nonexistent.txt"));
    }

    @Test
    void testLoadPath_FileIsDirectory() {
        // Should not be able to load a directory
        assertThrows(FileStorageException.class, () -> fileStorageService.loadPath("subdir"));
    }


    @Test
    void testLoadAsResource_Success() {
        Resource resource = fileStorageService.loadAsResource("test1.txt");
        assertNotNull(resource);
        assertTrue(resource.exists());
        assertTrue(resource.isReadable());
        assertEquals("test1.txt", resource.getFilename());
    }

    @Test
    void testLoadAsResource_FileNotFound() {
        assertThrows(FileStorageException.class, () -> fileStorageService.loadAsResource("nonexistent.txt"));
    }


    @Test
    void testPathTraversalProtection() {
        // Attempt to access a file outside the root directory using ".."
        String maliciousFilename = "../sensitive/data.txt";

        // This should throw an exception due to the security check
        assertThrows(FileStorageException.class, () -> fileStorageService.loadPath(maliciousFilename));
    }
}
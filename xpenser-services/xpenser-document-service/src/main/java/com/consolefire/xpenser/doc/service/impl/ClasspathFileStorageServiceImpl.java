package com.consolefire.xpenser.doc.service.impl;

import com.consolefire.xpenser.doc.config.props.FileStoreProperties;
import com.consolefire.xpenser.doc.data.dto.FileMetadataDto;
import com.consolefire.xpenser.doc.exception.FileStorageException;
import com.consolefire.xpenser.doc.service.FileStorageService;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Stream;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ClasspathFileStorageServiceImpl implements FileStorageService {

    private final Path fileStoreRootPath;

    public ClasspathFileStorageServiceImpl(FileStoreProperties fileStoreProperties) {
        this.fileStoreRootPath = Path.of(fileStoreProperties.getLocation());
        if (!Files.exists(fileStoreRootPath) || !Files.isDirectory(fileStoreRootPath)) {
            throw new FileStorageException("Root folder [ "
                + fileStoreProperties.getLocation()
                + " ] does not exists or is not a directory.");
        }
    }

    @Override
    public FileMetadataDto store(File file) {
        try (InputStream inputStream = Files.newInputStream(file.toPath())) {
            return store(file.getName(), inputStream);
        } catch (IOException e) {
            throw new FileStorageException("Failed to store file: " + file.getName(), e);
        }
    }

    @Override
    public FileMetadataDto store(String fileName, byte[] content) {
        try (InputStream inputStream = new java.io.ByteArrayInputStream(content)) {
            return store(fileName, inputStream);
        } catch (IOException e) {
            throw new FileStorageException("Failed to store file: " + fileName, e);
        }
    }

    @Override
    public FileMetadataDto store(MultipartFile file) {
        String filename = file.getOriginalFilename();
        try (InputStream inputStream = file.getInputStream()) {
            return store(filename, inputStream);
        } catch (IOException e) {
            throw new FileStorageException("Failed to store multipart file: " + filename, e);
        }
    }

    @Override
    public Stream<Path> loadPaths() {
        try {
            return Files.list(this.fileStoreRootPath)
                .filter(Files::isRegularFile);
        } catch (IOException e) {
            throw new FileStorageException("Failed to read stored files", e);
        }
    }

    @Override
    public Path loadPath(String filename) {
        Path path = this.fileStoreRootPath.resolve(filename);
        try {
            // Security check to prevent path traversal
            if (!path.toAbsolutePath().normalize().startsWith(this.fileStoreRootPath.toAbsolutePath().normalize())) {
                throw new FileStorageException("Cannot load file outside of the storage directory.");
            }
            if (Files.exists(path) && Files.isRegularFile(path)) {
                return path;
            } else {
                throw new FileStorageException("File not found: " + filename);
            }
        } catch (Exception e) {
            throw new FileStorageException("File not found: " + filename, e);
        }
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = loadPath(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new FileStorageException("Could not read file: " + filename);
            }
        } catch (FileStorageException | MalformedURLException e) {
            throw new FileStorageException("Could not read file: " + filename, e);
        }
    }

    // --- Private Helper Methods ---

    /**
     * Private helper method to handle the actual file storage logic.
     */
    private FileMetadataDto store(String filename, InputStream inputStream) throws IOException {
        Path destinationPath = this.fileStoreRootPath.resolve(filename).normalize();

        Files.copy(inputStream, destinationPath, StandardCopyOption.REPLACE_EXISTING);

        String fileUrl = "/files/" + filename;
        String checksum = calculateChecksum(destinationPath);

        return FileMetadataDto.builder()
            .fileUrl(fileUrl)
            .fileChecksum(checksum)
            .path(destinationPath)
            .build();
    }

    /**
     * Calculates the SHA-256 checksum of a file and converts it to a hex string without using
     * javax.xml.bind.DatatypeConverter.
     */
    private String calculateChecksum(Path path) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] fileBytes = Files.readAllBytes(path);
            byte[] hashBytes = digest.digest(fileBytes);
            return toHexString(hashBytes);
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new FileStorageException("Failed to calculate checksum for file: " + path.getFileName(), e);
        }
    }

    /**
     * Converts a byte array to a hexadecimal string.
     */
    private String toHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
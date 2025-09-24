package com.consolefire.xpenser.doc.service;

import com.consolefire.xpenser.doc.data.dto.FileMetadataDto;
import java.io.File;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    FileMetadataDto store(File file);

    FileMetadataDto store(String fileName, byte[] content);

    FileMetadataDto store(MultipartFile file);

    Stream<Path> loadPaths();

    Path loadPath(String filename);

    Resource loadAsResource(String filename);

}

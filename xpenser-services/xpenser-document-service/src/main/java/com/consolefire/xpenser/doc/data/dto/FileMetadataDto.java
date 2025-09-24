package com.consolefire.xpenser.doc.data.dto;

import java.nio.file.Path;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FileMetadataDto {

    private String fileUrl;
    private String fileChecksum;
    private Path path;
}

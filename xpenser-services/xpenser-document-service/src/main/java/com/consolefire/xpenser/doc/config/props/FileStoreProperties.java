package com.consolefire.xpenser.doc.config.props;

import com.consolefire.xpenser.doc.util.FileSize;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "common.infra.file-store")
public class FileStoreProperties {

    private String location;
    private FileSize maxFileSize;

}

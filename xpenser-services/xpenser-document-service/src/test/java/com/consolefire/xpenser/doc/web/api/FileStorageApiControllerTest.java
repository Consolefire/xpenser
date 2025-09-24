package com.consolefire.xpenser.doc.web.api;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.consolefire.xpenser.doc.exception.FileStorageException;
import com.consolefire.xpenser.doc.service.FileStorageService;
import java.nio.file.Paths;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@Disabled
@ExtendWith(MockitoExtension.class)
class FileStorageApiControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private FileStorageApiController fileStorageApiController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(fileStorageApiController).build();
    }

    //-------------------------------------------------------------------------
    // listFiles() Tests
    //-------------------------------------------------------------------------

    @Test
    void testListFiles_ReturnsListOfFilenames() throws Exception {
        // Mock the service to return a stream of paths
        when(fileStorageService.loadPaths())
            .thenReturn(Stream.of(
                Paths.get("data/test1.txt"),
                Paths.get("data/image.jpg")
            ));

        mockMvc.perform(get("/files"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0]").value("test1.txt"))
            .andExpect(jsonPath("$[1]").value("image.jpg"));
    }

    //-------------------------------------------------------------------------
    // downloadFile() Tests
    //-------------------------------------------------------------------------

    @Test
    void testDownloadFile_Success() throws Exception {
        String filename = "document.pdf";
        byte[] fileContent = "This is a test file.".getBytes();

        // Mock a Resource object for the service to return
        Resource mockResource = new ByteArrayResource(fileContent) {
            @Override
            public String getFilename() {
                return filename;
            }
        };

        when(fileStorageService.loadAsResource(filename)).thenReturn(mockResource);

        mockMvc.perform(get("/files/{filename}", filename))
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\""))
            .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
            .andExpect(content().bytes(fileContent));
    }

    @Test
    void testDownloadFile_FileNotFound() throws Exception {
        String filename = "nonexistent.txt";

        // Mock the service to throw a FileStorageException
        when(fileStorageService.loadAsResource(filename))
            .thenThrow(new FileStorageException("File not found: " + filename));

        mockMvc.perform(get("/files/{filename}", filename))
            .andExpect(status().isInternalServerError()); // Default behavior for uncaught runtime exception
    }
}
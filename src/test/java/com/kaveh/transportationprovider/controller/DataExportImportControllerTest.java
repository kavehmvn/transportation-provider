package com.kaveh.transportationprovider.controller;

import com.kaveh.transportationprovider.service.DataExportImportService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(value = DataExportImportController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DataExportImportControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DataExportImportService dataExportImportService;

    public Path testDirectory;

    @BeforeEach
    public void setUp() {
        this.testDirectory = Path.of("", "src/test/resources");
    }
    @Test
    @Order(1)
    void testUpload() throws Exception {
        Path file = this.testDirectory.resolve("testImport.csv");
        MockMultipartFile importFile = makeImportMultipartFile(file);
        mockMvc.perform(multipart("/api/v1/data/containers/upload").file(importFile))
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    void testDownload() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/data/containers/download")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)).andExpect(MockMvcResultMatchers.status().is(200)).andReturn();

    }
    private MockMultipartFile makeImportMultipartFile(Path file) throws IOException {
        MockMultipartFile importFile
                = new MockMultipartFile(
                "file",
                "testImport.csv",
                MediaType.TEXT_PLAIN_VALUE,
                Files.readAllBytes(file)
        );
        return importFile;
    }
}

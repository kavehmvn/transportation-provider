package com.kaveh.transportationprovider.service;

import com.kaveh.transportationprovider.model.Good;
import com.kaveh.transportationprovider.model.TransportationContainer;
import com.kaveh.transportationprovider.model.dto.ContainerInputDto;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DataExportImportServiceTest {

    @TempDir
    public Path testDirectory;

    @InjectMocks
    private DataExportImportService service;

    @Mock
    private TransportationContainerService containerService;


    private Path workingDir;

    @BeforeEach
    public void setUp() {
        TransportationContainer testContainer = getSampleContainer();

        Map<String, TransportationContainer> transportationContainers = new HashMap<>();
        transportationContainers.put("general", testContainer);
        this.workingDir = Path.of("", "src/test/resources");
        Mockito.when(containerService.createContainer(new ContainerInputDto("general", BigDecimal.valueOf(5L))))
                .thenReturn(new TransportationContainer("general", BigDecimal.valueOf(5L)).getContainerDto());
        Mockito.when(containerService.getAllContainers())
                .thenReturn(transportationContainers);
    }

    private static TransportationContainer getSampleContainer() {
        var testContainer = new TransportationContainer("general", BigDecimal.valueOf(5L));
        var testGood = new Good("Printer", BigDecimal.valueOf(400L), BigDecimal.valueOf(3L));
        testContainer.setTotalAllocatedWeight(BigDecimal.valueOf(3L));
        testContainer.getLoadedGoods().push(testGood);
        testContainer.getHighestPriceGoods().push(testGood);
        return testContainer;
    }

    // test import a csv file successfully
    @Test
    @Order(1)
    void testImportContainersFromCsv() {
        Path file = this.workingDir.resolve("testImport.csv");
        var importFile = makeImportMultipartFile(file);
        var containers = service.importContainersFromCsv(importFile);
        assertNotNull(containers.get("general"));
    }

    // test export data into csv file successfully
    @Test
    @Order(2)
    void testExportContainerToCsv() throws IOException {
        Path file = testDirectory.resolve("testExport.csv");
        Writer writer = new FileWriter(file.toFile());
        service.exportContainerToCsv(writer);
        writer.close();
        assertTrue(Files.exists(file));
    }

    private MultipartFile makeImportMultipartFile(Path file) {
        byte[] content = new byte[0];
        try {
            content = Files.readAllBytes(file);
        } catch (final IOException ignored) {
        }
        return new MockMultipartFile("testImport.csv",
                "testImport.csv", "text/csv", content);
    }
}

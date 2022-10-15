package com.kaveh.transportationprovider.service;

import com.kaveh.transportationprovider.model.TransportationContainer;
import com.kaveh.transportationprovider.model.dto.ContainerDto;
import com.kaveh.transportationprovider.model.dto.ContainerInputDto;
import com.kaveh.transportationprovider.model.dto.LoadDto;
import com.kaveh.transportationprovider.model.exception.ApiException;
import com.kaveh.transportationprovider.repository.TransportationContainerRepository;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TransportationContainerServiceTest {

    @InjectMocks
    private TransportationContainerService service;

    @Mock
    private TransportationContainerRepository repository;

    private static final Map<String, TransportationContainer> transportationContainers = new HashMap<>();


    // test create container with null input
    @Test
    @Order(1)
    void testCreateContainer(){
        assertThrows(ApiException.class, () -> service.createContainer(new ContainerInputDto()));
        assertThrows(ApiException.class, () -> service.createContainer(null));
    }

    // test create container successfully
    @Test
    @Order(2)
    void testCreateContainer2(){
        when(repository.getAllContainers()).thenReturn(transportationContainers);
        ContainerInputDto containerInputDto = mock(ContainerInputDto.class);
        when(containerInputDto.getMaxGrossLoad()).thenReturn(BigDecimal.valueOf(10L));
        when(containerInputDto.getName()).thenReturn("general");
        ContainerDto containerDto = service.createContainer(containerInputDto);
        assertEquals(BigDecimal.valueOf(10L), containerDto.getMaxGrossLoad());
        assertEquals("general", containerDto.getName());
    }

    // test create container with existing name
    @Test
    @Order(3)
    void testCreateContainer3(){
        TransportationContainer container = new TransportationContainer("general", BigDecimal.valueOf(10L));
        transportationContainers.put("general", container);
        when(repository.getAllContainers()).thenReturn(transportationContainers);
        ContainerInputDto containerInputDto = mock(ContainerInputDto.class);
        when(containerInputDto.getMaxGrossLoad()).thenReturn(BigDecimal.valueOf(15L));
        when(containerInputDto.getName()).thenReturn("General");
        when(repository.addContainer("general", container)).thenReturn(container);
        assertThrows(ApiException.class, () -> service.createContainer(containerInputDto));
    }

    // test get container successfully
    @Test
    @Order(4)
    void testGetContainer(){
        when(repository.getContainerByName("general")).thenReturn(transportationContainers.get("general"));
        ContainerDto containerDto = service.getContainerByName("general").getContainerDto();
        assertEquals(BigDecimal.valueOf(10L), containerDto.getMaxGrossLoad());
        assertEquals("general", containerDto.getName());
    }

    // test get container that does not exist
    @Test
    @Order(5)
    void testGetContainer2(){
        when(repository.getContainerByName("general2")).thenReturn(transportationContainers.get("general2"));
        assertThrows(ApiException.class, () -> service.getContainerByName("general2"));
    }

    // test load container successfully
    @Test
    @Order(6)
    void testLoadGood(){
        when(repository.getContainerByName("general")).thenReturn(transportationContainers.get("general"));
        LoadDto loadDto = mock(LoadDto.class);
        when(loadDto.getPrice()).thenReturn(BigDecimal.valueOf(200L));
        when(loadDto.getWeightInKg()).thenReturn(BigDecimal.valueOf(4L));
        when(loadDto.getGoodName()).thenReturn("table");
        when(loadDto.getTransportationContainerName()).thenReturn("general");
        ContainerDto containerDto = service.loadGood(loadDto).getContainerDto();
        assertEquals(BigDecimal.valueOf(4L), containerDto.getTotalAllocatedWeight());
        assertEquals("table", containerDto.getMostExpensiveGood().getName());
    }

    // test load container successfully, 2nd good
    @Test
    @Order(7)
    void testLoadGood2(){
        when(repository.getContainerByName("general")).thenReturn(transportationContainers.get("general"));
        LoadDto loadDto = mock(LoadDto.class);
        when(loadDto.getPrice()).thenReturn(BigDecimal.valueOf(100L));
        when(loadDto.getWeightInKg()).thenReturn(BigDecimal.valueOf(2L));
        when(loadDto.getGoodName()).thenReturn("ventilator");
        when(loadDto.getTransportationContainerName()).thenReturn("general");
        ContainerDto containerDto = service.loadGood(loadDto).getContainerDto();
        assertEquals(BigDecimal.valueOf(6L), containerDto.getTotalAllocatedWeight());
        assertEquals("table", containerDto.getMostExpensiveGood().getName());
    }

    // test load container which does not exist
    @Test
    @Order(8)
    void testLoadGood3(){
        when(repository.getContainerByName("test")).thenReturn(transportationContainers.get("test"));
        LoadDto loadDto = mock(LoadDto.class);
        when(loadDto.getPrice()).thenReturn(BigDecimal.valueOf(1000L));
        when(loadDto.getWeightInKg()).thenReturn(BigDecimal.valueOf(2L));
        when(loadDto.getGoodName()).thenReturn("laptop");
        when(loadDto.getTransportationContainerName()).thenReturn("test");
        assertThrows(ApiException.class, () -> service.loadGood(loadDto).getContainerDto());
    }

    // test unload container successfully
    @Test
    @Order(9)
    void testUnloadGood(){
        when(repository.getContainerByName("general")).thenReturn(transportationContainers.get("general"));
        ContainerDto containerDto = service.unloadContainer("general").getContainerDto();
        assertEquals(BigDecimal.valueOf(4L), containerDto.getTotalAllocatedWeight());
        assertEquals("table", containerDto.getMostExpensiveGood().getName());
    }

    // test unload container which does not exist
    @Test
    @Order(10)
    void testUnloadGood2(){
        when(repository.getContainerByName("test")).thenReturn(transportationContainers.get("test"));
        assertThrows(ApiException.class, () -> service.unloadContainer("test").getContainerDto());
    }

    // test unload container to empty the container
    @Test
    @Order(11)
    void testUnloadGood3(){
        when(repository.getContainerByName("general")).thenReturn(transportationContainers.get("general"));
        ContainerDto containerDto = service.unloadContainer("general").getContainerDto();
        assertEquals(BigDecimal.valueOf(0L), containerDto.getTotalAllocatedWeight());
        assertNull(containerDto.getMostExpensiveGood());
    }

    // test unload empty container, which raise exception
    @Test
    @Order(12)
    void testUnloadGood4(){
        when(repository.getContainerByName("general")).thenReturn(transportationContainers.get("general"));
        assertThrows(ApiException.class, () -> service.unloadContainer("general").getContainerDto());
    }

    // test create container with 0 total gross weight
    @Test
    @Order(13)
    void testCreateContainer4(){
        when(repository.getContainerByName("general2")).thenReturn(transportationContainers.get("general2"));
        ContainerInputDto containerInputDto = mock(ContainerInputDto.class);
        when(containerInputDto.getMaxGrossLoad()).thenReturn(BigDecimal.valueOf(0L));
        when(containerInputDto.getName()).thenReturn("general2");
        assertThrows(ApiException.class, () -> service.createContainer(containerInputDto));
    }

    // test load container, result in an overflow
    @Test
    @Order(14)
    void testLoadGood4(){
        when(repository.getContainerByName("general")).thenReturn(transportationContainers.get("general"));

        LoadDto loadDto = mock(LoadDto.class);
        when(loadDto.getPrice()).thenReturn(BigDecimal.valueOf(1000L));
        when(loadDto.getWeightInKg()).thenReturn(BigDecimal.valueOf(25L));
        when(loadDto.getGoodName()).thenReturn("oven");
        when(loadDto.getTransportationContainerName()).thenReturn("general");
        assertThrows(ApiException.class, () -> service.loadGood(loadDto).getContainerDto());
    }

    // test load container with empty container name
    @Test
    @Order(15)
    void testLoadGood5(){
        when(repository.getContainerByName("")).thenReturn(transportationContainers.get(""));
        LoadDto loadDto = mock(LoadDto.class);
        when(loadDto.getPrice()).thenReturn(BigDecimal.valueOf(100L));
        when(loadDto.getWeightInKg()).thenReturn(BigDecimal.valueOf(1L));
        when(loadDto.getGoodName()).thenReturn("Vase");
        when(loadDto.getTransportationContainerName()).thenReturn("");
        assertThrows(ApiException.class, () -> service.loadGood(loadDto).getContainerDto());
    }

    // test get all containers successfully
    @Test
    @Order(16)
    void testGetAllContainers(){
        when(repository.getAllContainers()).thenReturn(transportationContainers);
        Map<String, TransportationContainer> containers = service.getAllContainers();
        assertTrue(containers.containsKey("general"));
    }
}

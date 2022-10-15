package com.kaveh.transportationprovider.repository;

import com.kaveh.transportationprovider.model.TransportationContainer;
import com.kaveh.transportationprovider.model.dto.ContainerInputDto;
import com.kaveh.transportationprovider.model.exception.ApiException;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TransportationContainerRepositoryTest {

    @InjectMocks
    private TransportationContainerRepository repository;

    // test add container successfully
    @Test
    @Order(1)
    void testAddContainer(){
        TransportationContainer container = new TransportationContainer("general", BigDecimal.valueOf(12L));
        assertEquals("general", repository.addContainer("general", container).getName());
    }

    // test get all containers
    @Test
    @Order(2)
    void testAllContainers(){
        assertEquals(1, repository.getAllContainers().size());
    }

    // test get container by name
    @Test
    @Order(3)
    void testGetContainerByName(){
        assertNotNull(repository.getContainerByName("general"));
    }

    // test get container by name which does not exist
    @Test
    @Order(4)
    void testGetContainerByName2(){
        assertNull(repository.getContainerByName("test"));
    }
}

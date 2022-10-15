package com.kaveh.transportationprovider.controller;

import com.google.gson.Gson;
import com.kaveh.transportationprovider.model.Good;
import com.kaveh.transportationprovider.model.TransportationContainer;
import com.kaveh.transportationprovider.model.dto.ContainerDto;
import com.kaveh.transportationprovider.model.dto.ContainerInputDto;
import com.kaveh.transportationprovider.model.dto.LoadDto;
import com.kaveh.transportationprovider.model.dto.UnloadGoodInputDto;
import com.kaveh.transportationprovider.service.TransportationContainerService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(value = TransportationContainerController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TransportationContainerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransportationContainerService transportationContainerService;

    private Gson gson;

    @BeforeEach
    public void setUp() {
        gson = new Gson();
    }

    // test create container successfully
    @Test
    @Order(1)
    void testCreateContainer() throws Exception {
        ContainerInputDto containerInputDto = new ContainerInputDto("general", BigDecimal.valueOf(5L));
        String jsonInString = gson.toJson(containerInputDto);
        Mockito.when(transportationContainerService.createContainer(new ContainerInputDto("general", BigDecimal.valueOf(5L))))
                .thenReturn(new TransportationContainer("general", BigDecimal.valueOf(5L)).getContainerDto());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/v1/containers/")
                .accept(MediaType.APPLICATION_JSON).content(jsonInString)
                .contentType(MediaType.APPLICATION_JSON);

        var result = mockMvc.perform(requestBuilder).andReturn();
        var containerDto = gson.fromJson(result.getResponse().getContentAsString(), ContainerDto.class);
        assertEquals("general", containerDto.getName());
    }

    // test get container successfully
    @Test
    @Order(2)
    void testGetContainer() throws Exception {
        var container = new TransportationContainer("general", BigDecimal.valueOf(5L));
        Mockito.when(transportationContainerService.getContainerByName("general"))
                .thenReturn(container);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/v1/containers/general")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        var result = mockMvc.perform(requestBuilder).andReturn();
        var containerDto = gson.fromJson(result.getResponse().getContentAsString(), ContainerDto.class);
        assertEquals("general", containerDto.getName());
        assertEquals(BigDecimal.valueOf(5L), containerDto.getMaxGrossLoad());

    }

    // test load container successfully
    @Test
    @Order(3)
    void testLoadContainer() throws Exception {
        LoadDto loadDto = new LoadDto("general", "laptop", BigDecimal.valueOf(2L), BigDecimal.valueOf(2000L));
        String jsonInString = gson.toJson(loadDto);
        Mockito.when(transportationContainerService.loadGood(loadDto))
                .thenReturn(getSampleLoadedContainer());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/v1/containers/load")
                .accept(MediaType.APPLICATION_JSON).content(jsonInString)
                .contentType(MediaType.APPLICATION_JSON);

        var result = mockMvc.perform(requestBuilder).andReturn();
        var containerDto = gson.fromJson(result.getResponse().getContentAsString(), ContainerDto.class);
        assertNotNull(containerDto.getMostExpensiveGood());
        assertEquals("laptop", containerDto.getMostExpensiveGood().getName());
    }

    // test unload container successfully
    @Test
    @Order(4)
    void testUnloadContainer() throws Exception {
        UnloadGoodInputDto unloadGoodInputDto = new UnloadGoodInputDto("general");
        String jsonInString = gson.toJson(unloadGoodInputDto);
        Mockito.when(transportationContainerService.unloadContainer("general"))
                .thenReturn(getSampleEmptyContainer());
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/v1/containers/unload")
                .accept(MediaType.APPLICATION_JSON).content(jsonInString)
                .contentType(MediaType.APPLICATION_JSON);

        var result = mockMvc.perform(requestBuilder).andReturn();
        var containerDto = gson.fromJson(result.getResponse().getContentAsString(), ContainerDto.class);
        assertNull(containerDto.getMostExpensiveGood());
    }

    private static TransportationContainer getSampleLoadedContainer() {
        var testContainer = new TransportationContainer("general", BigDecimal.valueOf(5L));
        var testGood = new Good("laptop", BigDecimal.valueOf(2000L), BigDecimal.valueOf(2L));
        testContainer.setTotalAllocatedWeight(BigDecimal.valueOf(3L));
        testContainer.getLoadedGoods().push(testGood);
        testContainer.getHighestPriceGoods().push(testGood);
        return testContainer;
    }

    private static TransportationContainer getSampleEmptyContainer() {
        var testContainer = new TransportationContainer("general", BigDecimal.valueOf(5L));
        testContainer.setTotalAllocatedWeight(BigDecimal.valueOf(3L));
        return testContainer;
    }
}

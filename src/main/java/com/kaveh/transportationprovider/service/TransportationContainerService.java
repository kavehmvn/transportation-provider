package com.kaveh.transportationprovider.service;

import com.kaveh.transportationprovider.model.Good;
import com.kaveh.transportationprovider.model.TransportationContainer;
import com.kaveh.transportationprovider.model.dto.ContainerDto;
import com.kaveh.transportationprovider.model.dto.ContainerInputDto;
import com.kaveh.transportationprovider.model.dto.LoadDto;
import com.kaveh.transportationprovider.model.exception.ApiException;
import com.kaveh.transportationprovider.repository.TransportationContainerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class TransportationContainerService {

    @Autowired
    private TransportationContainerRepository repository;

    public Map<String, TransportationContainer> getAllContainers() {
        return repository.getAllContainers();
    }

    public ContainerDto createContainer(ContainerInputDto containerDto) {
        // validate inputs to create a container
        validateInputs(containerDto);

        // check if container name is unique
        validateUniqueContainerName(containerDto.getName());

        TransportationContainer container = new TransportationContainer(containerDto.getName().trim().toLowerCase(), containerDto.getMaxGrossLoad());
        repository.addContainer(container.getName(), container);
        return container.getContainerDto();
    }

    public TransportationContainer getContainerByName(String containerName) {
        if (containerName == null || containerName.trim().isEmpty()) {
            throw new ApiException(HttpStatus.NOT_ACCEPTABLE, "INVALID_CONTAINER_NAME");
        }
        var container = repository.getContainerByName(containerName);
        if (container == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "CONTAINER_NOT_FOUND");
        }
        return container;
    }

    public TransportationContainer loadGood(LoadDto loadDto) {
        var container = getContainerByName(loadDto.getTransportationContainerName());
        var good = new Good(loadDto.getGoodName(), loadDto.getPrice(), loadDto.getWeightInKg());
        loadGoodIntoContainer(container, good);
        return container;
    }

    // fetch the container
    // unload last added good from the container
    public TransportationContainer unloadContainer(String transportationContainerName) {

        var container = getContainerByName(transportationContainerName);
        unloadFromContainer(container);
        return container;
    }

    private void validateUniqueContainerName(String containerName) {
        var containers = getAllContainers();
        if (containers.containsKey(containerName.trim().toLowerCase())) {
            throw new ApiException(HttpStatus.NOT_ACCEPTABLE, "CONTAINER_ALREADY_EXISTS");
        }
    }

    private void validateInputs(ContainerInputDto containerDto) {
        if (containerDto == null) {
            throw new ApiException(HttpStatus.NOT_ACCEPTABLE, "INVALID_INPUTS");
        }

        // validate for mandatory inputs to create a container
        if (containerDto.getName() == null || containerDto.getName().trim().isEmpty()) {
            throw new ApiException(HttpStatus.NOT_ACCEPTABLE, "INVALID_CONTAINER_NAME");
        }
        if (containerDto.getMaxGrossLoad() == null || containerDto.getMaxGrossLoad().compareTo(BigDecimal.ZERO) == 0) {
            throw new ApiException(HttpStatus.NOT_ACCEPTABLE, "INVALID_MAX_GROSS_LOAD");
        }
    }

    // unload last added good from the given container
    // update the highest price in remaining goods
    // update the total allocated weight
    private void unloadFromContainer(TransportationContainer container) {
        if (container.getLoadedGoods().isEmpty()) {
            throw new ApiException(HttpStatus.NOT_ACCEPTABLE, "CONTAINER_IS_EMPTY");
        }
        var good = container.getLoadedGoods().pop();
        container.getHighestPriceGoods().pop();
        container.setTotalAllocatedWeight(container.getTotalAllocatedWeight().subtract(good.getWeightInKg()));
    }

    // load the given container with the given good
    // update the allocated weight of the container
    // update the highest price good in the container
    private void loadGoodIntoContainer(TransportationContainer container, Good good) {
        if (container.getMaxGrossLoad().compareTo(container.getTotalAllocatedWeight().add(good.getWeightInKg())) < 0) {
            throw new ApiException(HttpStatus.NOT_ACCEPTABLE, "CONTAINER_OVERFLOW");
        }
        container.setTotalAllocatedWeight(container.getTotalAllocatedWeight().add(good.getWeightInKg()));
        container.getLoadedGoods().push(good);
        if (good.getPrice().compareTo(container.getHighestPrice()) > 0)
            container.getHighestPriceGoods().push(good);
        else
            container.getHighestPriceGoods().push(container.getHighestPriceGoods().peek());
    }
}

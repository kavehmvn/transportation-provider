package com.kaveh.transportationprovider.repository;

import com.kaveh.transportationprovider.model.TransportationContainer;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class TransportationContainerRepository {
    private static final Map<String, TransportationContainer> transportationContainers = new HashMap<>();


    public Map<String, TransportationContainer> getAllContainers() {
        return transportationContainers;
    }

    public TransportationContainer getContainerByName(String containerName) {
        var container = transportationContainers.get(containerName.trim().toLowerCase());
        return container;
    }

    public TransportationContainer addContainer(String containerKey, TransportationContainer TransportationContainer) {
        transportationContainers.put(containerKey, TransportationContainer);
        return TransportationContainer;
    }

}

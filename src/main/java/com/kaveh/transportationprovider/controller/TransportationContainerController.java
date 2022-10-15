package com.kaveh.transportationprovider.controller;

import com.kaveh.transportationprovider.model.dto.ContainerDto;
import com.kaveh.transportationprovider.model.dto.ContainerInputDto;
import com.kaveh.transportationprovider.model.dto.LoadDto;
import com.kaveh.transportationprovider.model.dto.UnloadGoodInputDto;
import com.kaveh.transportationprovider.service.TransportationContainerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController()
@RequestMapping("/api/v1/containers")
@Api(tags = "Transportation container APIs")
public class TransportationContainerController {

    @Autowired
    private TransportationContainerService transportationContainerService;

    // create a transportation container with a max capacity
    @PostMapping(value = "/", produces = "application/json")
    @ApiOperation(value = "create a transportation container")
    public ContainerDto createContainer(@RequestBody ContainerInputDto containerDto) {
        return transportationContainerService.createContainer(containerDto);
    }

    // get a transportation container by name
    @GetMapping(value = "/{containerName}", produces = "application/json")
    @ApiOperation(value = "Retrieve a container by name")
    public ContainerDto getContainerByName(@PathVariable String containerName) {
        var container =  transportationContainerService.getContainerByName(containerName);
        return container.getContainerDto();
    }

    // load good on at a time into a given container
    @PostMapping(value = "/load", consumes = APPLICATION_JSON_VALUE, produces = "application/json")
    @ApiOperation(value = "Load a good into a specified container")
    public ContainerDto loadGoodInContainer(@RequestBody LoadDto loadDto) {
        var container =  transportationContainerService.loadGood(loadDto);
        return container.getContainerDto();
    }

    // unload goods on at a time from a given container
    @PostMapping(value = "/unload", produces = "application/json")
    @ApiOperation(value = "Unload a good into a specified container")
    public ContainerDto unloadContainer(@RequestBody UnloadGoodInputDto unloadGoodInputDto) {
        var container = transportationContainerService.unloadContainer(unloadGoodInputDto.getContainerName());
        return container.getContainerDto();
    }
}

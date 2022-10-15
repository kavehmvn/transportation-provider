package com.kaveh.transportationprovider.controller;


import com.kaveh.transportationprovider.model.dto.ContainerDto;
import com.kaveh.transportationprovider.service.DataExportImportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/data")
@Api(tags = "Export/Import data APIs")
public class DataExportImportController {

    @Autowired
    private DataExportImportService dataExportImportService;


    // export all the container instances into a csv file to download
    @GetMapping(value = "/containers/download", produces = "application/json")
    @ApiOperation(value = "Export current containers instances as a csv file")
    public void exportContainerToCsv(HttpServletResponse servletResponse) throws IOException {
        servletResponse.setContentType("text/csv");
        servletResponse.addHeader("Content-Disposition","attachment; filename=\"containers.csv\"");
        dataExportImportService.exportContainerToCsv(servletResponse.getWriter());
    }

    // import a given csf file containing container instances with goods
    @PostMapping("/containers/upload")
    @ApiOperation(value = "Import a csv file containing containers with goods")
    public Map<String, ContainerDto> uploadCSVFile(@RequestParam("file") MultipartFile file) {
        var containers = dataExportImportService.importContainersFromCsv(file);
        Map<String, ContainerDto> result = new HashMap<>();
        containers.forEach((k, v) -> result.put(k, v.getContainerDto()));
        return result;
    }
}

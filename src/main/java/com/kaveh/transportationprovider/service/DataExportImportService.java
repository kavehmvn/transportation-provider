package com.kaveh.transportationprovider.service;

import com.kaveh.transportationprovider.model.Good;
import com.kaveh.transportationprovider.model.TransportationContainer;
import com.kaveh.transportationprovider.model.dto.ContainerInputDto;
import com.kaveh.transportationprovider.model.dto.LoadDto;
import com.kaveh.transportationprovider.model.exception.ApiException;
import com.kaveh.transportationprovider.util.CsvUtil;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class DataExportImportService {

    @Autowired
    private TransportationContainerService transportationContainerService;

    public void exportContainerToCsv(Writer writer) {
        // export data of all available container instances to a csv file
        try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {

            // first row is the column definition
            csvPrinter.printRecord((Object[]) CsvUtil.ContainersCsvColumnHeaders);

            var containersItr = transportationContainerService.getAllContainers().entrySet().iterator();
            while (containersItr.hasNext()) {
                var containerItem = containersItr.next();
                var container = containerItem.getValue();
                Iterator<Good> it = container.getLoadedGoods().iterator();
                while (it.hasNext()) {
                    var good = it.next();
                    csvPrinter.printRecord(container.getName(), container.getMaxGrossLoad(), good.getName(), good.getPrice(), good.getWeightInKg());
                }
            }

        } catch (IOException e) {
            throw new ApiException(HttpStatus.NOT_ACCEPTABLE, "ERROR_EXPORTING_DATA");
        }
    }

    public Map<String, TransportationContainer> importContainersFromCsv(MultipartFile file) {
        //validate file before parsing
        validateImportedFile(file);

        // TODO failure cases should be taken care of including resume, checkpoints and rollbacks
        // read all the records of the file
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
             CSVParser csvFileParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withHeader(CsvUtil.ContainersCsvColumnHeaders).withFirstRecordAsHeader())) {
            Iterable<CSVRecord> csvRecords = csvFileParser.getRecords();

            // group by records by container specification
            Map<Pair<String, String>, List<CSVRecord>> containersGroupByName = StreamSupport
                    .stream(csvRecords.spliterator(), false).
                    collect(Collectors.groupingBy(record -> Pair.of(record.get("containerName"), record.get("maxGrossLoad"))));

            // TODO csv records should be validated before importing
            // TODO containers should be created in group and goods should be loaded at once
            // for each entry
            // 1. create the container
            // load the container with goods
            for (Map.Entry<Pair<String, String>, List<CSVRecord>> entry : containersGroupByName.entrySet()) {
                var containerSpec = entry.getKey();
                var containerGoods = entry.getValue();
                ContainerInputDto containerInputDto = new ContainerInputDto(containerSpec.getKey(),
                        new BigDecimal((String) containerSpec.getValue()));

                var container = transportationContainerService.createContainer(containerInputDto);
                for (CSVRecord good: containerGoods){
                    var loadDto = new LoadDto(container.getName(), good.get("goodName"), new BigDecimal((String) good.get("weightInKg")),
                            new BigDecimal((String) good.get("price")));
                    transportationContainerService.loadGood(loadDto);
                }

            }

        } catch (IOException e) {
            throw new ApiException(HttpStatus.NOT_ACCEPTABLE, "ERROR_IMPORTING_FILE");
        }
        return transportationContainerService.getAllContainers();
    }

    private void validateImportedFile(MultipartFile file) {
        // validate if file is csv and not null
        if (file.isEmpty()) {
            throw new ApiException(HttpStatus.NOT_ACCEPTABLE, "FILE_IS_EMPTY");
        }
        if (!CsvUtil.isCsvFormat(file)) {
            throw new ApiException(HttpStatus.NOT_ACCEPTABLE, "INVALID_FILE_FORMAT");
        }
    }
}

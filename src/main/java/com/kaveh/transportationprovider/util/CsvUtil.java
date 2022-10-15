package com.kaveh.transportationprovider.util;

import org.springframework.web.multipart.MultipartFile;

public class CsvUtil {
    public static String TYPE = "text/csv";
    public static String[] ContainersCsvColumnHeaders = {"containerName", "maxGrossLoad", "goodName", "price", "weightInKg"};

    public static boolean isCsvFormat(MultipartFile file) {

        if (!TYPE.equals(file.getContentType())) {
            return false;
        }
        return true;
    }
}

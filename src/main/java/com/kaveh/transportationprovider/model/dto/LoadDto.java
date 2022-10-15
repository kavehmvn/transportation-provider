package com.kaveh.transportationprovider.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoadDto {

    private String transportationContainerName;

    private String goodName;

    private BigDecimal weightInKg;

    private BigDecimal price;
}

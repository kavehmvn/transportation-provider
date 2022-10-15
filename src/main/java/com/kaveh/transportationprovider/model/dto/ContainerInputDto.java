package com.kaveh.transportationprovider.model.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContainerInputDto {

    private String name;

    private BigDecimal maxGrossLoad;

}

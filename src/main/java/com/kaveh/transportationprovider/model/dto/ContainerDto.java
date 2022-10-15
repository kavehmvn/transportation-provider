package com.kaveh.transportationprovider.model.dto;

import com.kaveh.transportationprovider.model.Good;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContainerDto {

    private UUID id;

    private String name;

    private BigDecimal maxGrossLoad;

    private BigDecimal totalAllocatedWeight;

    private Good mostExpensiveGood;

}

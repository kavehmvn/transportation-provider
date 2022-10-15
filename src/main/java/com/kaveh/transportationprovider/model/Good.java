package com.kaveh.transportationprovider.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

import static com.kaveh.transportationprovider.util.Utility.makeId;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Good {

    public Good(String name, BigDecimal price, BigDecimal weightInKg) {
        this.id = makeId();
        this.name = name;
        this.price = price;
        this.weightInKg = weightInKg;
    }

    private UUID id;

    private String name;

    private BigDecimal price;

    private BigDecimal weightInKg;
}

package com.kaveh.transportationprovider.model;

import com.kaveh.transportationprovider.model.dto.ContainerDto;
import com.kaveh.transportationprovider.util.Utility;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Stack;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransportationContainer {

    public TransportationContainer(String name, BigDecimal maxGrossLoad) {
        this.name = name;
        this.maxGrossLoad = maxGrossLoad;
        this.id = Utility.makeId();
        this.highestPriceGoods = new Stack<>();
        this.loadedGoods = new Stack<>();
        this.totalAllocatedWeight = BigDecimal.ZERO;
    }

    private UUID id;

    private String name;

    private BigDecimal maxGrossLoad;

    private BigDecimal totalAllocatedWeight;

    private Stack<Good> loadedGoods;

    private Stack<Good> highestPriceGoods;

    public ContainerDto getContainerDto() {
        return new ContainerDto(this.getId(), this.getName(), this.getMaxGrossLoad(),
                this.getTotalAllocatedWeight(), this.highestPriceGoods.isEmpty() ? null : this.highestPriceGoods.peek());
    }

    public BigDecimal getHighestPrice() {
        var good = this.highestPriceGoods.isEmpty() ? null : this.highestPriceGoods.peek();
        return good == null ? BigDecimal.ZERO : good.getPrice();
    }
}

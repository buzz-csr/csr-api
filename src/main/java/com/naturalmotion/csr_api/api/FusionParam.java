package com.naturalmotion.csr_api.api;

import java.math.BigDecimal;

public class FusionParam {

    private FusionColor color;

    private BigDecimal quantity;

    public FusionParam(FusionColor color, BigDecimal quantity) {
        this.color = color;
        this.quantity = quantity;
    }

    public FusionColor getColor() {
        return color;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

}

package com.naturalmotion.csr_api.api;

import java.math.BigDecimal;

public class Resource {

    private ResourceType type;

    private BigDecimal spent;

    private BigDecimal earned;

    public ResourceType getType() {
        return type;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }

    public BigDecimal getSpent() {
        return spent;
    }

    public void setSpent(BigDecimal spent) {
        this.spent = spent;
    }

    public BigDecimal getEarned() {
        return earned;
    }

    public void setEarned(BigDecimal earned) {
        this.earned = earned;
    }

    @Override
    public String toString() {
        return "Resource [type=" + type + ", spent=" + spent + ", earned=" + earned + "]";
    }

}

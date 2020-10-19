package com.naturalmotion.csr_api.api;

import java.math.BigDecimal;

public class EliteTokenParam {

    private EliteToken token;

    private BigDecimal amount;

    public EliteToken getToken() {
        return token;
    }

    public void setToken(EliteToken token) {
        this.token = token;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

}

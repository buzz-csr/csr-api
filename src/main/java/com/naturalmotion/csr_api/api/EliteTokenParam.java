package com.naturalmotion.csr_api.api;

import java.math.BigDecimal;

public class EliteTokenParam {

    private final EliteToken token;

    private final BigDecimal amount;

    public EliteTokenParam(EliteToken token, BigDecimal amount) {
        this.token = token;
        this.amount = amount;
    }

    public EliteToken getToken() {
        return token;
    }

    public BigDecimal getAmount() {
        return amount;
    }

}

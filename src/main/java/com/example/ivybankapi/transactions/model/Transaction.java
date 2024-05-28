package com.example.ivybankapi.transactions.model;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Transaction {
    private Long accountId;
    private BigDecimal amount;
    private BigDecimal amountInEuros;
    private Currency currency;
    private String sender;
    private String receiver;
    private BigDecimal initialBalance;
    private BigDecimal finalBalance;
    private String description;
    private CreditDebitIndicator creditDebitIndicator;

}

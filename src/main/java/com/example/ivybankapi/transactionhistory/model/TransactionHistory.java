package com.example.ivybankapi.transactionhistory.model;

import com.example.ivybankapi.transactions.model.Currency;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TransactionHistory {
  private Long transactionId;
  private Long accountId;
  private OffsetDateTime date;
  private BigDecimal amount;
  private Currency currency;
  private String description;
}


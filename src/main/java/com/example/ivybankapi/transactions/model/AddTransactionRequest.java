package com.example.ivybankapi.transactions.model;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddTransactionRequest {
  @NotNull
  private Long accountId;
  @NotNull
  @Digits(integer = 100, fraction = 2)
  private BigDecimal amount;
  private Currency currency;
  @NotBlank
  private String sender;
  @NotBlank
  private String receiver;
  @NotBlank
  private String description;
  private CreditDebitIndicator creditDebitIndicator;
}

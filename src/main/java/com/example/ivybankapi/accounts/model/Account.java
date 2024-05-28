package com.example.ivybankapi.accounts.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
  private Long id;
  private Long customerId;
  private String iban;
  private String name;
  private BigDecimal balance;
  private Boolean active;

}

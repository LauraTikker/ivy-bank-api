package com.example.ivybankapi.accounts.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddAccountRequest {
  @NotNull
  private Long customerId;
  private String accountName;

}

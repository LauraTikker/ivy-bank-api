package com.example.ivybankapi.customers.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddCustomerRequest {
  @NotBlank
  private String firstName;
  @NotBlank
  private String lastName;
  private CustomerType customerType;
}

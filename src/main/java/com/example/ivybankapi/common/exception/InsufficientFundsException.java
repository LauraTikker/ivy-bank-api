package com.example.ivybankapi.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class InsufficientFundsException extends RuntimeException {
  private final String message;
}

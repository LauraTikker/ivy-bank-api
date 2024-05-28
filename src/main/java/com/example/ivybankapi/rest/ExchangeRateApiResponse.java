package com.example.ivybankapi.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ExchangeRateApiResponse {
  @JsonProperty(value="conversion_result")
  private String conversionResult;
}

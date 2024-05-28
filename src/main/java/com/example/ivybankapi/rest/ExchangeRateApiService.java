package com.example.ivybankapi.rest;

import com.example.ivybankapi.transactions.model.Currency;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ExchangeRateApiService {

  @Value("${exchange.rate.api.url}")
  private String exchangeRateApiUrl;

  private final WebClient client = WebClient.create();

  public ExchangeRateApiResponse convertCurrencyToEuros(BigDecimal amount, Currency baseCurrency) {
    return client.get()
        .uri(exchangeRateApiUrl + baseCurrency + "/EUR/" + amount)
        .exchangeToMono(response -> {
          if (response.statusCode().equals(HttpStatus.OK)) {
            return response.bodyToMono(ExchangeRateApiResponse.class);
          } else {
            return response.createException()
                .flatMap(Mono::error);
          }
        }).block();
  }

}

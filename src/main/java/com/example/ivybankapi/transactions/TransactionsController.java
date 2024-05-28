package com.example.ivybankapi.transactions;

import com.example.ivybankapi.transactions.model.AddTransactionRequest;
import com.example.ivybankapi.transactions.model.AddTransactionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TransactionsController {
  
  private final TransactionsService transactionsService;
  
  @PostMapping("/transaction")
  public ResponseEntity<AddTransactionResponse> addTransaction(@Valid @RequestBody AddTransactionRequest request) {
    return ResponseEntity.ok().body(transactionsService.addTransaction(request));
  }

}

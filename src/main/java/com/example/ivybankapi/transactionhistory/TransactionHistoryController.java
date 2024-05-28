package com.example.ivybankapi.transactionhistory;

import com.example.ivybankapi.transactionhistory.model.TransactionHistory;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TransactionHistoryController {

  private final TransactionHistoryService transactionHistoryService;

  @GetMapping("/transaction-history")
  public ResponseEntity<List<TransactionHistory>> addTransaction(@RequestParam Long accountId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
  ) {
    List<TransactionHistory> transactionHistory = transactionHistoryService.getTransactionHistory(accountId, fromDate, toDate);
    return ResponseEntity.ok().body(transactionHistory);
  }

}

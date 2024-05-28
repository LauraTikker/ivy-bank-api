package com.example.ivybankapi.transactionhistory;

import com.example.ivybankapi.transactionhistory.model.TransactionHistory;
import com.example.ivybankapi.transactions.TransactionsRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionHistoryService {

  private final TransactionsRepository transactionsRepository;

  public List<TransactionHistory> getTransactionHistory(Long accountId, LocalDate fromDate, LocalDate toDate) {
    return transactionsRepository.findTransactionsByAccountId(accountId, fromDate, toDate);
  }

}

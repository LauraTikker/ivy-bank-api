package com.example.ivybankapi.transactions;

import com.example.ivybankapi.accounts.AccountsRepository;
import com.example.ivybankapi.accounts.model.Account;
import com.example.ivybankapi.common.exception.AccountNotFoundException;
import com.example.ivybankapi.common.exception.InsufficientFundsException;
import com.example.ivybankapi.common.exception.InvalidAmountException;
import com.example.ivybankapi.rest.ExchangeRateApiService;
import com.example.ivybankapi.transactions.model.AddTransactionRequest;
import com.example.ivybankapi.transactions.model.AddTransactionResponse;
import com.example.ivybankapi.transactions.model.CreditDebitIndicator;
import com.example.ivybankapi.transactions.model.Currency;
import com.example.ivybankapi.transactions.model.Transaction;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionsService {
  
  private final TransactionsRepository transactionsRepository;
  private final AccountsRepository accountsRepository;
  private final ExchangeRateApiService exchangeRateApiService;

  @Transactional
  public AddTransactionResponse addTransaction(AddTransactionRequest request) {
    verifyAmount(request.getAmount(), request.getCreditDebitIndicator());
    Account account = accountsRepository.findAccountById(request.getAccountId());
    verifyAccount(request.getAccountId(), account);

    BigDecimal transactionAmountInEuros = getAmountInEuros(request.getAmount(), request.getCurrency(), request.getCreditDebitIndicator());

    if (Boolean.TRUE.equals(insufficientFundsOnAccount(transactionAmountInEuros, account.getBalance(), request.getCreditDebitIndicator()))) {
      throw new InsufficientFundsException(String.format("Insufficient funds on an account with id: %s", account.getId()));
    }
    BigDecimal finalBalance = account.getBalance().add(transactionAmountInEuros);

    Transaction transaction = createTransaction(request, account, transactionAmountInEuros, finalBalance);

    Long transactionId = transactionsRepository.addTransaction(transaction);
    accountsRepository.updateAccountBalance(account.getId(), finalBalance);
    log.info(String.format("Account %s balance before %s transaction was %s. New account balance is %s", account.getId(), transactionId, account.getBalance(), finalBalance));
    return new AddTransactionResponse(transactionId);
  }

  private Transaction createTransaction(AddTransactionRequest request, Account account, BigDecimal transactionAmountInEuros, BigDecimal finalBalance) {
    return Transaction.builder()
        .accountId(account.getId())
        .amount(request.getAmount())
        .amountInEuros(transactionAmountInEuros)
        .currency(request.getCurrency())
        .sender(request.getSender())
        .receiver(request.getReceiver())
        .initialBalance(account.getBalance())
        .finalBalance(finalBalance)
        .description(request.getDescription())
        .creditDebitIndicator(request.getCreditDebitIndicator())
        .build();
  }

  private Boolean insufficientFundsOnAccount(BigDecimal transactionAmountInEuros, BigDecimal accountBalance, CreditDebitIndicator creditDebitIndicator) {
    return creditDebitIndicator == CreditDebitIndicator.DBIT && accountBalance.add(transactionAmountInEuros).compareTo(BigDecimal.ZERO) < 0;
  }

  private BigDecimal getAmountInEuros(BigDecimal amount, Currency currency, CreditDebitIndicator creditDebitIndicator) {
    if (currency == Currency.EUR) {
      return amount;
    }
     String conversionResult = exchangeRateApiService.convertCurrencyToEuros(amount.abs(), currency)
         .getConversionResult();
    BigDecimal convertedAmount = new BigDecimal(conversionResult).setScale(2, RoundingMode.HALF_UP);
    return negateDebitTransactionAmount(convertedAmount, creditDebitIndicator);
  }

  private void verifyAmount(BigDecimal amount, CreditDebitIndicator creditDebitIndicator) {
    if (amount.compareTo(new BigDecimal("0")) == 0) {
      throw new InvalidAmountException("Transaction amount cannot be 0");
    }
    if (creditDebitIndicator == CreditDebitIndicator.CRDT && amount.compareTo(new BigDecimal("0")) < 0) {
      throw new InvalidAmountException("Amount of credit transaction can not be negative");
    }
    if (creditDebitIndicator == CreditDebitIndicator.DBIT && amount.compareTo(new BigDecimal("0")) > 0) {
      throw new InvalidAmountException("Amount of debit transaction can not be positive");
    }
  }

  private void verifyAccount(Long accountId, Account account) {
    if (account == null) {
      throw new AccountNotFoundException(String.format("Account with id: %s not found", accountId));
    }
  }

  private BigDecimal negateDebitTransactionAmount(BigDecimal amount, CreditDebitIndicator creditDebitIndicator) {
    if (creditDebitIndicator == CreditDebitIndicator.DBIT) {
      return amount.negate();
    }
    return  amount;
  }
}

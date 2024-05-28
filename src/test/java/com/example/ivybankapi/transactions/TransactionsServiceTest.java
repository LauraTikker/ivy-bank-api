package com.example.ivybankapi.transactions;

import static com.example.ivybankapi.transactions.model.CreditDebitIndicator.CRDT;
import static com.example.ivybankapi.transactions.model.CreditDebitIndicator.DBIT;
import static com.example.ivybankapi.transactions.model.Currency.EUR;
import static com.example.ivybankapi.transactions.model.Currency.GBP;
import static com.example.ivybankapi.transactions.model.Currency.USD;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.example.ivybankapi.accounts.AccountsRepository;
import com.example.ivybankapi.accounts.model.Account;
import com.example.ivybankapi.common.exception.AccountNotFoundException;
import com.example.ivybankapi.common.exception.InsufficientFundsException;
import com.example.ivybankapi.common.exception.InvalidAmountException;
import com.example.ivybankapi.rest.ExchangeRateApiResponse;
import com.example.ivybankapi.rest.ExchangeRateApiService;
import com.example.ivybankapi.transactions.model.AddTransactionRequest;
import com.example.ivybankapi.transactions.model.AddTransactionResponse;
import com.example.ivybankapi.transactions.model.CreditDebitIndicator;
import com.example.ivybankapi.transactions.model.Currency;
import com.example.ivybankapi.transactions.model.Transaction;
import java.math.BigDecimal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionsServiceTest {

  @Mock
  private TransactionsRepository transactionsRepository;

  @Mock
  private AccountsRepository accountsRepository;

  @Mock
  private ExchangeRateApiService exchangeRateApiService;

  @InjectMocks
  private TransactionsService transactionsService;

  private static final Long ACCOUNT_ID = 123L;

  @Test
  void addTransaction_withCreditTransaction_shouldAddAmountToAccountBalance() {
    AddTransactionRequest request = getAddTransactionRequest(new BigDecimal("10.00"), CRDT, EUR);
    Account account = createAccount(new BigDecimal("15.00"));
    when(accountsRepository.findAccountById(ACCOUNT_ID)).thenReturn(account);
    Transaction transaction = Transaction.builder()
        .accountId(account.getId())
        .amount(request.getAmount())
        .amountInEuros(request.getAmount())
        .currency(request.getCurrency())
        .sender(request.getSender())
        .receiver(request.getReceiver())
        .initialBalance(account.getBalance())
        .finalBalance(new BigDecimal("25.00"))
        .description("description")
        .creditDebitIndicator(request.getCreditDebitIndicator())
        .build();
    when(transactionsRepository.addTransaction(transaction)).thenReturn(3333L);

    AddTransactionResponse result = transactionsService.addTransaction(request);

    verifyNoInteractions(exchangeRateApiService);
    verify(accountsRepository).updateAccountBalance(ACCOUNT_ID, new BigDecimal("25.00"));
    assertEquals(3333L, result.transactionId());
  }

  @Test
  void addTransaction_withDebitTransaction_shouldSubtractAmountFromAccountBalance() {
    AddTransactionRequest request = getAddTransactionRequest(new BigDecimal("-10.00"), DBIT, EUR);
    Account account = createAccount(new BigDecimal("15.00"));
    when(accountsRepository.findAccountById(ACCOUNT_ID)).thenReturn(account);
    Transaction transaction = Transaction.builder()
        .accountId(account.getId())
        .amount(request.getAmount())
        .amountInEuros(request.getAmount())
        .currency(request.getCurrency())
        .sender(request.getSender())
        .receiver(request.getReceiver())
        .initialBalance(account.getBalance())
        .finalBalance(new BigDecimal("5.00"))
        .description("description")
        .creditDebitIndicator(request.getCreditDebitIndicator())
        .build();
    when(transactionsRepository.addTransaction(transaction)).thenReturn(3333L);

    AddTransactionResponse result = transactionsService.addTransaction(request);

    verifyNoInteractions(exchangeRateApiService);
    verify(accountsRepository).updateAccountBalance(ACCOUNT_ID, new BigDecimal("5.00"));
    assertEquals(3333L, result.transactionId());
  }

  @Test
  void addTransaction_withCurrencyNotEURAndCreditTransaction_shouldConvertAmountToEUR() {
    AddTransactionRequest request = getAddTransactionRequest(new BigDecimal("10.00"), CRDT, GBP);
    Account account = createAccount(new BigDecimal("15.00"));
    when(accountsRepository.findAccountById(ACCOUNT_ID)).thenReturn(account);
    when(exchangeRateApiService.convertCurrencyToEuros(new BigDecimal("10.00"), GBP))
        .thenReturn(new ExchangeRateApiResponse().setConversionResult("11.00"));
    Transaction transaction = Transaction.builder()
        .accountId(account.getId())
        .amount(request.getAmount())
        .amountInEuros(new BigDecimal("11.00"))
        .currency(request.getCurrency())
        .sender(request.getSender())
        .receiver(request.getReceiver())
        .initialBalance(account.getBalance())
        .finalBalance(new BigDecimal("26.00"))
        .description("description")
        .creditDebitIndicator(request.getCreditDebitIndicator())
        .build();
    when(transactionsRepository.addTransaction(transaction)).thenReturn(3333L);

    AddTransactionResponse result = transactionsService.addTransaction(request);

    verify(accountsRepository).updateAccountBalance(ACCOUNT_ID, new BigDecimal("26.00"));
    assertEquals(3333L, result.transactionId());
  }

  @Test
  void addTransaction_withCurrencyNotEURAndDebitTransaction_shouldConvertAmountToEUR() {
    AddTransactionRequest request = getAddTransactionRequest(new BigDecimal("-10.00"), DBIT, USD);
    Account account = createAccount(new BigDecimal("15.00"));
    when(accountsRepository.findAccountById(ACCOUNT_ID)).thenReturn(account);
    when(exchangeRateApiService.convertCurrencyToEuros(new BigDecimal("10.00"), USD))
        .thenReturn(new ExchangeRateApiResponse().setConversionResult("11.742"));
    Transaction transaction = Transaction.builder()
        .accountId(account.getId())
        .amount(request.getAmount())
        .amountInEuros(new BigDecimal("-11.74"))
        .currency(request.getCurrency())
        .sender(request.getSender())
        .receiver(request.getReceiver())
        .initialBalance(account.getBalance())
        .finalBalance(new BigDecimal("3.26"))
        .description("description")
        .creditDebitIndicator(request.getCreditDebitIndicator())
        .build();
    when(transactionsRepository.addTransaction(transaction)).thenReturn(3333L);

    AddTransactionResponse result = transactionsService.addTransaction(request);

    verify(accountsRepository).updateAccountBalance(ACCOUNT_ID, new BigDecimal("3.26"));
    assertEquals(3333L, result.transactionId());
  }

  @Test
  void addTransaction_withNotEnoughFundsOnAccount_shouldThrowInsufficientFundsException() {
    AddTransactionRequest request = getAddTransactionRequest(new BigDecimal("-10.00"), DBIT, EUR);
    Account account = createAccount(new BigDecimal("5.00"));
    when(accountsRepository.findAccountById(ACCOUNT_ID)).thenReturn(account);

    InsufficientFundsException exception = Assertions.assertThrows(InsufficientFundsException.class, () ->
        transactionsService.addTransaction(request)
    );

    assertEquals("Insufficient funds on an account with id: 123", exception.getMessage());
  }

  @Test
  void addTransaction_withAccountNotFound_shouldThrowAccountNotFoundException() {
    AddTransactionRequest request = getAddTransactionRequest(new BigDecimal("10.00"), CRDT, EUR);
    when(accountsRepository.findAccountById(ACCOUNT_ID)).thenReturn(null);

    AccountNotFoundException exception = Assertions.assertThrows(AccountNotFoundException.class, () ->
        transactionsService.addTransaction(request)
    );

    assertEquals("Account with id: 123 not found", exception.getMessage());
  }

  @Test
  void addTransaction_withAmountPositiveAndDebitTransaction_shouldThrowInvalidAmountException() {
    AddTransactionRequest request = getAddTransactionRequest(new BigDecimal("10.00"), DBIT, EUR);

    InvalidAmountException exception = Assertions.assertThrows(InvalidAmountException.class, () ->
        transactionsService.addTransaction(request)
    );

    assertEquals("Amount of debit transaction can not be positive", exception.getMessage());
  }

  @Test
  void addTransaction_withAmountNegativeAndCreditTransaction_shouldThrowInvalidAmountException() {
    AddTransactionRequest request = getAddTransactionRequest(new BigDecimal("-10.00"), CRDT, EUR);

    InvalidAmountException exception = Assertions.assertThrows(InvalidAmountException.class, () ->
        transactionsService.addTransaction(request)
    );

    assertEquals("Amount of credit transaction can not be negative", exception.getMessage());
  }

  @Test
  void addTransaction_withAmountZero_shouldThrowInvalidAmountException() {
    AddTransactionRequest request = getAddTransactionRequest(BigDecimal.ZERO, CRDT, EUR);

    InvalidAmountException exception = Assertions.assertThrows(InvalidAmountException.class, () ->
        transactionsService.addTransaction(request)
    );

    assertEquals("Transaction amount cannot be 0", exception.getMessage());
  }

  private AddTransactionRequest getAddTransactionRequest(BigDecimal amount, CreditDebitIndicator creditDebitIndicator, Currency currency) {
    return AddTransactionRequest.builder()
        .accountId(ACCOUNT_ID)
        .amount(amount)
        .currency(currency)
        .sender("sender")
        .receiver("receiver")
        .description("description")
        .creditDebitIndicator(creditDebitIndicator)
        .build();
  }

  private Account createAccount(BigDecimal amount) {
    return Account.builder()
        .id(ACCOUNT_ID)
        .balance(amount)
        .iban("EE987765705251347771")
        .active(true)
        .customerId(2222L)
        .name("account_name")
        .build();
  }
}
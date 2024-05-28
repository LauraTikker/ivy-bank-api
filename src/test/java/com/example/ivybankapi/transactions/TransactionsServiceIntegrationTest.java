package com.example.ivybankapi.transactions;

import static com.example.ivybankapi.transactions.model.CreditDebitIndicator.CRDT;
import static com.example.ivybankapi.transactions.model.CreditDebitIndicator.DBIT;
import static com.example.ivybankapi.transactions.model.Currency.EUR;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.ivybankapi.accounts.AccountsRepository;
import com.example.ivybankapi.accounts.AccountsService;
import com.example.ivybankapi.accounts.model.Account;
import com.example.ivybankapi.accounts.model.AddAccountRequest;
import com.example.ivybankapi.accounts.model.AddAccountResponse;
import com.example.ivybankapi.customers.CustomersRepository;
import com.example.ivybankapi.customers.model.AddCustomerRequest;
import com.example.ivybankapi.customers.model.CustomerType;
import com.example.ivybankapi.transactions.model.AddTransactionRequest;
import com.example.ivybankapi.transactions.model.CreditDebitIndicator;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class TransactionsServiceIntegrationTest {

  @Autowired
  private TransactionsService transactionsService;

  @Autowired
  private CustomersRepository customersRepository;

  @Autowired
  private AccountsService accountsService;

  @Autowired
  private AccountsRepository accountsRepository;

  @Test
  void addTransaction_withCreditTransaction_shouldAddAmountToAccountBalance() {
    Long customerId = customersRepository.addCustomer(createCustomerRequest());
    AddAccountRequest request = AddAccountRequest.builder().customerId(customerId).accountName("account-name").build();
    AddAccountResponse accountResponse = accountsService.addAccount(request);

    transactionsService.addTransaction(createTransactionRequest(accountResponse.accountId(), new BigDecimal("10.00"), CRDT));

    Account account = accountsRepository.findAccountById(accountResponse.accountId());
    assertEquals(new BigDecimal("10.00"), account.getBalance());
  }

  @Test
  void addTransaction_withDebitTransaction_shouldSubtractAmountFromAccountBalance() {
    Long customerId = customersRepository.addCustomer(createCustomerRequest());
    AddAccountRequest request = AddAccountRequest.builder().customerId(customerId).accountName("account-name").build();
    AddAccountResponse accountResponse = accountsService.addAccount(request);

    transactionsService.addTransaction(createTransactionRequest(accountResponse.accountId(), new BigDecimal("10.00"), CRDT));
    transactionsService.addTransaction(createTransactionRequest(accountResponse.accountId(), new BigDecimal("-10.00"), DBIT));

    Account account = accountsRepository.findAccountById(accountResponse.accountId());
    assertEquals(new BigDecimal("0.00"), account.getBalance());
  }

  private AddCustomerRequest createCustomerRequest() {
    return AddCustomerRequest.builder().firstName("Mari").lastName("Maasikas").customerType(CustomerType.PRIVATE).build();
  }

  private AddTransactionRequest createTransactionRequest(Long accountId, BigDecimal amount, CreditDebitIndicator creditDebitIndicator) {
    return AddTransactionRequest.builder()
        .accountId(accountId)
        .amount(amount)
        .currency(EUR)
        .sender("sender")
        .receiver("receiver")
        .description("description")
        .creditDebitIndicator(creditDebitIndicator)
        .build();
  }

}
package com.example.ivybankapi.accounts;

import com.example.ivybankapi.accounts.model.Account;
import com.example.ivybankapi.accounts.model.AddAccountRequest;
import com.example.ivybankapi.accounts.model.AddAccountResponse;
import com.example.ivybankapi.common.exception.CustomerNotFoundException;
import com.example.ivybankapi.customers.CustomersRepository;
import com.example.ivybankapi.customers.model.Customer;
import com.example.ivybankapi.utils.IbanUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountsService {

  private final AccountsRepository accountsRepository;
  private final CustomersRepository customersRepository;

  public AddAccountResponse addAccount(AddAccountRequest request) {
    Customer customer = customersRepository.getCustomerById(request.getCustomerId());
    if (customer == null) {
      throw new CustomerNotFoundException(String.format("Customer with id: %s not found", request.getCustomerId()));
    }
    String accountIban = IbanUtil.generateIban();
    String accountName = getAccountName(request.getAccountName(), customer, accountIban);
    Long accountId = accountsRepository.addAccount(customer.getId(), accountIban, accountName);
    return new AddAccountResponse(accountId, accountIban);
  }

  public Account getAccount(Long accountId) {
    return accountsRepository.findAccountById(accountId);
  }

  private String getAccountName(String accountName, Customer customer, String accountIban) {
    if (accountName != null && !accountName.isBlank()) {
      return  accountName;
    }
    return String.format("%s %s %s", customer.getFirstName(), customer.getLastName(), accountIban);
  }

}

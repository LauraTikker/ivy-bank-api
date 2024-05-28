package com.example.ivybankapi.accounts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.example.ivybankapi.accounts.model.AddAccountRequest;
import com.example.ivybankapi.accounts.model.AddAccountResponse;
import com.example.ivybankapi.common.exception.CustomerNotFoundException;
import com.example.ivybankapi.customers.CustomersRepository;
import com.example.ivybankapi.customers.model.Customer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountsServiceTest {

  @InjectMocks
  private AccountsService accountsService;

  @Mock
  private AccountsRepository accountsRepository;

  @Mock
  private CustomersRepository customersRepository;

  private static final Long CUSTOMER_ID = 12345L;

  @Test
  void addAccount() {
    AddAccountRequest request = AddAccountRequest.builder().customerId(CUSTOMER_ID).accountName("account-name").build();
    Customer customer = Customer.builder().id(CUSTOMER_ID).firstName("Mari").lastName("Maasikas").build();
    when(customersRepository.getCustomerById(CUSTOMER_ID)).thenReturn(customer);
    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    when(accountsRepository.addAccount(eq(CUSTOMER_ID), captor.capture(), eq("account-name"))).thenReturn(2222L);

    AddAccountResponse result = accountsService.addAccount(request);

    assertEquals(2222L, result.accountId());
    assertEquals(captor.getValue(), result.accountIban());
  }

  @Test
  void addAccount_withCustomerNotFound_shouldThrowCustomerNotFoundException() {
    AddAccountRequest request = AddAccountRequest.builder().customerId(CUSTOMER_ID).accountName("account-name").build();
    when(customersRepository.getCustomerById(CUSTOMER_ID)).thenReturn(null);

    CustomerNotFoundException exception = Assertions.assertThrows(CustomerNotFoundException.class, () ->
      accountsService.addAccount(request)
    );

    assertEquals("Customer with id: 12345 not found", exception.getMessage());
  }

  @Test
  void addAccount_withAccountNameNull_shouldCreateDefaultAccountName() {
    AddAccountRequest request = AddAccountRequest.builder().customerId(CUSTOMER_ID).accountName(null).build();
    Customer customer = Customer.builder().id(CUSTOMER_ID).firstName("Mari").lastName("Maasikas").build();
    when(customersRepository.getCustomerById(CUSTOMER_ID)).thenReturn(customer);
    ArgumentCaptor<String> ibanCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> accountNameCaptor = ArgumentCaptor.forClass(String.class);
    when(accountsRepository.addAccount(eq(CUSTOMER_ID), ibanCaptor.capture(), accountNameCaptor.capture())).thenReturn(2222L);

    accountsService.addAccount(request);

    assertEquals(String.format("Mari Maasikas %s", ibanCaptor.getValue()), accountNameCaptor.getValue());
  }

  @Test
  void addAccount_withAccountNameBlank_shouldCreateDefaultAccountName() {
    AddAccountRequest request = AddAccountRequest.builder().customerId(CUSTOMER_ID).accountName("").build();
    Customer customer = Customer.builder().id(CUSTOMER_ID).firstName("Mari").lastName("Maasikas").build();
    when(customersRepository.getCustomerById(CUSTOMER_ID)).thenReturn(customer);
    ArgumentCaptor<String> ibanCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> accountNameCaptor = ArgumentCaptor.forClass(String.class);
    when(accountsRepository.addAccount(eq(CUSTOMER_ID), ibanCaptor.capture(), accountNameCaptor.capture())).thenReturn(2222L);

    accountsService.addAccount(request);

    assertEquals(String.format("Mari Maasikas %s", ibanCaptor.getValue()), accountNameCaptor.getValue());
  }

}
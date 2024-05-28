package com.example.ivybankapi.customers;

import com.example.ivybankapi.customers.model.AddCustomerRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomersService {

  private final CustomersRepository customersRepository;

  public Long addCustomer(AddCustomerRequest addCustomerRequest) {
    return customersRepository.addCustomer(addCustomerRequest);
  }

}

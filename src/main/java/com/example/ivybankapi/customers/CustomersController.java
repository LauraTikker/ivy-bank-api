package com.example.ivybankapi.customers;

import com.example.ivybankapi.customers.model.AddCustomerResponse;
import com.example.ivybankapi.customers.model.AddCustomerRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CustomersController {

  private final CustomersService customersService;

  @PostMapping("/customer")
  public ResponseEntity<AddCustomerResponse> addCustomer(@Valid @RequestBody AddCustomerRequest addCustomerRequest) {
    Long customerId = customersService.addCustomer(addCustomerRequest);
    return ResponseEntity.ok(new AddCustomerResponse(customerId));
  }

}

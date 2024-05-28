package com.example.ivybankapi.customers;

import static com.example.ivybankapi.customers.model.CustomerType.BUSINESS;
import static com.example.ivybankapi.customers.model.CustomerType.PRIVATE;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.ivybankapi.customers.model.AddCustomerRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(CustomersController.class)
class CustomersControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private CustomersService customersService;

  private static final Long CUSTOMER_ID = 12345L;
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @Test
  void addCustomer() throws Exception {
    AddCustomerRequest request = AddCustomerRequest.builder().firstName("Mari").lastName("Maasikas").customerType(PRIVATE).build();
    when(customersService.addCustomer(request)).thenReturn(CUSTOMER_ID);
    mockMvc.perform(MockMvcRequestBuilders
            .post("/customer")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(OBJECT_MAPPER.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.customerId").value(CUSTOMER_ID));
  }

  @Test
  void addCustomer_withFirstNameBlank_shouldReturnBadRequest () throws Exception {
    AddCustomerRequest request = AddCustomerRequest.builder().firstName("").lastName("Maasikas").customerType(BUSINESS).build();
    when(customersService.addCustomer(request)).thenReturn(CUSTOMER_ID);
    mockMvc.perform(MockMvcRequestBuilders
            .post("/customer")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(OBJECT_MAPPER.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  void addCustomer_withFLastNameBlank_shouldReturnBadRequest () throws Exception {
    AddCustomerRequest request = AddCustomerRequest.builder().firstName("Mari").lastName("").customerType(BUSINESS).build();
    when(customersService.addCustomer(request)).thenReturn(CUSTOMER_ID);
    mockMvc.perform(MockMvcRequestBuilders
            .post("/customer")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(OBJECT_MAPPER.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }
}
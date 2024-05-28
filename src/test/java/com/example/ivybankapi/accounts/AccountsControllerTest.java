package com.example.ivybankapi.accounts;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.ivybankapi.accounts.model.Account;
import com.example.ivybankapi.accounts.model.AddAccountRequest;
import com.example.ivybankapi.accounts.model.AddAccountResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(AccountsController.class)
class AccountsControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private AccountsService accountsService;

  private static final String IBAN = "EE987765705251347771";
  private static final Long ACCOUNT_ID = 345L;
  private static final Long CUSTOMER_ID = 12345L;
  private static final String ACCOUNT_NAME = "account name";
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @Test
  void addAccount() throws Exception {
    AddAccountRequest request = AddAccountRequest.builder().customerId(CUSTOMER_ID).accountName(ACCOUNT_NAME).build();
    when(accountsService.addAccount(request)).thenReturn(new AddAccountResponse(ACCOUNT_ID, IBAN));
    mockMvc.perform(MockMvcRequestBuilders
            .post("/account")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(OBJECT_MAPPER.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.accountId").value(ACCOUNT_ID))
        .andExpect(MockMvcResultMatchers.jsonPath("$.accountIban").value(IBAN));
  }

  @Test
  void addAccount_withCustomerIdNull_shouldReturnBadRequest() throws Exception {
    AddAccountRequest request = AddAccountRequest.builder().accountName(ACCOUNT_NAME).build();
    when(accountsService.addAccount(request)).thenReturn(new AddAccountResponse(ACCOUNT_ID, IBAN));
    mockMvc.perform(MockMvcRequestBuilders
            .post("/account")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(OBJECT_MAPPER.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  void getAccount() throws Exception {
    when(accountsService.getAccount(ACCOUNT_ID)).thenReturn(createAccount());
    mockMvc.perform(MockMvcRequestBuilders
            .get(String.format("/account/%s", ACCOUNT_ID))
            .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(ACCOUNT_ID))
        .andExpect(MockMvcResultMatchers.jsonPath("$.customerId").value(CUSTOMER_ID))
        .andExpect(MockMvcResultMatchers.jsonPath("$.balance").value(new BigDecimal("20.0")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.iban").value(IBAN))
        .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(ACCOUNT_NAME))
        .andExpect(MockMvcResultMatchers.jsonPath("$.active").value(true));
  }

  private Account createAccount() {
    return Account.builder()
        .id(ACCOUNT_ID)
        .balance(new BigDecimal("20.0"))
        .iban(IBAN)
        .active(true)
        .customerId(CUSTOMER_ID)
        .name(ACCOUNT_NAME)
        .build();
  }

}
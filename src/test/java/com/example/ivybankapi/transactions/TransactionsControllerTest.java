package com.example.ivybankapi.transactions;

import static com.example.ivybankapi.transactions.model.CreditDebitIndicator.CRDT;
import static com.example.ivybankapi.transactions.model.Currency.EUR;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.ivybankapi.transactions.model.AddTransactionRequest;
import com.example.ivybankapi.transactions.model.AddTransactionResponse;
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

@WebMvcTest(TransactionsController.class)
class TransactionsControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private TransactionsService transactionsService;

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final Long TRANSACTION_ID = 5555L;

  @Test
  void addTransaction() throws Exception  {
    AddTransactionRequest request = AddTransactionRequest.builder()
        .accountId(123L)
        .amount(BigDecimal.TEN)
        .currency(EUR)
        .sender("sender")
        .receiver("receiver")
        .description("description")
        .creditDebitIndicator(CRDT)
        .build();
    when(transactionsService.addTransaction(request)).thenReturn(new AddTransactionResponse(TRANSACTION_ID));
    mockMvc.perform(MockMvcRequestBuilders
            .post("/transaction")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(OBJECT_MAPPER.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.transactionId").value(TRANSACTION_ID));
  }
}
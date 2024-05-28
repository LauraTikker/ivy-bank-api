package com.example.ivybankapi.transactionhistory;

import static com.example.ivybankapi.transactions.model.Currency.EUR;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.ivybankapi.transactionhistory.model.TransactionHistory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(TransactionHistoryController.class)
class TransactionHistoryControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private TransactionHistoryService transactionHistoryService;

  private static final Long ACCOUNT_ID = 1234L;
  private static final OffsetDateTime DATE = OffsetDateTime.parse("2024-01-14T11:00:12+03:00");

  @Test
  void addTransaction() throws Exception {
    when(transactionHistoryService.getTransactionHistory(ACCOUNT_ID, LocalDate.parse("2024-01-13"), LocalDate.parse("2024-01-15")))
        .thenReturn(Collections.singletonList(createTransactionHistory()));
    mockMvc.perform(MockMvcRequestBuilders
            .get("/transaction-history")
            .accept(MediaType.APPLICATION_JSON)
            .queryParam("accountId", "1234")
            .queryParam("fromDate", "2024-01-13")
            .queryParam("toDate", "2024-01-15"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].amount").value(BigDecimal.TEN))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].transactionId").value(12L))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].currency").value(EUR.name()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].description").value("transaction 12"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].date").value(DATE.toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].accountId").value(ACCOUNT_ID));
  }

  private TransactionHistory createTransactionHistory() {
    return new TransactionHistory()
        .setTransactionId(12L)
        .setDate(DATE)
        .setAccountId(ACCOUNT_ID)
        .setCurrency(EUR)
        .setAmount(BigDecimal.TEN)
        .setDescription("transaction 12");
  }
}
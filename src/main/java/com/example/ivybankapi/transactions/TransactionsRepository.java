package com.example.ivybankapi.transactions;

import com.example.ivybankapi.transactionhistory.model.TransactionHistory;
import com.example.ivybankapi.transactions.model.Currency;
import com.example.ivybankapi.transactions.model.Transaction;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TransactionsRepository {
  
  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  public Long addTransaction(Transaction transaction) {
    String insertTransactionSql = "INSERT INTO ivybank.transactions (account_id, amount, amount_in_euros, currency, sender, receiver, initial_balance, final_balance, description, credit_debit_indicator)\n"
        + "VALUES (:accountId, :amount, :amountInEuros, :currency, :sender, :receiver, :initialBalance, :finalBalance, :description, :creditDebitIndicator) RETURNING id;";

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("accountId", transaction.getAccountId());
    params.addValue("amount", transaction.getAmount());
    params.addValue("amountInEuros", transaction.getAmountInEuros());
    params.addValue("currency", transaction.getCurrency(), Types.VARCHAR);
    params.addValue("sender", transaction.getSender());
    params.addValue("receiver", transaction.getReceiver());
    params.addValue("initialBalance", transaction.getInitialBalance());
    params.addValue("finalBalance", transaction.getFinalBalance());
    params.addValue("description", transaction.getDescription());
    params.addValue("creditDebitIndicator", transaction.getCreditDebitIndicator(), Types.VARCHAR);

    return namedParameterJdbcTemplate.queryForObject(insertTransactionSql, params, Long.class);
  }

  public List<TransactionHistory> findTransactionsByAccountId(Long accountId, LocalDate fromDate, LocalDate toDate) {
    String findAccountSql = "SELECT * FROM ivybank.transactions WHERE account_id = :accountId "
        + "AND ((created_at AT TIME ZONE 'Europe/Tallinn')::date >= :fromDate OR :fromDate IS NULL)"
        + " AND ((created_at AT TIME ZONE 'Europe/Tallinn')::date <= :toDate OR :toDate IS NULL) ORDER BY created_at;";

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("accountId", accountId);
    params.addValue("fromDate", fromDate, Types.DATE);
    params.addValue("toDate", toDate, Types.DATE);

    return namedParameterJdbcTemplate.query(findAccountSql, params, this::transactionHistoryRowMapper);
  }

  private TransactionHistory transactionHistoryRowMapper(ResultSet rs, int i) throws SQLException {
    TransactionHistory transactionHistory = new TransactionHistory();
    transactionHistory.setTransactionId(rs.getLong("id"));
    transactionHistory.setAccountId(rs.getLong("account_id"));
    transactionHistory.setDate(OffsetDateTime.ofInstant(rs.getTimestamp("created_At").toInstant(), ZoneId.systemDefault()));
    transactionHistory.setAmount(rs.getBigDecimal("amount"));
    transactionHistory.setCurrency(Currency.valueOf(rs.getString("currency")));
    transactionHistory.setDescription(rs.getString("description"));
    return transactionHistory;
  }

}

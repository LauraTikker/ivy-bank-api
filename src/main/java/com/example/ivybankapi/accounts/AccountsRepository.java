package com.example.ivybankapi.accounts;

import static org.springframework.dao.support.DataAccessUtils.singleResult;
import static org.springframework.jdbc.core.BeanPropertyRowMapper.newInstance;

import com.example.ivybankapi.accounts.model.Account;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AccountsRepository {

  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  public Long addAccount(Long customerId, String accountIban, String accountName) {
    String insertAccountSql = "INSERT INTO ivybank.accounts(customer_id, iban, name) VALUES (:customerId, :iban, :name) RETURNING id";

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("customerId", customerId);
    params.addValue("iban", accountIban);
    params.addValue("name", accountName);

    return namedParameterJdbcTemplate.queryForObject(insertAccountSql, params, Long.class);
  }

  public Account findAccountById(Long accountId) {
    String findAccountSql = "SELECT * FROM ivybank.accounts WHERE id = :accountId AND active IS TRUE";

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("accountId", accountId);

    return singleResult(namedParameterJdbcTemplate.query(findAccountSql, params, newInstance(Account.class)));
  }

  public void updateAccountBalance(Long accountId, BigDecimal accountBalance) {
    String updateAccountBalanceSql = "UPDATE ivybank.accounts SET balance = :accountBalance WHERE id = :accountId";

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("accountId", accountId);
    params.addValue("accountBalance", accountBalance);

    namedParameterJdbcTemplate.update(updateAccountBalanceSql, params);
  }

}

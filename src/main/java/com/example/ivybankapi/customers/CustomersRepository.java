package com.example.ivybankapi.customers;

import static org.springframework.dao.support.DataAccessUtils.singleResult;
import static org.springframework.jdbc.core.BeanPropertyRowMapper.newInstance;

import com.example.ivybankapi.customers.model.AddCustomerRequest;
import com.example.ivybankapi.customers.model.Customer;
import java.sql.Types;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomersRepository {

  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  public Long addCustomer(AddCustomerRequest request) {
    String insertUserSql = "INSERT INTO ivybank.customers(first_name, last_name, customer_type) VALUES (:firstName, :lastName, :customerType) RETURNING id";

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("firstName", request.getFirstName());
    params.addValue("lastName", request.getLastName());
    params.addValue("customerType", request.getCustomerType(), Types.VARCHAR);

    return namedParameterJdbcTemplate.queryForObject(insertUserSql, params, Long.class);
  }

  public Customer getCustomerById(Long customerId) {
    String getCustomerByIdSql = "SELECT * FROM ivybank.customers WHERE id = :customerId";

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("customerId", customerId);

    return singleResult(namedParameterJdbcTemplate.query(getCustomerByIdSql, params, newInstance(Customer.class)));
  }

}

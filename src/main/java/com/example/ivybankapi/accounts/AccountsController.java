package com.example.ivybankapi.accounts;

import com.example.ivybankapi.accounts.model.Account;
import com.example.ivybankapi.accounts.model.AddAccountRequest;
import com.example.ivybankapi.accounts.model.AddAccountResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AccountsController {

  private final AccountsService accountsService;

  @PostMapping("/account")
  public ResponseEntity<AddAccountResponse> addAccount(@Valid @RequestBody AddAccountRequest request) {
    return ResponseEntity.ok().body(accountsService.addAccount(request));
  }

  @GetMapping("/account/{accountId}")
  public ResponseEntity<Account> getAccount(@PathVariable Long accountId) {
    return ResponseEntity.ok().body(accountsService.getAccount(accountId));
  }

}

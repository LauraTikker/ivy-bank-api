package com.example.ivybankapi.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class IbanUtilTest {

  @Test
  void generateIban() {
    String iban = IbanUtil.generateIban();
    System.out.println(iban);
    assertEquals(20, iban.length());
    assertEquals(1, IbanUtil.validateIban(iban));
  }

}
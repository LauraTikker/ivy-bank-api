package com.example.ivybankapi.utils;

import java.math.BigInteger;
import java.util.Random;
import lombok.experimental.UtilityClass;

@UtilityClass
public class IbanUtil {

  private static final String COUNTRY_CODE = "EE";
  private static final String COUNTRY_CODE_AS_NUMBERS = "1414";
  private static final int BANK_CODE = 77;
  private static final String INITIAL_CHECK_DIGITS_VALUE = "00";
  private static final int MOD_97 = 97;
  private static final Random RANDOM = new Random();

  public static String generateIban() {
    String domesticBankAccountNumber = String.valueOf(RANDOM.nextLong(10000000000000L, 99999999999999L));
    String checkDigit = calculateCheckDigits(domesticBankAccountNumber);

    return COUNTRY_CODE + checkDigit + BANK_CODE + domesticBankAccountNumber;
  }

  private static String calculateCheckDigits(String domesticBankAccountNumber) {
    String numericValueOfIban = BANK_CODE + domesticBankAccountNumber + COUNTRY_CODE_AS_NUMBERS + INITIAL_CHECK_DIGITS_VALUE;
    BigInteger mod97 = new BigInteger(numericValueOfIban).mod(BigInteger.valueOf(MOD_97));
    String checkDigits = String.valueOf(98L - mod97.longValue());
    if (checkDigits.length() == 1) {
      return "0" + checkDigits;
    }
    return checkDigits;
  }

  public static int validateIban(String iban) {
    String checkDigits = (String) iban.subSequence(2, 4);
    String domesticBankAccountNumber = (String) iban.subSequence(4, iban.length());
    BigInteger numericValueOfIban = new BigInteger(domesticBankAccountNumber + COUNTRY_CODE_AS_NUMBERS + checkDigits);
    return numericValueOfIban.mod(BigInteger.valueOf(97)).intValue();
  }

}

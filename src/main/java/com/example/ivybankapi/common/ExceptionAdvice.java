package com.example.ivybankapi.common;

import com.example.ivybankapi.common.exception.AccountNotFoundException;
import com.example.ivybankapi.common.exception.CustomerNotFoundException;
import com.example.ivybankapi.common.exception.InsufficientFundsException;
import com.example.ivybankapi.common.exception.InvalidAmountException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice
public class ExceptionAdvice {

  @ExceptionHandler(InsufficientFundsException.class)
  @ResponseBody
  public ErrorDto insufficientFundsExceptionHandler(InsufficientFundsException ex, HttpServletResponse resp) {
    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    logError(400, ex);
    return new ErrorDto("400", ex.getMessage());
  }

  @ExceptionHandler(InvalidAmountException.class)
  @ResponseBody
  public ErrorDto invalidAmountExceptionHandler(InvalidAmountException ex, HttpServletResponse resp) {
    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    logError(400, ex);
    return new ErrorDto("400", ex.getMessage());
  }

  @ExceptionHandler(AccountNotFoundException.class)
  @ResponseBody
  public ErrorDto accountNotFoundExceptionHandler(AccountNotFoundException ex, HttpServletResponse resp) {
    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    logError(404, ex);
    return new ErrorDto("404", ex.getMessage());
  }

  @ExceptionHandler(CustomerNotFoundException.class)
  @ResponseBody
  public ErrorDto customerNotFoundExceptionHandler(CustomerNotFoundException ex, HttpServletResponse resp) {
    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    logError(404, ex);
    return new ErrorDto("404", ex.getMessage());
  }

  void logError(int errorCode, Exception ex) {
    log.error("Error {}: {}", errorCode, ex.getMessage(), ex);
  }
}

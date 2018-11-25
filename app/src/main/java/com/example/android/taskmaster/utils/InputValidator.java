package com.example.android.taskmaster.utils;

import android.text.TextUtils;

import org.apache.commons.validator.routines.EmailValidator;

public class InputValidator
{
  private InputValidator()
  {

  }

  public static String sanitizeInput(String input)
  {
    return input.trim();
  }

  public static boolean isValidInput(String input)
  {
    return !TextUtils.isEmpty(input);
  }

  public static boolean isEmailValid(String input)
  {
    return EmailValidator.getInstance().isValid(input);
  }
}

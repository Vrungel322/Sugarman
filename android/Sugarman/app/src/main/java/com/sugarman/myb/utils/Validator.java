package com.sugarman.myb.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nikita on 23.10.2017.
 */

public class Validator {

  public static boolean isEmailValid(String email) {
    String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
    Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(email);
    return matcher.matches();
  }

  public static boolean isPhoneValid(String phone) {
    if (phone.equals("none")) {
      return true;
    } else {
      String expression = "^[+][0-9]{8,15}$";
      Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
      Matcher matcher = pattern.matcher(phone);
      return matcher.matches();
    }
  }
}

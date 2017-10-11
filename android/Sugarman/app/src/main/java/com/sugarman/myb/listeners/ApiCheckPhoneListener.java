package com.sugarman.myb.listeners;

import java.util.List;

public interface ApiCheckPhoneListener extends ApiBaseListener {

  void onApiCheckPhoneSuccess(List<String> phones);

  void onApiCheckPhoneFailure(String message);
}

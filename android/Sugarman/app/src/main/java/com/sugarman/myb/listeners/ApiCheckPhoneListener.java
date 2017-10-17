package com.sugarman.myb.listeners;

import com.sugarman.myb.api.models.responses.Phones;
import java.util.List;

public interface ApiCheckPhoneListener extends ApiBaseListener {

  void onApiCheckPhoneSuccess(List<Phones> phones);

  void onApiCheckPhoneFailure(String message);
}

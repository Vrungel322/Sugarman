package com.sugarman.myb.listeners;

import java.util.List;

public interface ApiCheckVkListener extends ApiBaseListener {

  void onApiCheckVkSuccess(List<String> vks);

  void onApiCheckVkFailure(String message);
}

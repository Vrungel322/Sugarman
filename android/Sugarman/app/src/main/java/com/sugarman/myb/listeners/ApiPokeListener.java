package com.sugarman.myb.listeners;

public interface ApiPokeListener extends ApiBaseListener {

  void onApiPokeSuccess();

  void onApiPokeFailure(String message);
}

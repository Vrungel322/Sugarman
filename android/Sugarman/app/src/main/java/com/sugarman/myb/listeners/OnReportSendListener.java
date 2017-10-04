package com.sugarman.myb.listeners;

public interface OnReportSendListener {

  void onApiReportSendSuccess(int todaySteps, int steps, int currentDay);

  void onApiReportSendFailure(int addedSteps);
}

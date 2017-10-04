package com.sugarman.myb.listeners;

public interface ApiReportStatsListener extends ApiBaseListener {

  void onApiReportStatsSuccess();

  void onApiReportStatsFailure(String message);
}

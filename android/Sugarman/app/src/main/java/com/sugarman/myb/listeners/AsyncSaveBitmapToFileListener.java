package com.sugarman.myb.listeners;

public interface AsyncSaveBitmapToFileListener {

  void onAsyncBitmapSaveSuccess(String path);

  void onAsyncBitmapSaveFailed();
}

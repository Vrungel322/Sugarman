package com.sugarman.myb.utils;

import com.sugarman.myb.App;

public abstract class SoundHelper {

  private static int mCurrentStreamId = -1;

  public SoundHelper() {
    // only static methods and fields
  }

  public static void play(int id) {
    if (id != -1) {
      mCurrentStreamId = App.getSoundPoolInstance().play(id, 1f, 1f, 0, 0, 1);
    }
  }

  private static void stopPlay() {
    if (mCurrentStreamId != -1) {
      App.getSoundPoolInstance().stop(mCurrentStreamId);
    }
  }
}

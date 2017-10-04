package com.sugarman.myb.eventbus.events;

public class DebugCacheStepsEvent {
  private final int cache;

  public DebugCacheStepsEvent(int cache) {
    this.cache = cache;
  }

  public int getCache() {
    return cache;
  }
}

package com.sugarman.myb.utils;

/**
 * Created by nikita on 19.03.2018.
 */

public class Randomizer {

  public static float getRandom(float range, float startsfrom) {
    return (float) (Math.random() * range) + startsfrom;
  }
}

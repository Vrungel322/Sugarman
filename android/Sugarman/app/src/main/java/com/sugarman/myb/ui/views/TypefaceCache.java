package com.sugarman.myb.ui.views;

import android.graphics.Typeface;
import java.util.HashMap;
import java.util.Map;

class TypefaceCache {

  private static final Map<String, Typeface> sFontCache = new HashMap<>(10);

  public static Typeface get(String name) {
    return sFontCache.get(name);
  }

  public static void put(String name, Typeface typeface) {
    sFontCache.put(name, typeface);
  }
}
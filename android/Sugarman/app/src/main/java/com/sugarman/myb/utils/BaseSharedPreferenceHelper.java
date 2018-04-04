package com.sugarman.myb.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Pair;
import com.sugarman.myb.App;
import com.sugarman.myb.constants.SharedPreferenceConstants;
import java.util.Date;

public abstract class BaseSharedPreferenceHelper {

  private static volatile SharedPreferences instancePrefs;

  public static synchronized SharedPreferences getPrefsInstance() {
    SharedPreferences localInstance = instancePrefs;
    if (localInstance == null) {
      synchronized (BaseSharedPreferenceHelper.class) {
        localInstance = instancePrefs;
        if (localInstance == null) {
          Context context = App.getInstance();
          instancePrefs = PreferenceManager.getDefaultSharedPreferences(context);
          localInstance = instancePrefs;
        }
      }
    }
    return localInstance;
  }

  //base functionality
  static boolean getBoolean(String key, boolean defaultValue) {
    return getPrefsInstance().getBoolean(key, defaultValue);
  }

  static void putBoolean(String key, boolean value) {
    SharedPreferences.Editor editor = getPrefsInstance().edit();
    editor.putBoolean(key, value);
    editor.apply();
  }

  static int getInt(String key, int defaultValue) {
    return getPrefsInstance().getInt(key, defaultValue);
  }

  static void putDouble(String key, double value) {
    SharedPreferences.Editor editor = getPrefsInstance().edit();
    editor.putLong(key, Double.doubleToRawLongBits(value));
    editor.apply();
  }

  static double getDouble(String key, double defaultValue) {
    return Double.longBitsToDouble(
        getPrefsInstance().getLong(key, Double.doubleToLongBits(defaultValue)));
  }

  static void putInt(String key, int value) {
    SharedPreferences.Editor editor = getPrefsInstance().edit();
    editor.putInt(key, value);
    editor.apply();
  }

  static void removeAllKeysStartingWith(String string) {
    SharedPreferences.Editor editor = getPrefsInstance().edit();
    for (String key : getPrefsInstance().getAll().keySet()) {
      if (key.startsWith(string)) {
        editor.remove(key);
      }
    }
    editor.apply();
  }

  static long getLong(String key, long defaultValue) {
    return getPrefsInstance().getLong(key, defaultValue);
  }

  static void putLong(String key, long value) {
    SharedPreferences.Editor editor = getPrefsInstance().edit();
    editor.putLong(key, value);
    editor.apply();
  }

  static String getString(String key, String defaultValue) {
    return getPrefsInstance().getString(key, defaultValue);
  }

  static void putString(String key, String value) {
    SharedPreferences.Editor editor = getPrefsInstance().edit();
    editor.putString(key, value);
    editor.apply();
  }

  static float getFloat(String key, float defaultValue) {
    return getPrefsInstance().getFloat(key, defaultValue);
  }

  static void putFloat(String key, float value) {
    SharedPreferences.Editor editor = getPrefsInstance().edit();
    editor.putFloat(key, value);
    editor.apply();
  }

  static void putDate(String key, Date date) {
    SharedPreferences.Editor editor = getPrefsInstance().edit();
    long timestamp = date == null ? -1 : date.getTime();
    editor.putLong(key, timestamp);
    editor.apply();
  }

  static Date getDate(String key) {
    long timestamp = getPrefsInstance().getLong(key, -1L);
    return timestamp == -1 ? null : new Date(timestamp);
  }

  static Pair<String, Integer> getPair(String key) {
    int value = getInt(key + SharedPreferenceConstants.VALUE, -1);
    String unit = getString(key + SharedPreferenceConstants.UNIT, "");

    return new Pair<>(unit, value);
  }

  static void putPair(String key, Pair<String, Integer> pair) {
    SharedPreferences.Editor editor = getPrefsInstance().edit();
    editor.putString(key + SharedPreferenceConstants.UNIT, pair.first);
    editor.putInt(key + SharedPreferenceConstants.VALUE, pair.second);
    editor.apply();
  }
}

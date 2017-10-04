package com.sugarman.myb.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;

/**
 * Created by John on 26.01.2017.
 */

public class PreferencesHelper {

  public static final String PREF_FILE_NAME = "com.sugarman.myb";

  private final SharedPreferences mPreferences;
  private Gson mGson;

  public PreferencesHelper(Context context, Gson gson) {
    mPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
    mGson = gson;
  }

  public void clear() {
    mPreferences.edit().clear().apply();
  }



}

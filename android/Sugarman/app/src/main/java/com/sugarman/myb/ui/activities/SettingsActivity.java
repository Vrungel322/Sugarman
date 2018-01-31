package com.sugarman.myb.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AppsFlyerLib;
import com.sugarman.myb.App;
import com.sugarman.myb.R;
import com.sugarman.myb.constants.Config;
import com.sugarman.myb.ui.activities.mainScreeen.MainActivity;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Y500 on 15.06.2017.
 */

public class SettingsActivity extends PreferenceActivity
    implements SharedPreferences.OnSharedPreferenceChangeListener {

  boolean needsRestart = false;

  ListPreference lang;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.language_preferences);
    lang = (ListPreference) findPreference("pref_app_language");
    Preference termsOfUse = findPreference("terms_of_use");
    termsOfUse.setOnPreferenceClickListener(preference -> {
      String url = Config.TERMS_OF_USE_URL;
      Intent i = new Intent(Intent.ACTION_VIEW);
      i.setData(Uri.parse(url));
      startActivity(i);
      Map<String, Object> eventValue = new HashMap<>();
      eventValue.put(AFInAppEventParameterName.LEVEL, 9);
      eventValue.put(AFInAppEventParameterName.SCORE, 100);
      AppsFlyerLib.getInstance()
          .trackEvent(App.getInstance().getApplicationContext(), "af_open_terms_of_use",
              eventValue);
      return true;
    });

    Preference privacyPolicy = findPreference("privacy_policy");
    privacyPolicy.setOnPreferenceClickListener(preference -> {
      Map<String, Object> eventValues = new HashMap<>();
      eventValues.put(AFInAppEventParameterName.LEVEL, 9);
      eventValues.put(AFInAppEventParameterName.SCORE, 100);
      AppsFlyerLib.getInstance()
          .trackEvent(App.getInstance().getApplicationContext(), "af_open_private_policy",
              eventValues);
      String url = Config.PRIVACY_POLICY;
      Intent i = new Intent(Intent.ACTION_VIEW);
      i.setData(Uri.parse(url));
      startActivity(i);
      return true;
    });
  }

  @Override protected void onResume() {
    super.onResume();
    getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
  }

  @Override protected void onPause() {
    super.onPause();
    getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
  }

  @Override public void onBackPressed() {
    super.onBackPressed();
    if (needsRestart) {

      Map<String, Object> eventValue = new HashMap<>();
      eventValue.put(AFInAppEventParameterName.LEVEL, 9);
      eventValue.put(AFInAppEventParameterName.SCORE, 100);
      AppsFlyerLib.getInstance()
          .trackEvent(App.getInstance().getApplicationContext(), "af_change_language", eventValue);

      Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
      startActivity(intent);
    }
  }

  @Override public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
    System.out.println("CHANGED PREFERENCES HELLO!!!");
    if (s.equals("pref_app_language") || s.equals("isInvitesMenuEnabled") || s.equals(
        "isChatEnabled")) {
      needsRestart = true;
    }
    //if (s.equals("pref_app_language")) lang.setTitle(SharedPreferenceHelper.getLanguage());
  }
}

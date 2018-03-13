package com.sugarman.myb.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import java.util.Locale;
import timber.log.Timber;

/**
 * Created by alexandersvyatetsky on 21/07/17.
 */

public class LocaleHelper {

  public static Context setLocale(Context context, String language) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      Timber.e("setLocale not legacy");

      return updateResources(context, language);
    }
    Timber.e("setLocale legacy");

    return updateResourcesLegacy(context, language);
  }

  @TargetApi(Build.VERSION_CODES.N)
  private static Context updateResources(Context context, String language) {
    Locale locale = new Locale(language);
    Locale.setDefault(locale);
    Configuration config = new Configuration();
    config.locale = locale;
    context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    return context;
  }

  @SuppressWarnings("deprecation")
  private static Context updateResourcesLegacy(Context context, String language) {
    Locale locale = new Locale(language);
    Locale.setDefault(locale);

    Resources resources = context.getResources();

    Configuration configuration = resources.getConfiguration();
    configuration.locale = locale;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      configuration.setLayoutDirection(locale);
    }

    resources.updateConfiguration(configuration, resources.getDisplayMetrics());

    return context;
  }
}

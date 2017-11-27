package com.sugarman.myb.utils.licence;

import android.content.Context;
import android.text.TextUtils;

/**
 * Created by yegoryeriomin on 11/27/17.
 */

public class LicenceChecker {

  public static boolean isStoreVersion(Context context) {
    boolean result = false;

    try {
      String installer = context.getPackageManager()
          .getInstallerPackageName(context.getPackageName());
      result = !TextUtils.isEmpty(installer);
    } catch (Throwable e) {
    }

    return result;
  }

}

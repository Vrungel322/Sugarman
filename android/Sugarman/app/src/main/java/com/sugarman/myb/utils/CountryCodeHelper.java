package com.sugarman.myb.utils;

import android.content.Context;
import android.telephony.TelephonyManager;
import com.sugarman.myb.App;
import com.sugarman.myb.R;

/**
 * Created by nikita on 03.10.2017.
 */

public class CountryCodeHelper {

  public static String getCountryZipCode() {
    String CountryID = "";
    String CountryZipCode = "";

    TelephonyManager manager =
        (TelephonyManager) App.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
    //getNetworkCountryIso
    CountryID = manager.getSimCountryIso().toUpperCase();
    String[] rl = App.getInstance().getResources().getStringArray(R.array.CountryCodes);
    for (int i = 0; i < rl.length; i++) {
      String[] g = rl[i].split(",");
      if (g[1].trim().equals(CountryID.trim())) {
        CountryZipCode = "+"+g[0];
        break;
      }
    }
    return CountryZipCode;
  }
}

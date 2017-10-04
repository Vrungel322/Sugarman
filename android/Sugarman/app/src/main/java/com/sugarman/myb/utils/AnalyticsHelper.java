package com.sugarman.myb.utils;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.InviteEvent;
import com.crashlytics.android.answers.LevelStartEvent;
import com.crashlytics.android.answers.LoginEvent;
import com.crashlytics.android.answers.SignUpEvent;
import com.sugarman.myb.BuildConfig;
import com.sugarman.myb.constants.Constants;

public abstract class AnalyticsHelper {

  private AnalyticsHelper() {
    // only static methods and fields
  }

  public static void reportLogin(boolean isSuccess) {
    if (!BuildConfig.DEBUG) {
      if (SharedPreferenceHelper.isFirstLogin()) {
        Answers.getInstance()
            .logSignUp(new SignUpEvent().putMethod(Constants.SIGN_UP).putSuccess(isSuccess));
      } else {
        Answers.getInstance()
            .logLogin(new LoginEvent().putMethod(Constants.LOGIN).putSuccess(isSuccess));
      }
    }

    if (isSuccess) {
      SharedPreferenceHelper.saveIsFirstLogin(false);
    }
  }

  public static void reportChallenge() {
    if (!BuildConfig.DEBUG) {
      Answers.getInstance().logLevelStart(new LevelStartEvent().putLevelName(Constants.CHALLENGE));
    }
  }

  public static void reportInvite() {
    Answers.getInstance().logInvite(new InviteEvent().putMethod(Constants.INVITE));
  }
}

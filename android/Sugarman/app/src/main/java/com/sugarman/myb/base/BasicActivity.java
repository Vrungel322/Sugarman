package com.sugarman.myb.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.widget.Toast;
import butterknife.ButterKnife;
import com.arellomobile.mvp.MvpAppCompatActivity;
import com.sugarman.myb.App;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.models.custom_events.CustomUserEvent;
import com.sugarman.myb.models.custom_events.IActionOnCurrentScreen;
import com.sugarman.myb.ui.dialogs.dialogCuteRule.DialogCuteRule;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by John on 27.01.2017.
 */

public abstract class BasicActivity extends MvpAppCompatActivity {

  public static final String POPUP_ACTION = "popup";
  public static final String ANIMATION_ACTION = "animation";
  @Inject protected Context mContext;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ButterKnife.bind(this);
    App.getAppComponent().inject(this);

    Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
  }

  protected void showToastMessage(String message) {
    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
  }

  protected void showToastMessage(@StringRes int id) {
    Toast.makeText(mContext, id, Toast.LENGTH_SHORT).show();
  }

  protected void doEventAction(CustomUserEvent customEvent,
      @Nullable IActionOnCurrentScreen actionOnCurrentScreen) {
    switch (customEvent.getStrType()) {
      case POPUP_ACTION: {
        // event to show simple info dialog
        StringBuilder body = new StringBuilder();
        if (customEvent.getEventExtraStrings() != null && !customEvent.getEventExtraStrings()
            .isEmpty()) {
          customEvent.getEventExtraStrings().forEach(body::append);
        }
        body.append(customEvent.getEventText());
        SharedPreferenceHelper.setEventXStepsDone(customEvent.getNumValue());
        Timber.e("doEventAction", +customEvent.getNumValue());
        DialogCuteRule.newInstance(body.toString(), customEvent.getStrValue() ,() -> {
          if (customEvent.getEventName().equals(Constants.EVENT_X_NEW_USERS_INVITE)) {
            SharedPreferenceHelper.setEventGroupWithXNewUsersDone();
          }
        }).show(getFragmentManager(), "DialogCuteRule");
        break;
      }
      case ANIMATION_ACTION: {
        // animation on specific
        if (actionOnCurrentScreen != null) {
          actionOnCurrentScreen.action();
        }
        break;
      }
      case "qq": {
        // event to show some activity
        break;
      }
    }
  }
}

package com.sugarman.myb.ui.activities.base;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import com.appsflyer.AppsFlyerLib;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.facebook.login.LoginManager;
import com.instacart.library.truetime.TrueTime;
import com.sugarman.myb.App;
import com.sugarman.myb.R;
import com.sugarman.myb.api.clients.RefreshUserDataClient;
import com.sugarman.myb.api.clients.SendFirebaseTokenClient;
import com.sugarman.myb.base.BasicActivity;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.constants.DialogConstants;
import com.sugarman.myb.eventbus.events.ChangeConnectionEvent;
import com.sugarman.myb.eventbus.events.RefreshGCMTokenEvent;
import com.sugarman.myb.listeners.ApiBaseListener;
import com.sugarman.myb.listeners.ApiSendFirebaseTokenListener;
import com.sugarman.myb.listeners.SugarmanDialogListener;
import com.sugarman.myb.services.MasterStepDetectorService;
import com.sugarman.myb.tasks.DeleteFCMTokenAsyncTask;
import com.sugarman.myb.ui.activities.LoginActivity;
import com.sugarman.myb.ui.activities.SplashActivity;
import com.sugarman.myb.ui.dialogs.DialogButton;
import com.sugarman.myb.ui.dialogs.SugarmanDialog;
import com.sugarman.myb.ui.fragments.BaseFragment;
import com.sugarman.myb.ui.fragments.ProgressFragment;
import com.sugarman.myb.ui.fragments.SugarmanProgressFragment;
import com.sugarman.myb.utils.DeviceHelper;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import com.vk.sdk.VKSdk;
import java.io.IOException;
import java.util.Locale;
import org.greenrobot.eventbus.Subscribe;
import timber.log.Timber;

// activity must contains view with id fragment_container
public abstract class BaseActivity extends BasicActivity
    implements ApiBaseListener, SugarmanDialogListener, IBaseActivityView {

  private static final String TAG = BaseActivity.class.getName();
  @InjectPresenter BasicActivityPresenter presenter;
  SugarmanDialog noNetworkDialog;
  RefreshUserDataClient mRefreshUserDataClient = new RefreshUserDataClient();
  private SugarmanProgressFragment mSugarmanProgressFragment;
  private ProgressFragment mProgressFragment;
  private View connErrorLayout;
  private boolean isOverlayFragmentShowed = false;
  private SendFirebaseTokenClient mSendFirebaseTokenClient;
  private final ApiSendFirebaseTokenListener sendFirebaseTokenListener =
      new ApiSendFirebaseTokenListener() {

        @Override public void onApiUnauthorized() {
          Timber.e("onApiUnauthorized FireBase");
          logout();
        }

        @Override public void onUpdateOldVersion() {
          showUpdateOldVersionDialog();
        }

        @Override public void onApiSendFirebaseTokenSuccess() {
          SharedPreferenceHelper.saveGCMToken("");

          if (mSendFirebaseTokenClient != null) {
            mSendFirebaseTokenClient.unregisterListener();
          }
        }

        @Override public void onApiSendFirebaseTokenFailure(String message) {
          // nothing. token will be send in other time
          if (mSendFirebaseTokenClient != null) {
            mSendFirebaseTokenClient.unregisterListener();
          }
        }
      };

  void initializeTrueTime() {
    new Thread(new Runnable() {
      @Override public void run() {
        try {

          TrueTime.build().withNtpHost("ntp2.stratum2.ru").initialize();
          Log.e("True time", "WORKED!!!");
        } catch (IOException e) {
          Log.e("True time", "didn't work");
          try {
            Thread.currentThread().sleep(500);
          } catch (InterruptedException e1) {
            e1.printStackTrace();
          }
          initializeTrueTime();
          Thread.currentThread().interrupt();
          //e.printStackTrace();
        }
      }
    }).start();
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    // initializeTrueTime();
    SharedPreferences prefs =
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

    String appLang = prefs.getString("pref_app_language", Locale.getDefault().getLanguage());

    Log.d("APP LANGUAGE", appLang);

    Resources res = getApplicationContext().getResources();
    DisplayMetrics dm = res.getDisplayMetrics();
    android.content.res.Configuration conf = res.getConfiguration();
    conf.locale = new Locale(appLang);
    res.updateConfiguration(conf, dm);

    super.onCreate(savedInstanceState);

    AppsFlyerLib.getInstance().startTracking(this.getApplication(), "PtjAzTP7TzPLhFZRJW3ouk");

    connErrorLayout = findViewById(R.id.layout_conn_error);
    mSugarmanProgressFragment = new SugarmanProgressFragment();
    mProgressFragment = new ProgressFragment();
    noNetworkDialog =
        new SugarmanDialog.Builder(this, DialogConstants.NO_INTERNET_CONNECTION_ID).content(
            R.string.no_internet_connection).btnCallback(this).build();

    if (!App.getEventBus().isRegistered(this)) {
      App.getEventBus().register(this);
    }
  }

  @Override protected void onResume() {
    super.onResume();

    SharedPreferenceHelper.isFirstLaunchOfTheDay(SharedPreferenceHelper.getUserId());

    new Thread(new Runnable() {
      @Override public void run() {
        while (true) {
          try {

            Thread.sleep(5000);
            //System.out.println("slept for 5000");
            runOnUiThread(new Runnable() {
              @Override public void run() {
                showNoInternetLabel(DeviceHelper.isNetworkConnected());
              }
            });
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    }).start();

    //showNoInternetLabel(DeviceHelper.isNetworkConnected());

  }

  @Override protected void onDestroy() {
    super.onDestroy();

    try {
      App.getEventBus().unregister(this);
    } catch (IllegalStateException e) {
      Log.e(TAG, "failure unregister event bus", e);
    }
  }

  @Override public void onBackPressed() {
    DeviceHelper.hideKeyboard(this);
    super.onBackPressed();
  }

  @Subscribe public void onEvent(RefreshGCMTokenEvent event) {
    Timber.e("onEvent");
    String token = event.getToken();
    if (!TextUtils.isEmpty(token)
        && !(this instanceof SplashActivity)
        && !(this instanceof LoginActivity)) {
      if (mSendFirebaseTokenClient == null) {
        mSendFirebaseTokenClient = new SendFirebaseTokenClient();
      }

      mSendFirebaseTokenClient.registerListener(sendFirebaseTokenListener);
      mSendFirebaseTokenClient.sendFirebaseToken(token);
    }
  }

  @Subscribe public void onEvent(ChangeConnectionEvent event) {
    showNoInternetLabel(event.isConnected());
    if (event.isConnected()) {
      connectionEnabled();
    }
  }

  @Override public void onApiUnauthorized() {
    //        logout();
    Timber.e(SharedPreferenceHelper.getVkToken());
    Timber.e(SharedPreferenceHelper.getFBAccessToken());
    Timber.e(SharedPreferenceHelper.getPhoneNumber());

    if (
        SharedPreferenceHelper.getFBAccessToken().equals("none")
        && SharedPreferenceHelper.getVkToken().equals("none")
        && SharedPreferenceHelper.getPhoneNumber().equals("none")
        ) {
      Timber.e("no tokens no ph");
      logout();
    } else {
      presenter.refreshToken(SharedPreferenceHelper.getFBAccessToken(),
          SharedPreferenceHelper.getVkToken());
    }
    Timber.e("rrrrrrrrr");
    mRefreshUserDataClient.registerListener(this);
  }

  @Override public void startSplashActivity() {
    Intent intent = new Intent(BaseActivity.this, SplashActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
  }

  @Override public void onUpdateOldVersion() {
    showUpdateOldVersionDialog();
  }

  @Override public void onClickDialog(SugarmanDialog dialog, DialogButton button) {
    String id = dialog.getId();

    switch (id) {
      case DialogConstants.NO_INTERNET_CONNECTION_ID:
        dialog.dismiss();

        setResult(RESULT_CANCELED);
        //finish();
        break;
      default:
        Log.d(TAG, "not processed click dialog with id: " + id);
        break;
    }
  }

  public boolean isReady() {
    boolean isReady = (!isFinishing());

    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
      isReady = isReady && !isDestroyed();
    }
    return isReady;
  }

  public void showUpdateOldVersionDialog() {
    new SugarmanDialog.Builder(this, DialogConstants.UPDATE_OLD_VERSION).content(
        R.string.update_old_version).btnCallback(new SugarmanDialogListener() {
      @Override public void onClickDialog(SugarmanDialog dialog, DialogButton button) {
        final String appPackageName =
            getPackageName(); // getPackageName() from Context or Activity object
        try {
          startActivity(
              new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
          startActivity(new Intent(Intent.ACTION_VIEW,
              Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
        finish();
      }
    }).show();
  }

  public void showNoInternetConnectionDialog() {
    noNetworkDialog =
        new SugarmanDialog.Builder(this, DialogConstants.NO_INTERNET_CONNECTION_ID).content(
            R.string.no_internet_connection).btnCallback(this).build();
    noNetworkDialog.show();
  }

  public void hideNoInternetConnectionDialog() {
    noNetworkDialog.hide();
  }

  public void closeFragment(BaseFragment fragment) {
    isOverlayFragmentShowed = false;
    if (fragment != null) {
      fragment.closeFragment();
    }
  }

  public void showSugarmanProgressFragment() {
    DeviceHelper.hideKeyboard(this);

    try {
      if (mSugarmanProgressFragment != null) {
        if (mSugarmanProgressFragment.isFragmentHidden()
            && !mSugarmanProgressFragment.isAdded()
            && !isOverlayFragmentShowed) {
          FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
          transaction.add(R.id.fragment_container, mSugarmanProgressFragment);

          transaction.commitAllowingStateLoss();
          isOverlayFragmentShowed = true;
        }
      } else {
        Log.e(TAG, "progress sugarman fragment is null");
      }
    } catch (IllegalStateException e) {
      Log.e(TAG, "failure show progress sugarman fragment", e);
    }
  }

  public void closeSugarmanProgressFragment() {
    isOverlayFragmentShowed = false;
    if (mSugarmanProgressFragment != null) {
      mSugarmanProgressFragment.closeFragment();
    }
  }

  public void showProgressFragmentTemp() {
    DeviceHelper.hideKeyboard(this);

    try {
      if (mProgressFragment != null) {
        if (mProgressFragment.isFragmentHidden()
            && !mProgressFragment.isAdded()
            && !isOverlayFragmentShowed) {
          FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
          transaction.add(R.id.fragment_container, mProgressFragment);

          transaction.commitAllowingStateLoss();
          isOverlayFragmentShowed = true;
        }
      } else {
        Log.e(TAG, "progress fragment is null");
      }
    } catch (IllegalStateException e) {
      Log.e(TAG, "failure show progress fragment", e);
    }
  }

  public void showProgressFragment() {
    DeviceHelper.hideKeyboard(this);

    try {
      if (mProgressFragment != null) {
        if (mProgressFragment.isFragmentHidden()
            && !mProgressFragment.isAdded()
            && !isOverlayFragmentShowed) {
          //      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
          //      transaction.add(R.id.fragment_container, mProgressFragment);

          //   transaction.commitAllowingStateLoss();
          //   isOverlayFragmentShowed = true;
        }
      } else {
        Log.e(TAG, "progress fragment is null");
      }
    } catch (IllegalStateException e) {
      Log.e(TAG, "failure show progress fragment", e);
    }
  }

  public void showFragment(BaseFragment fragment) {
    DeviceHelper.hideKeyboard(this);

    try {
      if (fragment != null) {
        if (fragment.isFragmentHidden() && !fragment.isAdded() && !isOverlayFragmentShowed) {
          FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
          transaction.add(R.id.fragment_container, fragment);

          transaction.commitAllowingStateLoss();
          isOverlayFragmentShowed = true;
        }
      } else {
        Log.e(TAG, "fragment is null");
      }
    } catch (IllegalStateException e) {
      Log.e(TAG, "failure show fragment", e);
    }
  }

  public void closeProgressFragment() {
    isOverlayFragmentShowed = false;
    if (mProgressFragment != null) {
      mProgressFragment.closeFragment();
    }
  }

  public void logout() {
    clearLoginData();
    Log.e("Token from base", "Logged out");

    stopStepDetectorService();
    App.appendLog(Constants.TAG_TEST_GO_TO_NEXT_DAY, "logOut");
    Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
  }

  public void clearLoginData() {
    Timber.e("clearLoginData");
    new DeleteFCMTokenAsyncTask().execute();

    SharedPreferenceHelper.saveGCMToken("");
    SharedPreferenceHelper.clearUser();
    SharedPreferenceHelper.clearFBDate();
    SharedPreferenceHelper.saveVkId("none");
    SharedPreferenceHelper.saveVkToken("none");
    SharedPreferenceHelper.savePhoneNumber("none");
    VKSdk.logout();
    LoginManager.getInstance().logOut();
  }

  public void showNoInternetLabel(boolean isConnected) {
    if (isConnected && connErrorLayout != null) {
      connErrorLayout.setVisibility(View.INVISIBLE);
    } else {
      //connErrorLayout.setVisibility(View.VISIBLE);
    }
  }

  private void stopStepDetectorService() {
    Intent startIntent = new Intent(this, MasterStepDetectorService.class);
    startIntent.setAction(Constants.COMMAND_STOP_STEP_DETECTOR_SERVICE);
    startService(startIntent);
  }

  protected void connectionEnabled() {
  }
}
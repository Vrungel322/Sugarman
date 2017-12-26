package com.sugarman.myb.ui.activities.splash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AppsFlyerLib;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.facebook.FacebookException;
import com.sugarman.myb.App;
import com.sugarman.myb.R;
import com.sugarman.myb.api.models.requests.ReportStats;
import com.sugarman.myb.api.models.responses.Tracking;
import com.sugarman.myb.api.models.responses.me.invites.Invite;
import com.sugarman.myb.api.models.responses.me.notifications.Notification;
import com.sugarman.myb.api.models.responses.me.requests.Request;
import com.sugarman.myb.constants.Config;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.constants.DialogConstants;
import com.sugarman.myb.eventbus.events.NeedOpenSpecificActivityEvent;
import com.sugarman.myb.models.iab.SubscriptionEntity;
import com.sugarman.myb.tasks.DeleteFileAsyncTask;
import com.sugarman.myb.ui.activities.GetUserInfoActivity;
import com.sugarman.myb.ui.activities.LoginActivity;
import com.sugarman.myb.ui.activities.approveOtp.ApproveOtpActivity;
import com.sugarman.myb.ui.activities.mainScreeen.MainActivity;
import com.sugarman.myb.ui.dialogs.DialogButton;
import com.sugarman.myb.ui.dialogs.SugarmanDialog;
import com.sugarman.myb.utils.AnalyticsHelper;
import com.sugarman.myb.utils.DeviceHelper;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import com.sugarman.myb.utils.inapp.IabHelper;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class SplashActivity extends GetUserInfoActivity
    implements SoundPool.OnLoadCompleteListener, ISplashActivityView, InstallReferrerStateListener {
  private static final String TAG = SplashActivity.class.getName();
  private final Runnable showNotSupported = () -> new SugarmanDialog.Builder(SplashActivity.this,
      DialogConstants.DEVICE_IS_NOT_SUPPORTED_ID).content(R.string.step_detector_not_supported)
      .btnCallback(SplashActivity.this)
      .show();
  IabHelper mHelper;
  @InjectPresenter SplashActivityPresenter mPresenter;
  private int openedActivityCode = -1;
  private String trackingIdFromFcm = "";
  private CompositeSubscription mCompositeSubscription = new CompositeSubscription();
  private boolean done;
  InstallReferrerClient mReferrerClient;


  @Override protected void onCreate(Bundle savedInstanceState) {
    setContentView(R.layout.activity_splash);
    super.onCreate(savedInstanceState);

    mReferrerClient = InstallReferrerClient.newBuilder(this).build();
    mReferrerClient.startConnection(this);

    try {
      PackageInfo info =
          getPackageManager().getPackageInfo("com.sugarman.myb", PackageManager.GET_SIGNATURES);
      for (Signature signature : info.signatures) {
        MessageDigest md = MessageDigest.getInstance("SHA");
        md.update(signature.toByteArray());
        Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
      }
    } catch (PackageManager.NameNotFoundException e) {
      Timber.e("Govno 1");
    } catch (NoSuchAlgorithmException e) {
      Timber.e("Govno 2");
    }

    AppsFlyerLib.getInstance().trackAppLaunch(getApplicationContext(), "PtjAzTP7TzPLhFZRJW3ouk");

    Map<String, Object> eventValue = new HashMap<>();
    eventValue.put(AFInAppEventParameterName.LEVEL, 9);
    eventValue.put(AFInAppEventParameterName.SCORE, 100);
    AppsFlyerLib.getInstance().trackEvent(getApplicationContext(), "af_launch_splash", eventValue);

    SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    if (accelerometerSensor != null) {
      if (!openSpecificActivity()) {
        SoundPool soundPool = App.getSoundPoolInstance();
        soundPool.setOnLoadCompleteListener(this);
        int[] brokenGlassIds = new int[7];
        brokenGlassIds[0] = soundPool.load(this, R.raw.broken_glass1, 1);
        brokenGlassIds[1] = soundPool.load(this, R.raw.broken_glass2, 1);
        brokenGlassIds[2] = soundPool.load(this, R.raw.broken_glass3, 1);
        brokenGlassIds[3] = soundPool.load(this, R.raw.broken_glass4, 1);
        brokenGlassIds[4] = soundPool.load(this, R.raw.broken_glass5, 1);
        brokenGlassIds[5] = soundPool.load(this, R.raw.broken_glass6, 1);
        brokenGlassIds[6] = soundPool.load(this, R.raw.broken_glass7, 1);
        SharedPreferenceHelper.saveBrokenGlassIds(brokenGlassIds);
      }
    } else {
      App.getHandlerInstance().postDelayed(showNotSupported, Constants.SPLASH_UPDATE_TIMEOUT);
    }
    setupInAppPurchase();
  }

  @Override protected void onResume() {
    super.onResume();
    new DeleteFileAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
        Config.APP_FOLDER); // clear unused file
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    //if (mHelper != null) mHelper.dispose();
    //mHelper = null;

    App.getSoundPoolInstance().setOnLoadCompleteListener(null);
  }

  private void setupInAppPurchase() {
    mHelper = new IabHelper(this, Config.BASE_64_ENCODED_PUBLIC_KEY);

    mHelper.startSetup(result -> {
      if (!result.isSuccess()) {
        Timber.e("In-app Billing setup failed: " + result);
      } else {
        Timber.e("In-app Billing is set up OK");
        consumeItem();
      }
    });
    mHelper.enableDebugLogging(true);
  }

  public void consumeItem() {
    mHelper.queryInventoryAsync(true, (result, inventory) -> {
      Timber.e(result.getMessage());
      if (SharedPreferenceHelper.getListSubscriptionEntity() != null
          && SharedPreferenceHelper.getListSubscriptionEntity().size() != 0) {
        for (SubscriptionEntity subscriptionEntity : SharedPreferenceHelper.getListSubscriptionEntity()) {
          //Timber.e(inventory.getSkuDetails(subscriptionEntity.getSlot()).getTitle());
          //Timber.e(inventory.getSkuDetails(subscriptionEntity.getSlot()).getSku());

          String skuName = "";
          if(inventory.getSkuDetails(subscriptionEntity.getSlot())==null)
            skuName="null";
          else
            skuName = inventory.getSkuDetails(subscriptionEntity.getSlot()).getTitle();
          mPresenter.checkInAppBilling(inventory.getPurchase(subscriptionEntity.getSlot()),
              skuName, null,
              subscriptionEntity.getSlot());
        }
      }
    });
  }

  @Override public void onClickDialog(SugarmanDialog dialog, DialogButton button) {
    String id = dialog.getId();

    switch (id) {
      case DialogConstants.API_REFRESH_FB_TOKEN_FAILURE_ID:
      case DialogConstants.API_SEND_FIREBASE_TOKEN_FAILURE_ID:
      case DialogConstants.API_GET_MY_TRACKINGS_FAILURE_ID:
      case DialogConstants.API_GET_MY_USER_FAILURE_ID:
      case DialogConstants.API_GET_NOTIFICATIONS_FAILURE_ID:
      case DialogConstants.DEVICE_IS_NOT_SUPPORTED_ID:
      case DialogConstants.API_GET_MY_REQUESTS_FAILURE_ID:
      case DialogConstants.API_GET_MY_INVITES_FAILURE_ID:
      case DialogConstants.API_GET_MY_STATS_FAILURE_ID:
        dialog.dismiss();
        //
        finish();
        break;
      case DialogConstants.FB_REFRESH_TOKEN_FAILURE_ID:
        dialog.dismiss();
        finish();
        openLoginActivity();
        break;
      default:
        super.onClickDialog(dialog, button);
        break;
    }
  }

  @Override public void OnTokenRefreshFailed(FacebookException exception) {
    Log.e(TAG, "failure refresh fb token: ", exception);
    if (DeviceHelper.isNetworkConnected()) {
      Timber.e("isNetworkConnected");
      clearLoginData();
      showFailureDialog(getString(R.string.no_internet_connection),
          DialogConstants.FB_REFRESH_TOKEN_FAILURE_ID);
    } else {
      showNoInternetConnectionDialog();
    }
  }

  @Override public void onApiRefreshUserDataFailure(String message) {
    AnalyticsHelper.reportLogin(false);

    if (DeviceHelper.isNetworkConnected()) {
      showFailureDialog(message, DialogConstants.API_REFRESH_FB_TOKEN_FAILURE_ID);
    } else {
      showNoInternetConnectionDialog();
    }
  }

  @Override public void onApiSendFirebaseTokenFailure(String message) {
    if (DeviceHelper.isNetworkConnected()) {
      showFailureDialog(message, DialogConstants.API_SEND_FIREBASE_TOKEN_FAILURE_ID);
    } else {
      showNoInternetConnectionDialog();
    }
  }

  @Override public void onApiGetMyAllUserInfoFailure(String message) {
    if (DeviceHelper.isNetworkConnected()) {
      String userId = SharedPreferenceHelper.getUserId();
      ReportStats[] stats = SharedPreferenceHelper.getReportStats(userId);
      if (!TextUtils.isEmpty(SharedPreferenceHelper.getUserId()) && stats.length != 0) {

        openMainActivity();
      } else {
        showFailureDialog(message, DialogConstants.API_GET_MY_USER_FAILURE_ID);
      }
    } else {
      String userId = SharedPreferenceHelper.getUserId();
      ReportStats[] stats = SharedPreferenceHelper.getReportStats(userId);
      if (!TextUtils.isEmpty(SharedPreferenceHelper.getUserId()) && stats.length != 0) {
        openMainActivity();
      } else {
        showNoInternetConnectionDialog();
      }
    }
  }

  @Override public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
    if (!done) {
      Timber.e("onLoadComplete");
      getUserDataWithDelay(Constants.SPLASH_UPDATE_TIMEOUT);
      done = true;
    }
  }

  @Override public void noAccessToken() {
    //if(!VKSdk.isLoggedIn())
    openLoginActivity();
    //else haveTokensAndUserData();
  }

  @Override public void haveTokensAndUserData() {
    openMainActivity();
  }

  private boolean openSpecificActivity() {
    Intent intent = getIntent();
    openedActivityCode = intent.getIntExtra(Constants.INTENT_OPEN_ACTIVITY, -1);
    trackingIdFromFcm = intent.getStringExtra(Constants.INTENT_FCM_TRACKING_ID);

    boolean isForeground = App.getInstance().isAppForeground();
    Activity current = App.getInstance().getCurrentActivity();

    if (current == null) {
      // application is closed. usually run splash activity
      return false;
    } else {
      if (!isForeground && current instanceof MainActivity) {
        // main activity opened yet
        //                finish(); // close splash
        App.getEventBus()
            .post(new NeedOpenSpecificActivityEvent(openedActivityCode, trackingIdFromFcm));
        return true;
      } else {
        // usually run splash activity
        return false;
      }
    }
  }

  @Override public void onApiGetMyAllUserInfoNeedApproveOTP(String phoneNumber) {
    Intent intent = new Intent(SplashActivity.this, ApproveOtpActivity.class);
    intent.putExtra("showSettings", true);
    intent.putExtra("phone", phoneNumber);
    intent.putExtra("nameParentActivity", SplashActivity.class.getName());
    startActivity(intent);
    finish();
  }

  private void openLoginActivity() {
    Timber.e("openLoginActivity");
    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
  }

  private void openMainActivity() {
    Timber.e("openMainActivity");
    actualTrackings = actualTrackings == null ? new Tracking[0] : actualTrackings;
    actualNotifications = actualNotifications == null ? new Notification[0] : actualNotifications;
    actualInvites = actualInvites == null ? new Invite[0] : actualInvites;
    actualRequests = actualRequests == null ? new Request[0] : actualRequests;

    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    intent.putExtra(Constants.INTENT_MY_TRACKINGS, actualTrackings);
    intent.putExtra(Constants.INTENT_MY_INVITES, actualInvites);
    intent.putExtra(Constants.INTENT_MY_REQUESTS, actualRequests);
    intent.putExtra(Constants.INTENT_OPEN_ACTIVITY, openedActivityCode);
    intent.putExtra(Constants.INTENT_FCM_TRACKING_ID, trackingIdFromFcm);
    intent.putExtra(Constants.INTENT_MY_NOTIFICATIONS, actualNotifications);
    startActivity(intent);
    finish();
  }

  @Override public void onInstallReferrerSetupFinished(int responseCode) {

    switch (responseCode) {
      case InstallReferrerClient.InstallReferrerResponse.OK:
        try {
          Timber.e("InstallReferrer conneceted");
          ReferrerDetails response = mReferrerClient.getInstallReferrer();
          handleReferrer(response);
          mReferrerClient.endConnection();
        } catch (RemoteException e) {
          e.printStackTrace();
        }
        break;
      case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
        Timber.e("InstallReferrer not supported");
        break;
      case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
        Timber.e( "Unable to connect to the service");
        break;
      default:
        Timber.e( "responseCode not found.");
    }

  }

  @Override public void onInstallReferrerServiceDisconnected() {
    Timber.e("Referrer service disconnected");
  }

  private void handleReferrer(ReferrerDetails response)
  {
    String searchQuery = "utm_source";
    String referrerStr = response.getInstallReferrer();

    Timber.e(SharedPreferenceHelper.getCampaign());
    if(referrerStr.contains(searchQuery))
    {
      String temp = referrerStr.split(searchQuery)[1];
      int index = temp.indexOf("&");
      if(index==-1) index = temp.length();
      temp = temp.substring(1,index);
      Timber.e(temp);
      SharedPreferenceHelper.setCampaign(temp);
    }
  }
}
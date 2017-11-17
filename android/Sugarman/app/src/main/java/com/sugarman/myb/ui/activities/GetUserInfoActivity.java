package com.sugarman.myb.ui.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.facebook.AccessToken;
import com.facebook.FacebookException;
import com.sugarman.myb.App;
import com.sugarman.myb.api.clients.GetMyAllUserDataClient;
import com.sugarman.myb.api.clients.RefreshUserDataClient;
import com.sugarman.myb.api.clients.SendFirebaseTokenClient;
import com.sugarman.myb.api.models.responses.AllMyUserDataResponse;
import com.sugarman.myb.api.models.responses.Tracking;
import com.sugarman.myb.api.models.responses.me.invites.Invite;
import com.sugarman.myb.api.models.responses.me.notifications.Notification;
import com.sugarman.myb.api.models.responses.me.requests.Request;
import com.sugarman.myb.api.models.responses.me.stats.Stats;
import com.sugarman.myb.api.models.responses.users.UsersResponse;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.listeners.ApiGetMyAllUserInfoListener;
import com.sugarman.myb.listeners.ApiRefreshUserDataListener;
import com.sugarman.myb.listeners.ApiSendFirebaseTokenListener;
import com.sugarman.myb.tasks.RefreshFCMTokenAsyncTask;
import com.sugarman.myb.ui.activities.base.BaseActivity;
import com.sugarman.myb.ui.dialogs.SugarmanDialog;
import com.sugarman.myb.utils.AnalyticsHelper;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import timber.log.Timber;

public class GetUserInfoActivity extends BaseActivity
    implements AccessToken.AccessTokenRefreshCallback, ApiRefreshUserDataListener,
    ApiSendFirebaseTokenListener, ApiGetMyAllUserInfoListener {

  protected Tracking[] actualTrackings;
  protected Notification[] actualNotifications;
  protected Invite[] actualInvites;
  protected Request[] actualRequests;
  protected AccessToken facebookToken;
  private GetMyAllUserDataClient mGetMyAllUserData;
  private RefreshUserDataClient mRefreshUserDataClient;
  private SendFirebaseTokenClient mSendFirebaseTokenClient;
  private final Runnable moveNext = new Runnable() {
    @Override public void run() {
      moveNext();
    }
  };

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mGetMyAllUserData = new GetMyAllUserDataClient();
    mRefreshUserDataClient = new RefreshUserDataClient();
    mSendFirebaseTokenClient = new SendFirebaseTokenClient();

    mGetMyAllUserData.registerListener(this);
    mRefreshUserDataClient.registerListener(this);
    mSendFirebaseTokenClient.registerListener(this);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    if (mGetMyAllUserData != null) {
      mGetMyAllUserData.unregisterListener();
    }

    if (mRefreshUserDataClient != null) {
      mRefreshUserDataClient.unregisterListener();
    }

    if (mSendFirebaseTokenClient != null) {
      mSendFirebaseTokenClient.unregisterListener();
    }
  }

  @Override public void OnTokenRefreshed(AccessToken accessToken) {
    refreshUserData(accessToken, SharedPreferenceHelper.getVkToken(), "none", "none",
        SharedPreferenceHelper.getEmail(), SharedPreferenceHelper.getUserName(),
        SharedPreferenceHelper.getVkId(), SharedPreferenceHelper.getFacebookId(),
        SharedPreferenceHelper.getAvatar()); // TODO: 31.08.2017 Берем токен ВК из настроек. Если его нет, то none
  }

  @Override public void OnTokenRefreshFailed(FacebookException exception) {
  }

  @Override public void onApiSendFirebaseTokenSuccess() {
    SharedPreferenceHelper.saveGCMToken("");
    mGetMyAllUserData.getMyAllUserData();
  }

  @Override public void onApiSendFirebaseTokenFailure(String message) {
  }

  @Override public void onApiRefreshUserDataSuccess(UsersResponse response) {
    AnalyticsHelper.reportLogin(true);
    Log.e("ApiRefreshUserData", "Called");
    SharedPreferenceHelper.saveUser(response.getUser());
    SharedPreferenceHelper.saveToken(response.getTokens());
    Timber.e("limitGUIA " + response.getUser().getGroupsLimit());
    SharedPreferenceHelper.saveGroupsLimit(response.getUser().getGroupsLimit());
    if (response.getUser().getEmail() != null && !response.getUser().getEmail().equals("")) {
      Timber.e("RETURNED EMAIL " + response.getUser().getEmail());
      SharedPreferenceHelper.saveEmail(response.getUser().getEmail());
    }
    if (response.getUser().getPhoneNumber() != null && !response.getUser()
        .getPhoneNumber()
        .equals("")) {
      Timber.e("RETURNED PHONE " + response.getUser().getPhoneNumber());
      SharedPreferenceHelper.savePhoneNumber(response.getUser().getPhoneNumber());
    } else {
      Timber.e("RETURNED PHONE govno");
    }
    Timber.e("huy" + response.toString());
    Timber.e(response.getTokens().getAccessToken());

    checkFirebaseToken();
  }

  @Override public void onApiRefreshUserDataFailure(String message) {

    Log.e("Token error", message);
  }

  @Override public void onApiGetMyAllUserInfoSuccess(AllMyUserDataResponse allMyInfo) {
    SharedPreferenceHelper.saveUser(allMyInfo.getUser());

    Stats[] stats = allMyInfo.getStats();
    App.appendLog(Constants.TAG_TEST_GO_TO_NEXT_DAY,
        getClass().getSimpleName() + " received stats:");
    for (int i = 0; i < stats.length; i++) {
      App.appendLog(Constants.TAG_TEST_GO_TO_NEXT_DAY,
          "stat  " + i + "   " + allMyInfo.getStats()[i].toString());
    }
    Timber.e("Level: " + allMyInfo.getUser().getLevel());
    App.appendLog(Constants.TAG_TEST_GO_TO_NEXT_DAY,
        getClass().getSimpleName() + " received stats finish:");
    SharedPreferenceHelper.saveStats(allMyInfo.getStats());

    this.actualTrackings = allMyInfo.getTrackings();

    List<Invite> actualInvites = new ArrayList<>(allMyInfo.getInvites().length);
    for (Invite invite : allMyInfo.getInvites()) {
      String status = invite.getTracking().getStatus();
      if (!TextUtils.equals(status, Constants.STATUS_FAILED) && !TextUtils.equals(status,
          Constants.STATUS_COMPLETED)) {
        actualInvites.add(invite);
      }
    }
    this.actualInvites = actualInvites.toArray(new Invite[actualInvites.size()]);

    List<Request> actualRequests = new ArrayList<>(allMyInfo.getRequests().length);
    for (Request request : allMyInfo.getRequests()) {
      String status = request.getTracking().getStatus();
      if (!TextUtils.equals(status, Constants.STATUS_FAILED) && !TextUtils.equals(status,
          Constants.STATUS_COMPLETED)) {
        actualRequests.add(request);
      }
    }
    this.actualRequests = actualRequests.toArray(new Request[actualRequests.size()]);

    this.actualNotifications = allMyInfo.getNotifications();

    checkFirebaseToken();
  }

  @Override public void onApiGetMyAllUserInfoFailure(String message) {
  }

  @Override public void onApiGetMyAllUserInfoNeedApproveOTP(String phone) {

  }

  public void refreshUserData(AccessToken accessToken, String vkToken, String gToken,
      String phoneNumber, String email, String name, String vkId, String fbId, String pictureUrl) {
    String token = "none";
    if (accessToken != (null)) {
      token = accessToken.getToken();
      SharedPreferenceHelper.saveFBExipredTokenDate(accessToken.getExpires());
      SharedPreferenceHelper.saveFBAccessToken(token);
    }
    mRefreshUserDataClient.refreshUserData(token, vkToken, gToken, phoneNumber, email, name, vkId,
        fbId, pictureUrl); //test
  }

  public void refreshUserData(String accessToken, String vkToken, String gToken, String phoneNumber,
      String email, String name, String vkId, String fbId, String pictureUrl) {
    Timber.e("refreshUserData");
    SharedPreferenceHelper.saveFBAccessToken(accessToken);
    mRefreshUserDataClient.refreshUserData(accessToken, vkToken, gToken, phoneNumber, email, name,
        vkId, fbId, pictureUrl); //test
  }

  public void getUserDataWithDelay(long delayMs) {
    clearData();
    facebookToken = AccessToken.getCurrentAccessToken();
    if (facebookToken != null) {
      Observable.just(true).delay(delayMs, TimeUnit.MILLISECONDS).map(aLong -> {
        if (facebookToken.isExpired()) {
          AccessToken.refreshCurrentAccessTokenAsync(GetUserInfoActivity.this);
        } else {

          checkFirebaseToken();
        }
        return "";
      }).subscribe();
      //App.getHandlerInstance().postDelayed(updateDate, delayMs);
    } else {
      if (!SharedPreferenceHelper.getVkToken().equals("none")
          || !SharedPreferenceHelper.getPhoneNumber().equals("none")) {
        checkFirebaseToken();
      } else {
        Timber.e("GetUserInfo clean token");
        clearLoginData();
        App.getHandlerInstance().postDelayed(moveNext, delayMs);
      }
    }
  }

  private void checkFirebaseToken() {
    String gcmToken = SharedPreferenceHelper.getGCMToken();
    if (TextUtils.isEmpty(gcmToken)) {
      new RefreshFCMTokenAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
      if (actualTrackings == null) {
        mGetMyAllUserData.getMyAllUserData();
      } else {

        haveTokensAndUserData();
      }
    } else {
      mSendFirebaseTokenClient.sendFirebaseToken(gcmToken);
    }
  }

  private void moveNext() {
    AccessToken facebookToken = AccessToken.getCurrentAccessToken();
    String accessToken = SharedPreferenceHelper.getAccessToken();
    if (accessToken.equals("none")) {
      noAccessToken();
    } else {

      checkFirebaseToken();
    }
  }

  public void showFailureDialog(String message, String dialogConstant) {
    new SugarmanDialog.Builder(this, dialogConstant).content(message).btnCallback(this).show();
  }

  private void clearData() {
    actualTrackings = null;
    actualNotifications = null;
    actualInvites = null;
    actualRequests = null;
  }

  public void noAccessToken() {
  }

  public void haveTokensAndUserData() {
  }
}
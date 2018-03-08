package com.sugarman.myb.ui.activities;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.sugarman.myb.R;
import com.sugarman.myb.api.models.requests.ReportStats;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.constants.DialogConstants;
import com.sugarman.myb.ui.activities.googleLogin.GoogleLoginHiddenActivity;
import com.sugarman.myb.ui.activities.mainScreeen.MainActivity;
import com.sugarman.myb.ui.dialogs.DialogButton;
import com.sugarman.myb.ui.dialogs.SugarmanDialog;
import com.sugarman.myb.utils.AnalyticsHelper;
import com.sugarman.myb.utils.DeviceHelper;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import com.sugarman.myb.utils.apps_Fly.AppsFlyerEventSender;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import java.util.Arrays;
import jp.wasabeef.blurry.Blurry;
import org.json.JSONException;
import org.json.JSONObject;
import timber.log.Timber;

public class LoginActivity extends GetUserInfoActivity implements View.OnClickListener {

  private static final String TAG = LoginActivity.class.getName();
  private final WebViewClient webViewClient = new WebViewClient() {

    @Override public void onPageFinished(WebView view, String url) {
      closeSugarmanProgressFragment();
    }

    @SuppressWarnings("deprecation") @Override
    public void onReceivedError(WebView view, int errorCode, String description,
        String failingUrl) {
      closeSugarmanProgressFragment();
      new SugarmanDialog.Builder(LoginActivity.this, DialogConstants.OPEN_URL_FAILURE_ID).content(
          R.string.no_internet_connection).btnCallback(LoginActivity.this).show();
    }

    @TargetApi(android.os.Build.VERSION_CODES.M) @Override
    public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
      // Redirect to deprecated method, so you can use it in all SDK versions
      onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(),
          req.getUrl().toString());
    }
  };
  SignInButton gplus;
  RelativeLayout rlfb, rlvk, rlphone, rlGoogle;
  AlphaAnimation animation1;
  Thread t;
  int zeroPointY;
  private CallbackManager callbackManager;
  private WebView wvLogin;
  private ImageView blurry;
  //private TextView vkButton;
  private VKRequest.VKRequestListener vkListener;
  private float mCoeficient = 250f;

  private void saveIMEI() {
    String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

    Timber.e("Secure ID " + androidId);
    SharedPreferenceHelper.setIMEI(androidId);
  }

  @SuppressLint("SetJavaScriptEnabled") @Override
  protected void onCreate(Bundle savedInstanceState) {
    setContentView(R.layout.activity_login);
    super.onCreate(savedInstanceState);
    saveIMEI();
    //vkButton = (TextView) findViewById(R.id.vkButton);
    blurry = (ImageView) findViewById(R.id.blurry_background);
    FrameLayout rootView = (FrameLayout) findViewById(R.id.rootView);
    Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_splash);

    vkListener = new VKRequest.VKRequestListener() {
      @Override public void onComplete(VKResponse response) {
        super.onComplete(response);
        Log.e("VK", "response" + response.responseString);
        JSONObject responseJson = response.json;
        try {
          String name =
              responseJson.getJSONArray("response").getJSONObject(0).getString("first_name")
                  + " "
                  + responseJson.getJSONArray("response").getJSONObject(0).getString("last_name");
          String photoUrl =
              responseJson.getJSONArray("response").getJSONObject(0).getString("photo_100");
          SharedPreferenceHelper.saveAvatar(photoUrl);
          SharedPreferenceHelper.saveUserName(name);
          refreshUserData("none", SharedPreferenceHelper.getVkId(), null, "none",
              SharedPreferenceHelper.getEmail(), SharedPreferenceHelper.getUserName(),
              SharedPreferenceHelper.getVkId(), SharedPreferenceHelper.getFacebookId(),
              photoUrl); //accessToken vkToken gToken phoneNumber email name vkId)
          Log.e("VK", "SUKA" + name);
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    };

    Blurry.with(this)
        .radius(2)
        .sampling(15)
        .color(Color.argb(150, 255, 255, 255))
        .async()
        .from(bmp)
        .into(blurry);

    animation1 = new AlphaAnimation(0.0f, 1.0f);
    animation1.setDuration(1500);

    rlfb = (RelativeLayout) findViewById(R.id.rv_fb);
    rlvk = (RelativeLayout) findViewById(R.id.rv_vk);
    rlphone = (RelativeLayout) findViewById(R.id.rv_phone);
    rlGoogle = (RelativeLayout) findViewById(R.id.rv_google);

    Display display = getWindowManager().getDefaultDisplay();
    Point size = new Point();
    display.getSize(size);
    int width = size.x;
    int height = size.y;
    setUpPositionDependingScreenSize(height);

    rlfb.setY(5000);
    rlvk.setY(5000);
    rlphone.setY(5000);
    rlGoogle.setY(5000);

    rlGoogle.setOnClickListener(view -> {
      Intent intent = new Intent(this, GoogleLoginHiddenActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      //https://console.developers.google.com/apis/credentials?project=api-7925429546426385753-346830
      // need to paste WEB- Client key from that link
      intent.putExtra(GoogleLoginHiddenActivity.EXTRA_CLIENT_ID,
          "214670354742-ukahj46me93mssuaui3eeovkqf1987o6.apps.googleusercontent.com");
      startActivity(intent);
    });

    rlphone.setOnClickListener(v -> {

      AppsFlyerEventSender.sendEvent("af_login_with_phone");

      Intent intent = new Intent(LoginActivity.this, PhoneLoginActivity.class);
      startActivity(intent);
    });

    //Schedule a task to run every 5 seconds (or however long you want)
    t = new Thread(new Runnable() {
      @Override public void run() {
        try {
          Thread.currentThread().sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        runOnUiThread(new Runnable() {
          @Override public void run() {
            blurry.setAlpha(1.0f);
            blurry.startAnimation(animation1);
            rlfb.animate().y(mCoeficient * 0 + zeroPointY).setDuration(1200);
          }
        });
        try {
          Thread.currentThread().sleep(150);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        runOnUiThread(new Runnable() {
          @Override public void run() {
            rlvk.animate().y(mCoeficient + zeroPointY).setDuration(1100);
          }
        });

        try {
          Thread.currentThread().sleep(150);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        runOnUiThread(new Runnable() {
          @Override public void run() {
            rlGoogle.animate().y(mCoeficient * 2 + zeroPointY).setDuration(1000);
          }
        });
        try {
          Thread.currentThread().sleep(150);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        runOnUiThread(new Runnable() {
          @Override public void run() {
            rlphone.animate().y(mCoeficient * 3 + zeroPointY).setDuration(900);
          }
        });
      }
    });
    t.start();

    rlvk.setOnClickListener(v -> {
      //showSugarmanProgressFragment();

      AppsFlyerEventSender.sendEvent("af_login_with_vk");

      VKSdk.login(LoginActivity.this, "friends,email,messages");
    });

    Log.e("VK", "" + VKSdk.isLoggedIn());

    if (VKSdk.isLoggedIn()) {
      //refreshUserData(null, SharedPreferenceHelper.getVkId(), "none","none","none","none","none"); // TODO: 31.08.2017 Берем токен ВК из настроек. Если его нет, то none
      //haveTokensAndUserData();
    }

    callbackManager = CallbackManager.Factory.create();
    LoginManager.getInstance()
        .registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
          @Override public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken = loginResult.getAccessToken();
            if (accessToken != null && !TextUtils.isEmpty(accessToken.getToken())) {
              showSugarmanProgressFragment();
              SharedPreferenceHelper.saveFbId(loginResult.getAccessToken().getUserId());
              new GraphRequest(AccessToken.getCurrentAccessToken(),
                  "/" + loginResult.getAccessToken().getUserId(), null, HttpMethod.GET,
                  new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                      Timber.e(response.getJSONObject().toString());
                      try {
                        SharedPreferenceHelper.saveUserName(
                            response.getJSONObject().getString("name"));
                      } catch (JSONException e) {
                        e.printStackTrace();
                      }
                      refreshUserData(accessToken, "none", "none", "none",
                          SharedPreferenceHelper.getEmail(), SharedPreferenceHelper.getUserName(),
                          SharedPreferenceHelper.getVkId(), SharedPreferenceHelper.getFbId(),
                          SharedPreferenceHelper.getAvatar());// TODO: 31.08.2017 Берем токен ВК из настроек. Если его нет, то none
                    }
                  }).executeAsync();
            } else {
              Log.e(TAG, "token from facebook is null");
              onError(null);
            }
          }

          @Override public void onCancel() {
            Timber.e("onCancel FB");
            clearLoginData();
          }

          @Override public void onError(FacebookException error) {
            String errorText = error != null ? error.getMessage() : "";
            Log.e(TAG, "failure sendFirebaseToken with fb: " + errorText);

            if (DeviceHelper.isNetworkConnected()) {
              Timber.e("onCancel FB isNetworkConnected");

              clearLoginData();

              new SugarmanDialog.Builder(LoginActivity.this,
                  DialogConstants.FB_LOGIN_FAILURE_ID).content(R.string.no_internet_connection)
                  .show();
            } else {
              showNoInternetConnectionDialog();
            }
          }
        });
    rlfb.setOnClickListener(view -> {

      AppsFlyerEventSender.sendEvent("af_login_with_fb");

      LoginManager.getInstance()
          .logInWithReadPermissions(this,
              Arrays.asList("public_profile", "user_friends", "email", "read_custom_friendlists"));
    });

    // View vTerms = findViewById(R.id.tv_terms_of_use);
    // View vPolicy = findViewById(R.id.tv_privacy_policy);
    //LoginButton btnFBLogin = (LoginButton) findViewById(R.id.btn_fb_login);
    wvLogin = (WebView) findViewById(R.id.wv_login);

    WebSettings settings = wvLogin.getSettings();
    settings.setUseWideViewPort(true);
    settings.setJavaScriptEnabled(true);
    wvLogin.setWebViewClient(webViewClient);

    //btnFBLogin.setReadPermissions(Config.FACEBOOK_PERMISSIONS);
    //btnFBLogin.registerCallback(callbackManager, this);

    //        vTerms.setOnClickListener(this);
    //       vPolicy.setOnClickListener(this);
  }

  private void setUpPositionDependingScreenSize(int height) {
    zeroPointY = height / 4;
    mCoeficient = 250f;
    if (height > 1800) {
      zeroPointY = height / 4;
      mCoeficient = 250f;
    }
    if (height < 1400) {
      zeroPointY = height / 5;
      mCoeficient = 200f;
    }
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    callbackManager.onActivityResult(requestCode, resultCode, data);

    if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
      @Override public void onResult(VKAccessToken res) {
        // Пользователь успешно авторизовался
        Log.e("VK", "HUY HUY HUY" + res.accessToken + " " + res.email);
        SharedPreferenceHelper.saveVkToken(res.accessToken);
        SharedPreferenceHelper.saveVkId(res.userId);
        SharedPreferenceHelper.saveEmail(res.email);

        VKRequest request =
            new VKRequest("users.get", VKParameters.from(VKApiConst.FIELDS, "photo_100", "email"));
        request.executeWithListener(vkListener);
      }

      @Override public void onError(VKError error) {
        // Произошла ошибка авторизации (например, пользователь запретил авторизацию)
      }
    })) {
      if (requestCode == 1337) {
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        if (result.isSuccess()) {
          // Signed in successfully, show authenticated UI.
          GoogleSignInAccount acct = result.getSignInAccount();

          Log.e("GPLUS", "УБЛЮДОК " + acct.getId());
        } else {
          Log.e("GPLUS", "ZALOOPA " + result.getStatus() + " ");
        }
      }
    }
  }

  @Override public void onBackPressed() {
    if (wvLogin.getVisibility() == View.VISIBLE) {
      hideWebView();
    } else {
      super.onBackPressed();
    }
  }

  @Override public void onClick(View v) {
    int id = v.getId();
  }

  @Override public void onClickDialog(SugarmanDialog dialog, DialogButton button) {
    String id = dialog.getId();

    switch (id) {
      case DialogConstants.API_REFRESH_FB_TOKEN_FAILURE_ID:
      case DialogConstants.API_GET_MY_TRACKINGS_FAILURE_ID:
      case DialogConstants.API_SEND_FCM_TOKEN_FAILURE_ID:
      case DialogConstants.API_GET_NOTIFICATIONS_FAILURE_ID:
      case DialogConstants.API_GET_MY_INVITES_FAILURE_ID:
      case DialogConstants.API_GET_MY_REQUESTS_FAILURE_ID:
      case DialogConstants.API_GET_MY_USER_FAILURE_ID:
      case DialogConstants.API_GET_MY_STATS_FAILURE_ID:
        dialog.dismiss();
        finish();
        break;
      case DialogConstants.OPEN_URL_FAILURE_ID:
        dialog.dismiss();
        hideWebView();
        break;
      default:
        super.onClickDialog(dialog, button);
        break;
    }
  }

  @Override public void onApiRefreshUserDataFailure(String message) {
    AnalyticsHelper.reportLogin(false);

    closeSugarmanProgressFragment();

    SharedPreferenceHelper.clearFBDate();
    LoginManager.getInstance().logOut();

    if (DeviceHelper.isNetworkConnected()) {
      showFailureDialog(message, DialogConstants.API_REFRESH_FB_TOKEN_FAILURE_ID);
    } else {
      showNoInternetConnectionDialog();
    }
  }

  @Override public void onApiGetMyAllUserInfoFailure(String message) {
    closeSugarmanProgressFragment();
    if (DeviceHelper.isNetworkConnected()) {
      Timber.e("ApiRefreshMyUserInfoFailure");
      haveTokensAndUserData();

      showFailureDialog(message, DialogConstants.API_GET_MY_USER_FAILURE_ID);
    } else {
      showNoInternetConnectionDialog();
    }
  }

  @Override public void onApiSendFirebaseTokenFailure(String message) {
    closeSugarmanProgressFragment();
    if (DeviceHelper.isNetworkConnected()) {
      showFailureDialog(message, DialogConstants.API_SEND_FCM_TOKEN_FAILURE_ID);
    } else {
      showNoInternetConnectionDialog();
    }
  }

  @Override public void haveTokensAndUserData() {
    closeSugarmanProgressFragment();
    showNextActivity();
  }

  @Override public void noAccessToken() {
    closeSugarmanProgressFragment();
  }

  private void openInWebView(String url) {
    showSugarmanProgressFragment();
    wvLogin.setVisibility(View.VISIBLE);
    wvLogin.loadUrl(url);
  }

  private void hideWebView() {
    wvLogin.setVisibility(View.GONE);
  }

  private void showNextActivity() {
    Timber.e("showNextActivity");
    createLocalStats();
    if (SharedPreferenceHelper.introIsShown()) {
      openMainActivity();
    } else {
      openIntroActivity();
    }
  }

  private void openIntroActivity() {
    Intent intent = new Intent(LoginActivity.this, IntroActivity.class);
    intent.putExtra(IntroActivity.CODE_IS_OPEN_LOGIN_ACTIVITY, true);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    intent.putExtra(Constants.INTENT_MY_TRACKINGS, actualTrackings);
    intent.putExtra(Constants.INTENT_MY_INVITES, actualInvites);
    intent.putExtra(Constants.INTENT_MY_REQUESTS, actualRequests);
    intent.putExtra(Constants.INTENT_MY_NOTIFICATIONS, actualNotifications);
    startActivity(intent);
  }

  private void openMainActivity() {
    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    intent.putExtra(Constants.INTENT_MY_TRACKINGS, actualTrackings);
    intent.putExtra(Constants.INTENT_MY_INVITES, actualInvites);
    intent.putExtra(Constants.INTENT_MY_REQUESTS, actualRequests);
    intent.putExtra(Constants.INTENT_MY_NOTIFICATIONS, actualNotifications);
    startActivity(intent);
  }

  private void createLocalStats() {
    String userId = SharedPreferenceHelper.getUserId();
    ReportStats[] stats = SharedPreferenceHelper.getReportStatsLocal(userId);
    Log.e("stats length", "" + stats + " l: " + stats.length);
    if (stats == null || stats.length == 0) {
      SharedPreferenceHelper.saveReportStatsLocal(SharedPreferenceHelper.getReportStats(userId));
      stats = SharedPreferenceHelper.getReportStatsLocal(userId);
    }
  }
}

//Intent intent = new Intent(this, GoogleLoginHiddenActivity.class);
//      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//          intent.putExtra(GoogleLoginHiddenActivity.EXTRA_CLIENT_ID, "665166717862-sv96md550gqprv1nmak21rmd3rcfl5r7.apps.googleusercontent.com"/* Web Client*/);
//          startActivity(intent);

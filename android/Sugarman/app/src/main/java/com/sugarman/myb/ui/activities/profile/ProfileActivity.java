package com.sugarman.myb.ui.activities.profile;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AppsFlyerLib;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.squareup.picasso.CustomPicasso;
import com.sugarman.myb.App;
import com.sugarman.myb.R;
import com.sugarman.myb.api.models.responses.me.invites.Invite;
import com.sugarman.myb.api.models.responses.me.requests.Request;
import com.sugarman.myb.constants.Config;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.constants.DialogConstants;
import com.sugarman.myb.eventbus.events.InviteRemovedEvent;
import com.sugarman.myb.eventbus.events.InvitesUpdatedEvent;
import com.sugarman.myb.eventbus.events.RequestsRemovedEvent;
import com.sugarman.myb.eventbus.events.RequestsUpdatedEvent;
import com.sugarman.myb.ui.activities.HighScoreActivity;
import com.sugarman.myb.ui.activities.IntroActivity;
import com.sugarman.myb.ui.activities.InvitesActivity;
import com.sugarman.myb.ui.activities.MyStatsActivity;
import com.sugarman.myb.ui.activities.RequestsActivity;
import com.sugarman.myb.ui.activities.SettingsActivity;
import com.sugarman.myb.ui.activities.base.BaseActivity;
import com.sugarman.myb.ui.activities.editProfile.EditProfileActivity;
import com.sugarman.myb.ui.dialogs.DialogButton;
import com.sugarman.myb.ui.dialogs.SugarmanDialog;
import com.sugarman.myb.ui.views.MaskTransformation;
import com.sugarman.myb.utils.DeviceHelper;
import com.sugarman.myb.utils.IntentExtractorHelper;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import com.sugarman.myb.utils.apps_Fly.AppsFlyerEventSender;
import com.vk.sdk.VKSdk;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.greenrobot.eventbus.Subscribe;

public class ProfileActivity extends BaseActivity
    implements View.OnTouchListener, IProfileActivityView {

  private static final String TAG = ProfileActivity.class.getName();

  private WebView wvProfile;
  private TextView tvInvitesCounter;
  private TextView tvRequestsCounter;
  private TextView tvInviteFriendsCounter;
  @InjectPresenter ProfileActivityPresenter mPresenter;
  @BindView(R.id.wave1) ImageView wave1;
  @BindView(R.id.wave2) ImageView wave2;
  @BindView(R.id.wave3) ImageView wave3;
  @BindView(R.id.tv_level) TextView level;
  @BindView(R.id.tvTestServer) TextView tvTestEnvironment;
  @BindView(R.id.tvWalked10k) TextView tvWalked10k;
  ImageView ivNoInvites, ivNoRequests;
  ImageView ivAvatar;
  int days;
  private ImageView loadingStrip;

  boolean invitesEnabled;

  private final List<Invite> invites = new ArrayList<>();
  private final List<Request> requests = new ArrayList<>();

  private boolean isNeedRefreshTrackings;
  private String lastAcceptInviteTrackingId;

  private final WebViewClient webViewClient = new WebViewClient() {

    @Override public void onPageFinished(WebView view, String url) {
      closeSugarmanProgressFragment();
    }

    @SuppressWarnings("deprecation") @Override
    public void onReceivedError(WebView view, int errorCode, String description,
        String failingUrl) {
      closeSugarmanProgressFragment();
      new SugarmanDialog.Builder(ProfileActivity.this, DialogConstants.OPEN_URL_FAILURE_ID).content(
          R.string.no_internet_connection).btnCallback(ProfileActivity.this).show();
    }

    @TargetApi(android.os.Build.VERSION_CODES.M) @Override
    public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
      // Redirect to deprecated method, so you can use it in all SDK versions
      onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(),
          req.getUrl().toString());
    }
  };

  @SuppressLint("SetJavaScriptEnabled") @Override
  protected void onCreate(Bundle savedStateInstance) {
    setContentView(R.layout.activity_profile);
    super.onCreate(savedStateInstance);

    if (SharedPreferenceHelper.getBaseUrl().contains("test")) {
      tvTestEnvironment.setVisibility(View.VISIBLE);
    } else {
      tvTestEnvironment.setVisibility(View.GONE);
    }

    invitesEnabled = true;

    Intent intent = getIntent();
    invites.clear();
    invites.addAll(Arrays.asList(IntentExtractorHelper.getInvites(intent)));
    requests.clear();
    requests.addAll(Arrays.asList(IntentExtractorHelper.getRequests(intent)));

    wvProfile = (WebView) findViewById(R.id.wv_profile);
    tvInvitesCounter = (TextView) findViewById(R.id.tv_invite_counter);
    tvInviteFriendsCounter = (TextView) findViewById(R.id.tv_invite_friends_counter);
    tvRequestsCounter = (TextView) findViewById(R.id.tv_requests_counter);
    loadingStrip = (ImageView) findViewById(R.id.loading_strip);
    ivAvatar = (ImageView) findViewById(R.id.iv_avatar);
    ivNoInvites = (ImageView) findViewById(R.id.no_invites_image);
    ivNoRequests = (ImageView) findViewById(R.id.no_requests_image);
    TextView tvTotal = (TextView) findViewById(R.id.tv_total_steps);
    TextView tvVersion = (TextView) findViewById(R.id.tv_version);
    View vInvites = findViewById(R.id.ll_invites_container);
    View vRequests = findViewById(R.id.ll_requests_container);
    View vHighScore = findViewById(R.id.ll_high_score_container);
    View vInviteFriends = findViewById(R.id.tv_invite_friends);
    View vLogout = findViewById(R.id.ll_logout_container);
    View vCross = findViewById(R.id.iv_cross);
    View vSettings = findViewById(R.id.ll_settings_container);
    View vProfileSettings = findViewById(R.id.ll_edit_profile_container);

    if (!invitesEnabled) vInviteFriends.setVisibility(View.GONE);

    days = SharedPreferenceHelper.getCompletedDaysCount();

    if (days > 21) days = days - 21;

    ViewTreeObserver vto = loadingStrip.getViewTreeObserver();
    vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override public void onGlobalLayout() {
        loadingStrip.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        int width = loadingStrip.getMeasuredWidth();
        int height = loadingStrip.getMeasuredHeight();
        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap bmp = Bitmap.createBitmap(width, height, conf); // this creates a MUTABLE bitmap
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        paint.setStrokeWidth(2);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0xffD9DADB);
        paint.setAntiAlias(true);
        canvas.drawRoundRect(new RectF(38, height / 4 - 1, width - 38, height - height / 4), 25, 25,
            paint);
        Paint p = new Paint();
        p.setStrokeWidth(height / 2);
        p.setStrokeCap(Paint.Cap.ROUND);
        p.setColor(0xfff8C1C1);
        //canvas.drawLine(50, height / 2, width - 50, height / 2, p);
        p.setColor(0xffFA2928);
        int drawto = ((width - 100) / 21) * days;
        if (days > 0) canvas.drawLine(50, height / 2, drawto, height / 2, p);

        loadingStrip.setImageBitmap(bmp);
      }
    });

    tvWalked10k.setText(String.format(getString(R.string.walked10k),days));

    //level.setText(String.format(getString(R.string.level), SharedPreferenceHelper.getLevel()));
    level.setText(SharedPreferenceHelper.getUserName());

    Typeface tfDin = Typeface.createFromAsset(getAssets(), "din_light.ttf");
    tvTotal.setTypeface(tfDin);

    String version = DeviceHelper.getAppVersionName();
    if (!TextUtils.isEmpty(version)) {
      version = "V." + version;
      tvVersion.setText(version);
    }

    updateInvitesCounter(invites.size());
    updateRequestsCounter(requests.size());

    WebSettings settings = wvProfile.getSettings();
    settings.setUseWideViewPort(true);
    settings.setJavaScriptEnabled(true);
    wvProfile.setWebViewClient(webViewClient);

    ivAvatar.setOnTouchListener(this);
    vInvites.setOnTouchListener(this);
    vRequests.setOnTouchListener(this);
    vHighScore.setOnTouchListener(this);
    vLogout.setOnTouchListener(this);
    vCross.setOnTouchListener(this);
    vSettings.setOnTouchListener(this);
    vProfileSettings.setOnTouchListener(this);
    //vInviteFriends.                             setOnTouchListener(this);
    findViewById(R.id.ll_invite_friends_container).setOnTouchListener(this);
    findViewById(R.id.ll_tutorial_container).setOnTouchListener(this);
    findViewById(R.id.ll_my_stats_container).setOnTouchListener(this);

    int openActivityCode = IntentExtractorHelper.getOpenActivityCode(intent);
    switch (openActivityCode) {
      case Constants.OPEN_INVITES_ACTIVITY:
        openInvitesActivity();
        break;
      case Constants.OPEN_REQUESTS_ACTIVITY:
        openRequestsActivity();
        break;
    }
  }

  @Override protected void onPause() {
    super.onPause();
    wave1.clearAnimation();
    wave2.clearAnimation();
    wave3.clearAnimation();
  }

  @Override protected void onResume() {
    super.onResume();

    String urlAvatar = SharedPreferenceHelper.getAvatar();
    if (TextUtils.isEmpty(urlAvatar)) {
      ivAvatar.setImageResource(R.drawable.ic_red_avatar);
    } else {
      CustomPicasso.with(this)
          .load(urlAvatar)
          .fit()
          .centerCrop()
          .placeholder(R.drawable.ic_red_avatar)
          .transform(new MaskTransformation(this, R.drawable.profile_mask, false, 0xffff0000))
          .error(R.drawable.ic_red_avatar)
          .into(ivAvatar);
    }

    Animation animation =
        AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_scale_up);
    Animation animation2 =
        AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_scale_up);
    Animation animation3 =
        AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_scale_up);

    new Thread(new Runnable() {
      @Override public void run() {
        runOnUiThread(new Runnable() {
          @Override public void run() {
            wave1.startAnimation(animation);
          }
        });

        try {
          Thread.currentThread().sleep(700);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        runOnUiThread(new Runnable() {
          @Override public void run() {
            wave2.startAnimation(animation2);
          }
        });
        try {
          Thread.currentThread().sleep(700);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        runOnUiThread(new Runnable() {
          @Override public void run() {
            wave3.startAnimation(animation3);
          }
        });
      }
    }).start();
  }

  @Override public void onBackPressed() {
    if (wvProfile.getVisibility() == View.VISIBLE) {
      hideWebView();
    } else {
      closeActivity();
    }
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    switch (requestCode) {
      case Constants.OPEN_INVITES_ACTIVITY_REQUEST_CODE:
        if (resultCode == RESULT_OK) {
          if (data != null
              && data.hasExtra(Constants.INTENT_IS_NEED_REFRESH_TRACKINGS)
              && data.hasExtra(Constants.INTENT_LAST_ACCEPT_INVITE_TRACKING_ID)) {
            isNeedRefreshTrackings =
                data.getBooleanExtra(Constants.INTENT_IS_NEED_REFRESH_TRACKINGS, false);
            lastAcceptInviteTrackingId =
                data.getStringExtra(Constants.INTENT_LAST_ACCEPT_INVITE_TRACKING_ID);
          }
        }
        break;
      case Constants.OPEN_REQUESTS_ACTIVITY_REQUEST_CODE:
        if (resultCode == RESULT_OK) {
          if (data != null && data.hasExtra(Constants.INTENT_IS_NEED_REFRESH_TRACKINGS)) {
            isNeedRefreshTrackings =
                data.getBooleanExtra(Constants.INTENT_IS_NEED_REFRESH_TRACKINGS, false);
          } else {
            Log.e(TAG, "flag is need refresh actualTrackings is absent (actualRequests)");
          }
        }
        break;
      default:
        break;
    }
  }

  @Override public boolean onTouch(View v, MotionEvent event) {
    int id = v.getId();
    switch (event.getAction()) {
      case MotionEvent.ACTION_UP:
        v.setBackgroundColor(0xffffffff);
        switch (id) {


          case R.id.ll_settings_container:
            openSettingsActivity();
            break;

          case R.id.ll_invites_container:
            openInvitesActivity();
            break;
          case R.id.ll_requests_container:
            openRequestsActivity();
            break;
          case R.id.ll_high_score_container:
            openHighScoreActivity();
            break;
          case R.id.ll_invite_friends_container:

            AppsFlyerEventSender.sendEvent("af_invite_friends");

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                ProfileActivity.this.getResources().getString(R.string.invite_message));
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
            break;
          case R.id.ll_tutorial_container:
            openIntroActivity();
            break;
          case R.id.ll_my_stats_container:
            openMyStatsActivity();
            break;
          case R.id.ll_logout_container:

            AppsFlyerEventSender.sendEvent("af_log_out");

            logout();
            VKSdk.logout();
            break;
          case R.id.iv_cross:
          case R.id.iv_avatar:
            closeActivity();
            break;
          case R.id.ll_edit_profile_container:
            AppsFlyerEventSender.sendEvent("af_go_to_profile_settings");

            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
            break;
          default:
            Log.d(TAG,
                "Click on not processed view with id " + getResources().getResourceEntryName(id));
            break;
        }

        break;
      case MotionEvent.ACTION_DOWN:
        if (id != R.id.iv_avatar) v.setBackgroundColor(getResources().getColor(R.color.light_red));
        break;
      case MotionEvent.ACTION_CANCEL:
        if (id != R.id.iv_avatar) v.setBackgroundColor(0xffffffff);
        break;
      default:
        break;
    }
    return true;
  }

  @Override public void onClickDialog(SugarmanDialog dialog, DialogButton button) {
    String id = dialog.getId();
    switch (id) {
      case DialogConstants.OPEN_URL_FAILURE_ID:
        dialog.dismiss();
        hideWebView();
        break;
      default:
        super.onClickDialog(dialog, button);
        break;
    }
  }

  @Subscribe public void onEvent(RequestsRemovedEvent event) {
    String id = event.getId();
    for (Request request : requests) {
      if (TextUtils.equals(request.getId(), id)) {
        requests.remove(request);
        break;
      }
    }
    updateRequestsCounter(requests.size());
  }

  @Subscribe public void onEvent(InviteRemovedEvent event) {
    String id = event.getId();
    for (Invite invite : invites) {
      if (TextUtils.equals(invite.getId(), id)) {
        invites.remove(invite);
        break;
      }
    }

    updateInvitesCounter(invites.size());
  }

  @Subscribe public void onEvent(InvitesUpdatedEvent event) {
    invites.clear();
    invites.addAll(event.getInvites());
    updateInvitesCounter(invites.size());
  }

  @Subscribe public void onEvent(RequestsUpdatedEvent event) {
    requests.clear();
    requests.addAll(event.getRequests());
    updateRequestsCounter(requests.size());
  }

  private void openInWebView(String url) {
    showSugarmanProgressFragment();
    wvProfile.setVisibility(View.VISIBLE);
    wvProfile.loadUrl(url);
  }

  private void hideWebView() {
    wvProfile.setVisibility(View.GONE);
  }

  private void updateInvitesCounter(int count) {
    tvInvitesCounter.setText(String.valueOf(count));
    int paddingHorizontal = getResources().getDimensionPixelOffset(
        count < 10 ? R.dimen.profile_counter_padding_horizontal_large
            : R.dimen.profile_counter_padding_horizontal_small);
    int paddingVertical =
        getResources().getDimensionPixelOffset(R.dimen.profile_counter_padding_vertical);
    tvInvitesCounter.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal,
        paddingVertical);

    tvInvitesCounter.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
    ivNoInvites.setVisibility(count > 0 ? View.GONE : View.VISIBLE);

    int counterOffset =
        getResources().getDimensionPixelOffset(R.dimen.profile_counter_padding_horizontal_large);
    tvInviteFriendsCounter.setPadding(counterOffset, paddingVertical, counterOffset,
        paddingVertical);
  }

  private void updateRequestsCounter(int count) {
    tvRequestsCounter.setText(String.valueOf(count));
    int paddingHorizontal = getResources().getDimensionPixelOffset(
        count < 10 ? R.dimen.profile_counter_padding_horizontal_large
            : R.dimen.profile_counter_padding_horizontal_small);
    int paddingVertical =
        getResources().getDimensionPixelOffset(R.dimen.profile_counter_padding_vertical);
    tvRequestsCounter.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal,
        paddingVertical);
    tvRequestsCounter.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
    ivNoRequests.setVisibility(count > 0 ? View.GONE : View.VISIBLE);
  }

  private void openHighScoreActivity() {
    AppsFlyerEventSender.sendEvent("af_open_high_score");
    Intent intent = new Intent(ProfileActivity.this, HighScoreActivity.class);
    startActivity(intent);
  }

  private void openInvitesActivity() {
    AppsFlyerEventSender.sendEvent("af_open_invites_screen");
    Intent intent = new Intent(ProfileActivity.this, InvitesActivity.class);
    intent.putExtra(Constants.INTENT_MY_INVITES, invites.toArray(new Invite[invites.size()]));
    startActivityForResult(intent, Constants.OPEN_INVITES_ACTIVITY_REQUEST_CODE);
  }

  private void openIntroActivity() {

    AppsFlyerEventSender.sendEvent("af_open_tutorial_from_menu");

    Intent intent = new Intent(ProfileActivity.this, IntroActivity.class);
    intent.putExtra(IntroActivity.CODE_IS_OPEN_LOGIN_ACTIVITY, false);
    startActivity(intent);
  }

  private void openSettingsActivity() {

    AppsFlyerEventSender.sendEvent("af_open_settings");

    Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
    startActivity(intent);
  }

  private void openRequestsActivity() {

    AppsFlyerEventSender.sendEvent("af_open_requests_screen");

    Intent intent = new Intent(ProfileActivity.this, RequestsActivity.class);
    intent.putExtra(Constants.INTENT_MY_REQUESTS, requests.toArray(new Request[requests.size()]));
    startActivityForResult(intent, Constants.OPEN_REQUESTS_ACTIVITY_REQUEST_CODE);
  }

  private void closeActivity() {
    Intent data = new Intent();
    data.putExtra(Constants.INTENT_IS_NEED_REFRESH_TRACKINGS, isNeedRefreshTrackings);
    data.putExtra(Constants.INTENT_LAST_ACCEPT_INVITE_TRACKING_ID, lastAcceptInviteTrackingId);
    setResult(RESULT_OK, data);
    finish();
  }

  private void openMyStatsActivity() {

    AppsFlyerEventSender.sendEvent("af_open_my_stats");

    Intent intent = new Intent(ProfileActivity.this, MyStatsActivity.class);
    startActivity(intent);
  }
}

package com.sugarman.myb.ui.activities;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import com.facebook.CallbackManager;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;
import com.sugarman.myb.App;
import com.sugarman.myb.R;
import com.sugarman.myb.api.clients.GetTrackingInfoClient;
import com.sugarman.myb.constants.Config;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.constants.DialogConstants;
import com.sugarman.myb.listeners.ApiGetTrackingInfoListener;
import com.sugarman.myb.ui.activities.base.BaseActivity;
import com.sugarman.myb.ui.dialogs.DialogButton;
import com.sugarman.myb.ui.dialogs.SugarmanDialog;
import com.sugarman.myb.utils.DeviceHelper;
import com.sugarman.myb.utils.IntentExtractorHelper;

public abstract class NotificationFullScreenActivity extends BaseActivity
    implements View.OnClickListener, ApiGetTrackingInfoListener {

  private static final String TAG = NotificationFullScreenActivity.class.getName();

  ShareButton shareButton;

  View vRoot;
  View vCross;

  private boolean isTakeScreenshot = false;
  private boolean isShareClick = false;
  String trackingId;

  private CallbackManager fbCallbackManager;

  private final Runnable makeScreen = new Runnable() {
    @Override public void run() {
      Bitmap root = vRoot.getDrawingCache();
      if (root != null) {
        Bitmap screenshot = Bitmap.createBitmap(vRoot.getDrawingCache());
        SharePhotoContent.Builder builder = new SharePhotoContent.Builder();

        if (screenshot != null) {
          SharePhoto photo = new SharePhoto.Builder().setBitmap(screenshot).build();
          builder.addPhoto(photo);
        }

        SharePhotoContent content = builder.build();
        shareButton.setShareContent(content);

        isTakeScreenshot = true;
      }
    }
  };

  private GetTrackingInfoClient trackingInfoClient;

  Typeface bold;
  Typeface regular;

  @Override protected void onCreate(Bundle savedStateInstance) {
    super.onCreate(savedStateInstance);

    AssetManager assets = getAssets();
    bold = Typeface.createFromAsset(assets, getString(R.string.font_roboto_bold));
    regular = Typeface.createFromAsset(assets, getString(R.string.font_roboto_regular));

    trackingId = IntentExtractorHelper.getTrackingId(getIntent());
    if (!TextUtils.isEmpty(trackingId)) {
      trackingInfoClient = new GetTrackingInfoClient();
    }

    fbCallbackManager = CallbackManager.Factory.create();
  }

  @Override protected void onStart() {
    super.onStart();

    if (trackingInfoClient != null) {
      trackingInfoClient.registerListener(this);
      showProgressFragment();
      trackingInfoClient.getTrackingInfo(trackingId);
    }
  }

  @Override protected void onStop() {
    super.onStop();

    if (trackingInfoClient != null) {
      trackingInfoClient.unregisterListener();
    }
  }

  @Override protected void onPause() {
    super.onPause();

    if (isShareClick) {
      App.getHandlerInstance().postDelayed(new Runnable() {
        @Override public void run() {
          finish();
        }
      }, 1500);
      isShareClick = false;
    }
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    fbCallbackManager.onActivityResult(requestCode, resultCode, data);
  }

  @Override public void onClick(View v) {
    int id = v.getId();
    switch (id) {
      case R.id.iv_cross:
        finish();
        break;
      case R.id.btn_fb_share:
        if (!DeviceHelper.isPackageInstalled(Constants.FACEBOOK_PACKAGE)) {
          showFacebookUpdateDialog();
        } else {
          isShareClick = true;
        }
        break;
      default:
        Log.d(TAG,
            "Click on not processed view with id " + getResources().getResourceEntryName(id));
        break;
    }
  }

  @Override public void onClickDialog(SugarmanDialog dialog, DialogButton button) {
    String id = dialog.getId();

    switch (id) {
      case DialogConstants.API_GET_TRACKIN_INFO_FAILURE_ID:
        dialog.dismiss();
        finish();
        break;
      case DialogConstants.CHECK_FACEBOOK_ID:
        dialog.dismiss();
        if (button == DialogButton.POSITIVE) {
          Intent i = new Intent(Intent.ACTION_VIEW);
          i.setData(Uri.parse(
              String.format(Constants.GOOGLE_PLAY_APP_URL_TEMPLATE, Constants.FACEBOOK_PACKAGE)));
          startActivity(i);
        }
        break;
      default:
        super.onClickDialog(dialog, button);
        break;
    }
  }

  @Override public void onApiGetTrackingInfoFailure(String message) {
    closeProgressFragment();
    if (DeviceHelper.isNetworkConnected()) {
      new SugarmanDialog.Builder(this, DialogConstants.API_GET_TRACKIN_INFO_FAILURE_ID).content(
          message).btnCallback(this).show();
    } else {
      showNoInternetConnectionDialog();
    }
  }

  void makeScreenshot() {
    if (!isTakeScreenshot && vRoot != null) {
      vRoot.postDelayed(makeScreen,
          Config.MAKE_SCREENSHOT_DELAY); // delay for set loaded image to imageview
    }
  }

  private void showFacebookUpdateDialog() {
    if (DeviceHelper.isNetworkConnected()) {
      new SugarmanDialog.Builder(this, DialogConstants.CHECK_FACEBOOK_ID).content(
          R.string.check_facebook)
          .btnCallback(this)
          .positiveText(R.string.update_fb)
          .negativeText(R.string.cancel)
          .show();
    } else {
      showNoInternetConnectionDialog();
    }
  }
}

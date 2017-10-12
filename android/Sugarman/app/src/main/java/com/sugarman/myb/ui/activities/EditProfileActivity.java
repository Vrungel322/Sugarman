package com.sugarman.myb.ui.activities;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.squareup.picasso.Picasso;
import com.sugarman.myb.R;
import com.sugarman.myb.api.clients.EditProfileClient;
import com.sugarman.myb.api.clients.RefreshUserDataClient;
import com.sugarman.myb.api.models.responses.users.UsersResponse;
import com.sugarman.myb.base.BasicActivity;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.listeners.ApiBaseListener;
import com.sugarman.myb.listeners.ApiRefreshUserDataListener;
import com.sugarman.myb.ui.dialogs.SugarmanDialog;
import com.sugarman.myb.ui.views.MaskImage;
import com.sugarman.myb.ui.views.MaskTransformation;
import com.sugarman.myb.utils.AnalyticsHelper;
import com.sugarman.myb.utils.DeviceHelper;
import com.sugarman.myb.utils.SaveFileHelper;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;
import java.io.File;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import timber.log.Timber;

public class EditProfileActivity extends BasicActivity
    implements ApiBaseListener, ApiRefreshUserDataListener {
  @BindView(R.id.iv_profile_avatar) ImageView profileAvatar;
  @BindView(R.id.iv_next) ImageView nextButton;
  @BindView(R.id.iv_back) ImageView backButton;
  @BindView(R.id.tv_facebook) TextView tvFb;
  @BindView(R.id.tv_vk) TextView tvVk;
  @BindView(R.id.cb_facebook) CheckBox cbFb;
  @BindView(R.id.cb_vk) CheckBox cbVk;
  EditProfileClient editProfileClient;
  String displayNumber;
  String otp;
  File selectedFile;
  @BindView(R.id.et_name) EditText etName;
  @BindView(R.id.et_phone_number) EditText etPhone;
  @BindView(R.id.et_email) EditText etEmail;
  private CallbackManager callbackManager;
  private RefreshUserDataClient mRefreshUserDataClient;


  public static boolean isEmailValid(String email) {
    String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
    Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(email);
    return matcher.matches();
  }

  public static boolean isPhoneValid(String phone)
  {
    String expression = "^[+][0-9]{10,13}$";
    Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(phone);
    return matcher.matches();

  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    setContentView(R.layout.activity_edit_profile);
    super.onCreate(savedInstanceState);
    if (ActivityCompat.checkSelfPermission(EditProfileActivity.this,
        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(EditProfileActivity.this, new String[] {
          Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
      }, 5500);
    }


    editProfileClient = new EditProfileClient();
    editProfileClient.registerListener(this);

    etName.setText(SharedPreferenceHelper.getUserName());
    etPhone.setText(SharedPreferenceHelper.getPhoneNumber().equals("none")?"":SharedPreferenceHelper.getPhoneNumber());
    etEmail.setText(SharedPreferenceHelper.getEmail());

    mRefreshUserDataClient = new RefreshUserDataClient();
    mRefreshUserDataClient.registerListener(this);

    selectedFile = null;

    callbackManager = CallbackManager.Factory.create();
    Log.e("FBAccess", "GOVNO" + SharedPreferenceHelper.getFBAccessToken());
    if (!SharedPreferenceHelper.getFbId().equals("none")) {
      cbFb.setChecked(true);
      Log.e("FBAccess", SharedPreferenceHelper.getFBAccessToken());
    }
    if (!SharedPreferenceHelper.getVkToken().equals("none")) cbVk.setChecked(true);

    Log.e("Avatar", "Link " + SharedPreferenceHelper.getAvatar());
    if (!SharedPreferenceHelper.getAvatar().equals("")) {
      Picasso.with(this)
          .load(SharedPreferenceHelper.getAvatar())
          .placeholder(R.drawable.ic_gray_avatar)
          .fit()
          .centerCrop()
          .error(R.drawable.ic_gray_avatar)
          .transform(new MaskTransformation(this, R.drawable.mask, false, 0x00ffffff))
          .into(profileAvatar);
    }

    LoginManager.getInstance()
        .registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
          @Override public void onSuccess(LoginResult loginResult) {
            Log.e("Facebook token", "Success");
            AccessToken accessToken = loginResult.getAccessToken();
            if (accessToken != null) {
              Log.e("EditProfileActivity", "Facebook token:" + accessToken.getToken());
              SharedPreferenceHelper.saveFBExipredTokenDate(accessToken.getExpires());
              SharedPreferenceHelper.saveFBAccessToken(accessToken.getToken());
              SharedPreferenceHelper.saveFbId(accessToken.getUserId());
              cbFb.setChecked(true);
            } else {
              Log.e("Facebook", "token from facebook is null");
              onError(null);
            }
          }

          @Override public void onCancel() {
            Log.e("Facebook token", "Cancel");
          }

          @Override public void onError(FacebookException e) {
            Log.e("Facebook token", "Error " + e.getMessage());
          }
        });
  }

  @OnClick(R.id.cb_facebook) public void cbFacebookClicked() {
    Log.e("EditProfileActivity", "is checked: " + cbFb.isChecked());

    if (!cbFb.isChecked()) {
      logoutFacebook();
    } else {
      loginFacebook();
    }
  }

  @OnClick(R.id.tv_facebook) public void tvFacebookClicked() {
    Log.e("EditProfileActivity", "is checked: " + cbFb.isChecked());

    if (cbFb.isChecked()) {
      logoutFacebook();
    } else {
      loginFacebook();
    }
  }

  @OnClick(R.id.cb_vk) public void cbVkClicked() {
    if (VKSdk.isLoggedIn()) {
      logoutVk();
    } else {
      loginVk();
    }
  }

  @OnClick(R.id.tv_vk) public void tvVkClicked() {
    if (VKSdk.isLoggedIn()) {
      logoutVk();
    } else {
      loginVk();
    }
    cbVk.setChecked(VKSdk.isLoggedIn());
  }

  @OnClick(R.id.iv_next) public void ivNextClicked() {
    //SharedPreferenceHelper.saveUserName("Test name");

editProfile();
  }

  private void editProfile()
  {
    String displayName = etName.getText().toString();
    Log.e("display name", displayName);
    SharedPreferenceHelper.saveUserName(displayName);
    String displayEmail = etEmail.getText().toString();
    displayNumber = etPhone.getText().toString();

    if (isEmailValid(displayEmail)) {
      if(displayNumber.equals("")) {
        displayNumber = "none";
        Timber.e("Got in here 1");
        editProfileClient.editUser(displayNumber, displayEmail, displayName,
            SharedPreferenceHelper.getFbId(), SharedPreferenceHelper.getVkId(),
            SharedPreferenceHelper.getAvatar(), selectedFile); //brand.png
        SharedPreferenceHelper.saveEmail(displayEmail);
        nextButton.setEnabled(false);
        backButton.setEnabled(false);
        //showNextActivity();
      }
      else
      {
        if(isPhoneValid(displayNumber))
        {
          Timber.e("Got in here 2");
          editProfileClient.editUser(displayNumber, displayEmail, displayName,
              SharedPreferenceHelper.getFbId(), SharedPreferenceHelper.getVkId(),
              SharedPreferenceHelper.getAvatar(), selectedFile); //brand.png
          SharedPreferenceHelper.saveEmail(displayEmail);
          nextButton.setEnabled(false);
          backButton.setEnabled(false);
          //showNextActivity();
        }
        else
        {
          new SugarmanDialog.Builder(this, "Phone").content(getResources().getString(R.string.the_phone_is_not_valid))
              .build()
              .show();
        }
      }



    } else {
      new SugarmanDialog.Builder(this, "Email").content(getResources().getString(R.string.the_email_is_not_valid))
          .build()
          .show();
    }
    Log.e("EDIT PROFILE", "PRESSED");
  }

  @OnClick(R.id.iv_back) public void ivBackClicked() {
    finish();
  }

  @Override public void onBackPressed() {
    ivBackClicked();
  }

  @OnClick({ R.id.iv_profile_avatar, R.id.tv_change_photo }) public void ivAvatarClicked() {
    chooseGroupAvatar();
  }

  private void loginVk() {
    VKSdk.login(this, "friends, messages, email");
    cbVk.setChecked(true);
  }

  private void logoutVk() {
    VKSdk.logout();
    SharedPreferenceHelper.saveVkId("none");
    SharedPreferenceHelper.saveVkToken("none");
    cbVk.setChecked(false);
  }

  private void loginFacebook() {
    LoginManager.getInstance()
        .logInWithReadPermissions(EditProfileActivity.this,
            Arrays.asList("public_profile", "user_friends", "email", "read_custom_friendlists"));
    Log.e("Facebook", "Logged in");
    cbFb.setChecked(true);
  }

  private void logoutFacebook() {
    LoginManager.getInstance().logOut();
    SharedPreferenceHelper.clearFBDate();
    Log.e("Facebook", "Logged out");
    cbFb.setChecked(false);
  }

  @Override public void onApiUnauthorized() {

  }

  @Override public void onUpdateOldVersion() {

  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    callbackManager.onActivityResult(requestCode, resultCode, data);

    if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
      @Override public void onResult(VKAccessToken res) {
        SharedPreferenceHelper.saveVkToken(res.accessToken);
        SharedPreferenceHelper.saveVkId(res.userId);
        cbVk.setChecked(VKSdk.isLoggedIn());
      }

      @Override public void onError(VKError error) {
        // ÐŸÑ€Ð¾Ð¸Ð·Ð¾ÑˆÐ»Ð° Ð¾ÑˆÐ¸Ð±ÐºÐ° Ð°Ð²Ñ‚Ð¾Ñ€Ð¸Ð·Ð°Ñ†Ð¸Ð¸ (Ð½Ð°Ð¿Ñ€Ð¸Ð¼ÐµÑ€, Ð¿Ð¾Ð»ÑŒÐ·Ð¾Ð²Ð°Ñ‚ÐµÐ»ÑŒ Ð·Ð°Ð¿Ñ€ÐµÑ‚Ð¸Ð» Ð°Ð²Ñ‚Ð¾Ñ€Ð¸Ð·Ð°Ñ†Ð¸ÑŽ)
      }
    })) {
      ;
    }

    if (requestCode == Constants.INTENT_CHOOSER_IMAGE_REQUEST_CODE) {
      if (resultCode == RESULT_OK) {
        if (data == null || (data.getData() == null && data.getClipData() == null)) {
          // return data from camera
          processCaptureCamera();
        } else {
          // return data from gallery
          processCameraGallery(data);
        }
      } else {
      }
    }

    SharedPreferenceHelper.saveCaptureCameraUri("");
    SharedPreferenceHelper.saveCaptureCameraPath("");
  }

  private void processCameraGallery(Intent data) {
    ClipData clipData = data.getClipData();
    if (clipData != null
        && clipData.getItemAt(0) != null
        && clipData.getItemAt(0).getUri() != null) {
      Uri uri = clipData.getItemAt(0).getUri();
      selectedFile = DeviceHelper.pickedExistingPicture(uri);
    } else if (data.getData() != null) {
      String path = DeviceHelper.getRealPathFromUri(data.getData());
      selectedFile = new File(path);
    } else {
    }
    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
    Bitmap bitmap = BitmapFactory.decodeFile(selectedFile.getAbsolutePath(), bmOptions);
    bitmap = Bitmap.createBitmap(bitmap);
    MaskImage mi = new MaskImage(EditProfileActivity.this, R.drawable.mask, false, 0xfff);
    bitmap = mi.crop(bitmap);
    bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
    selectedFile = new File(SaveFileHelper.saveFileTemp(bitmap, getApplicationContext()));
    bitmap = mi.transform(bitmap);
    profileAvatar.setImageBitmap(bitmap);
  }

  private void processCaptureCamera() {
    String filePath = SharedPreferenceHelper.getCaptureCameraPath();
    String cameraUri = SharedPreferenceHelper.getCaptureCameraUri();

    selectedFile = new File(filePath);
    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
    Bitmap bitmap = BitmapFactory.decodeFile(selectedFile.getAbsolutePath(), bmOptions);
    bitmap = Bitmap.createBitmap(bitmap);
    MaskImage mi = new MaskImage(EditProfileActivity.this, R.drawable.mask, false, 0xfff);
    bitmap = mi.crop(bitmap);
    bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
    selectedFile = new File(SaveFileHelper.saveFileTemp(bitmap, getApplicationContext()));
    bitmap = mi.transform(bitmap);
    profileAvatar.setImageBitmap(bitmap);

    if (!TextUtils.isEmpty(cameraUri)) {
      Uri uri = Uri.parse(cameraUri);
      DeviceHelper.revokeWritePermission(uri);
    } else {
    }
  }

  private void chooseGroupAvatar() {
    Intent galleryIntent = DeviceHelper.getGalleryIntent();
    Pair<String[], Parcelable[]> cameraData = DeviceHelper.getCameraData();
    Parcelable[] cameraIntents = cameraData.second;

    String path = cameraData.first[0];
    String uri = cameraData.first[1];

    SharedPreferenceHelper.saveCaptureCameraUri(uri);
    SharedPreferenceHelper.saveCaptureCameraPath(path);

    if (!TextUtils.isEmpty(uri)
        && !TextUtils.isEmpty(path)
        && galleryIntent != null
        && cameraIntents != null) {
      Intent chooserIntent = Intent.createChooser(galleryIntent, getString(R.string.add_photo));
      chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents);

      startActivityForResult(chooserIntent, Constants.INTENT_CHOOSER_IMAGE_REQUEST_CODE);
    } else {
    }
  }

  @Override public void onApiRefreshUserDataSuccess(UsersResponse response) {
    if (response.getResult() != null) {
      AnalyticsHelper.reportLogin(true);
      Log.e("ApiRefreshUserData", "Called");
      SharedPreferenceHelper.saveUser(response.getUser());
      otp = response.getResult().getUser().getPhoneOTP();
      Timber.e("OTP Rewritten");
      showNextActivity();
      Log.e("Token", "huy" + response.toString());
    } else

    {
      new SugarmanDialog.Builder(this, "soc network").content(response.getError()).build().show();
      if(response.getError().equals("This Facebook account is already created")) {
        logoutFacebook();
      }
      if(response.getError().equals("This VK account is already created"))
      {
        logoutVk();
      }
    }
  }

  @Override public void onApiRefreshUserDataFailure(String message) {

  }

  private void showNextActivity() {
    Timber.e(SharedPreferenceHelper.getPhoneNumber() + " " + (etPhone.getText().toString()));
    if (!SharedPreferenceHelper.getPhoneNumber().equals(etPhone.getText().toString())
        && !etPhone.getText().toString().equals("")) {
      Intent intent = new Intent(EditProfileActivity.this, ApproveOtpActivity.class);
      intent.putExtra("otp", otp);
      intent.putExtra("showSettings", false);
      intent.putExtra("phone", displayNumber);
      startActivity(intent);
    } else {
      SharedPreferenceHelper.savePhoneNumber(etPhone.getText().toString());
      if (SharedPreferenceHelper.introIsShown()) {
        Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
      } else {
        Intent intent = new Intent(EditProfileActivity.this, IntroActivity.class);
        intent.putExtra(IntroActivity.CODE_IS_OPEN_LOGIN_ACTIVITY, true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
      }
    }
  }
}
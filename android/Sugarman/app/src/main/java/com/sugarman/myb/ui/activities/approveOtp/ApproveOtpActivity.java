package com.sugarman.myb.ui.activities.approveOtp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.sugarman.myb.R;
import com.sugarman.myb.api.clients.ResendMessageClient;
import com.sugarman.myb.api.models.responses.ApproveOtpResponse;
import com.sugarman.myb.api.models.responses.users.Tokens;
import com.sugarman.myb.base.BasicActivity;
import com.sugarman.myb.ui.activities.LoginActivity;
import com.sugarman.myb.ui.activities.PhoneLoginActivity;
import com.sugarman.myb.ui.activities.editProfile.EditProfileActivity;
import com.sugarman.myb.ui.activities.mainScreeen.MainActivity;
import com.sugarman.myb.ui.activities.splash.SplashActivity;
import com.sugarman.myb.ui.dialogs.SugarmanDialog;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import timber.log.Timber;

public class ApproveOtpActivity extends BasicActivity implements IApproveOtpActivityView {

  @InjectPresenter ApproveOtpActivityPresenter mPresenter;

  ResendMessageClient resendClient;
  boolean showSettings = true;
  @BindView(R.id.tv_phone_number) TextView phoneNumber;
  @BindView(R.id.et_otp) EditText otpEditText;
  @BindView(R.id.iv_back) ImageView backButton;
  @BindView(R.id.iv_cart) ImageView btn;
  @BindView(R.id.resend_code) TextView resendCode;
  @BindView(R.id.tvChangePhone) TextView tvChangePhone;
  String otp;
  String phoneNumberStr;
  private Tokens mTokens;
  private CountDownTimer mTimer;
  private String nameParentActivity;

  @Override protected void onCreate(Bundle savedInstanceState) {
    setContentView(R.layout.activity_approve_otp);
    super.onCreate(savedInstanceState);
    otp = getIntent().getStringExtra("otp");
    nameParentActivity = getIntent().getStringExtra("nameParentActivity");
    ButterKnife.bind(this);
    phoneNumberStr = getIntent().getStringExtra("phone");
    mTokens = getIntent().getParcelableExtra("token");

    phoneNumber.setText(phoneNumberStr);

    showSettings = getIntent().getBooleanExtra("showSettings", true);

    Timber.e("ApproveOtp " + SharedPreferenceHelper.getAccessToken());

    resendClient = new ResendMessageClient();

    resendCode.setTextColor(ContextCompat.getColor(ApproveOtpActivity.this, R.color.gray));

    mTimer = new CountDownTimer(30000, 1000) {

      @Override public void onTick(long l) {
        resendCode.setClickable(false);
        resendCode.setText(getResources().getString(R.string.resend_in)
            + " "
            + l / 1000
            + " "
            + getResources().getString(R.string.seconds));
      }

      @Override public void onFinish() {
        resendCode.setClickable(true);
        resendCode.setTextColor(ContextCompat.getColor(ApproveOtpActivity.this, R.color.red));
        resendCode.setText(getString(R.string.resend_code));
      }
    }.start();

    btn.setOnClickListener(
        view -> mPresenter.approveOtp(SharedPreferenceHelper.getUserId(), phoneNumberStr,
            otpEditText.getText().toString()));
  }

  @OnClick(R.id.resend_code) public void startTimer() {
    resendCode.setTextColor(ContextCompat.getColor(ApproveOtpActivity.this, R.color.gray));
    resendClient.resendMessage(phoneNumberStr);
    mTimer = new CountDownTimer(60000, 1000) {

      @Override public void onTick(long l) {
        resendCode.setClickable(false);
        resendCode.setText(getString(R.string.wait_for) + " " + l / 1000 + " seconds");
      }

      @Override public void onFinish() {
        resendCode.setClickable(true);
        resendCode.setTextColor(ContextCompat.getColor(ApproveOtpActivity.this, R.color.red));
        resendCode.setText(getString(R.string.resend_code));
      }
    }.start();
  }

  @OnClick(R.id.tvChangePhone) public void tvChangePhoneClicked() {
    Timber.e("Name parent activity " + nameParentActivity);
    if (nameParentActivity.equals(SplashActivity.class.getName())) {
      Intent intent = new Intent(ApproveOtpActivity.this, LoginActivity.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
      startActivity(intent);
    }
    if (nameParentActivity.equals(PhoneLoginActivity.class.getName())) {
      Intent intent = new Intent(ApproveOtpActivity.this, PhoneLoginActivity.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
      startActivity(intent);
    }
    if (nameParentActivity.equals(EditProfileActivity.class.getName())) {
      finish();
    }
  }

  @OnClick(R.id.iv_back) public void nextActivity() {
    finish();
  }

  @Override public void onApiUnauthorized() {

  }

  @Override public void onUpdateOldVersion() {

  }

  @Override public void onApiRefreshUserDataFailure(String errorMessage) {

  }

  @Override public void onApiApproveOtpSuccess(ApproveOtpResponse response) {
    Timber.e("OTP" + otp);
    if (response.getCode().equals("0")) {
      Intent intent;
      if (showSettings) {
        intent = new Intent(ApproveOtpActivity.this, EditProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
      } else {
        intent = new Intent(ApproveOtpActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
      }
      Timber.e("phoneNumberStr " + phoneNumberStr);
      SharedPreferenceHelper.savePhoneNumber(phoneNumberStr);

      if (mTokens != null) {
        SharedPreferenceHelper.saveToken(mTokens);
      }
      SharedPreferenceHelper.setOTPStatus(response.getUser().getNeedOTP());
      startActivity(intent);
    } else if (response.getCode().equals("1")) {
      new SugarmanDialog.Builder(this, "Error").content(R.string.error_code_otp).show();
    }
  }
}

package com.sugarman.myb.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.sugarman.myb.R;
import com.sugarman.myb.api.clients.ApproveOtpClient;
import com.sugarman.myb.api.clients.ResendMessageClient;
import com.sugarman.myb.api.models.responses.ApproveOtpResponse;
import com.sugarman.myb.api.models.responses.users.Tokens;
import com.sugarman.myb.listeners.ApiApproveOtp;
import com.sugarman.myb.ui.dialogs.SugarmanDialog;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import timber.log.Timber;

public class ApproveOtpActivity extends AppCompatActivity implements ApiApproveOtp {

  ApproveOtpClient client;
  ResendMessageClient resendClient;
  boolean showSettings = true;
  @BindView(R.id.tv_phone_number) TextView phoneNumber;
  @BindView(R.id.et_otp) EditText otpEditText;
  @BindView(R.id.iv_back) ImageView backButton;
  @BindView(R.id.resend_code) TextView resendCode;
  String otp;
  String phoneNumberStr;
  private Tokens mTokens;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_approve_otp);
    otp = getIntent().getStringExtra("otp");
    ButterKnife.bind(this);
    phoneNumberStr = getIntent().getStringExtra("phone");
    mTokens = getIntent().getParcelableExtra("token");

    phoneNumber.setText(phoneNumberStr);

    showSettings = getIntent().getBooleanExtra("showSettings", true);

    Timber.e("ApproveOtp " + SharedPreferenceHelper.getAccessToken());

    client = new ApproveOtpClient();
    client.registerListener(this);

    resendClient = new ResendMessageClient();

    ImageView btn = (ImageView) findViewById(R.id.iv_cart);
    btn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        client.approveOtp(SharedPreferenceHelper.getUserId(),
            phoneNumberStr, otpEditText.getText().toString());
      }
    });
  }

  @OnClick(R.id.iv_back) public void nextActivity() {
    finish();
  }
  @OnClick(R.id.resend_code) public void resendCode() {

    resendClient.resendMessage(phoneNumberStr);

  }

  @Override public void onApiUnauthorized() {

  }

  @Override public void onUpdateOldVersion() {

  }

  @Override public void onApiApproveOtpSuccess(ApproveOtpResponse response) {
    Timber.e("OTP" + otp);
    if (otp != null) {
      if (response.getCode().equals("0")) {
        Intent intent;
        if (showSettings) {
          intent = new Intent(ApproveOtpActivity.this, EditProfileActivity.class);
          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        } else {
          intent = new Intent(ApproveOtpActivity.this, MainActivity.class);
        }
        Timber.e("phoneNumberStr " + phoneNumberStr);
        SharedPreferenceHelper.savePhoneNumber(phoneNumberStr);

        if(mTokens!=null) {
          SharedPreferenceHelper.saveToken(mTokens);
        }
        startActivity(intent);
      } else if (response.getCode().equals("1")) {
        new SugarmanDialog.Builder(this, "Error").content("Please check the code you have entered!")
            .show();
      }
    }
  }

  @Override public void onApiApproveOtpFailure(String message) {
    Timber.e("GOENO");
  }
}

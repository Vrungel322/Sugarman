package com.sugarman.myb.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import butterknife.BindView;
import butterknife.OnClick;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sugarman.myb.App;
import com.sugarman.myb.R;
import com.sugarman.myb.api.models.responses.users.UsersResponse;
import com.sugarman.myb.listeners.ApiRefreshUserDataListener;
import com.sugarman.myb.models.CountryCodeEntity;
import com.sugarman.myb.ui.dialogs.SugarmanDialog;
import com.sugarman.myb.utils.Converters;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import timber.log.Timber;

public class PhoneLoginActivity extends GetUserInfoActivity implements ApiRefreshUserDataListener {

  @BindView(R.id.et_phone_number) EditText etPhoneNumber;
  @BindView(R.id.iv_cart) ImageView nextButton;
  @BindView(R.id.iv_back) ImageView backButton;
  @BindView(R.id.spCountryCode) Spinner mSpinnerCountryCode;
  String phoneNumber;
  private List<CountryCodeEntity> mCountryCodeEntities;

  public static boolean isPhoneValid(String phone) {
    String expression = "^[+][0-9]{8,15}$";
    Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(phone);
    return matcher.matches();
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    setContentView(R.layout.activity_phone_login);
    super.onCreate(savedInstanceState);

    setUpSpinner();

    mSpinnerCountryCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        etPhoneNumber.setText("+" + mCountryCodeEntities.get(position).getCode());
        etPhoneNumber.setSelection(etPhoneNumber.getText().length());
      }

      @Override public void onNothingSelected(AdapterView<?> arg0) {
      }
    });

    etPhoneNumber.setSelection(etPhoneNumber.getText().length());
  }

  @Override protected void onResume() {
    super.onResume();
    nextButton.setEnabled(true);
  }

  @OnClick(R.id.iv_cart) public void toApproveOtp() {
    phoneNumber = etPhoneNumber.getText().toString();
    //SharedPreferenceHelper.savePhoneNumber(phoneNumber);
    if (isPhoneValid(phoneNumber)) {
      nextButton.setEnabled(false);
      refreshUserData("none", "none", "none", phoneNumber, "enter@email.com", phoneNumber, "none",
          "none", "none");
    } else {
      new SugarmanDialog.Builder(this, "Phone").content(
          getResources().getString(R.string.the_phone_is_not_valid)).build().show();
    }
  }

  @Override public void onApiRefreshUserDataSuccess(UsersResponse response) {
    super.onApiRefreshUserDataSuccess(response);
    Intent intent = new Intent(PhoneLoginActivity.this, ApproveOtpActivity.class);
    intent.putExtra("otp", response.getResult().getUser().getPhoneOTP());
    intent.putExtra("token", response.getResult().getTokens());
    intent.putExtra("phone", phoneNumber);
    intent.putExtra("nameParentActivity", PhoneLoginActivity.class.getName());

    startActivity(intent);
  }

  private void setUpSpinner() {
    TelephonyManager manager =
        (TelephonyManager) App.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
    String currentCountry = new Locale("",manager.getSimCountryIso().toUpperCase()).getDisplayCountry(new Locale("en"));
    Timber.e("COUNTRY " + currentCountry);
    int currentCountryPosition = 0;


    String myJson = Converters.loadAssetTextAsString(getBaseContext(), "countryCode.json");
    Type listType = new TypeToken<List<CountryCodeEntity>>() {
    }.getType();
    mCountryCodeEntities = (List<CountryCodeEntity>) new Gson().fromJson(myJson, listType);
    List<String> items = new ArrayList<>();
    for (int i = 0; i < mCountryCodeEntities.size(); i++) {
      items.add(mCountryCodeEntities.get(i).getCountryName());
      if (mCountryCodeEntities.get(i).getCountryName().contains(currentCountry)){
        currentCountryPosition = i;
      }
    }
    ArrayAdapter<String> adapter =
        new ArrayAdapter<String>(this, R.layout.item_country_code, items);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    mSpinnerCountryCode.setAdapter(adapter);
    mSpinnerCountryCode.setSelection(currentCountryPosition);
    etPhoneNumber.setText(mCountryCodeEntities.get(0).getCode());
  }

  @OnClick(R.id.iv_back) public void goToLogin() {
    clearLoginData();
    Intent intent = new Intent(PhoneLoginActivity.this, LoginActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    startActivity(intent);
  }
}

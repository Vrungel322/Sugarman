package com.sugarman.myb.ui.activities.checkout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.squareup.picasso.CustomPicasso;
import com.sugarman.myb.R;
import com.sugarman.myb.base.BasicActivity;
import com.sugarman.myb.constants.Config;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.ui.views.CropCircleTransformation;
import com.sugarman.myb.utils.apps_Fly.AppsFlyerEventSender;
import timber.log.Timber;

public class CheckoutActivity extends BasicActivity
    implements ICheckoutActivityView, View.OnClickListener {
  private static final int PAYPAL_REQUEST_CODE = 123;
  private static PayPalConfiguration config;
  @InjectPresenter CheckoutActivityPresenter presenter;
  @BindView(R.id.iv_back) ImageView backButton;
  @BindView(R.id.purchase_details_tv) TextView purchaseDetailsTV;
  @BindView(R.id.buy_now_for_x) TextView buyButton;
  @BindView(R.id.tvTotalPrice) TextView totalPrice;
  int num = 1;
  String productPrice;
  String productImageUrl;
  String productName = "";
  @BindView(R.id.etCountryName) EditText etCountryName;
  @BindView(R.id.etCityName) EditText etCityName;
  @BindView(R.id.etStreetName) EditText etStreetName;
  @BindView(R.id.etZipCode) EditText etZipCode;
  @BindView(R.id.etFullName) EditText etFullName;
  @BindView(R.id.etPhoneNumber) EditText etPhoneNumber;
  private String paymentType;

  @Override protected void onCreate(Bundle savedInstanceState) {
    setContentView(R.layout.activity_checkout);
    super.onCreate(savedInstanceState);

    AppsFlyerEventSender.sendEvent("af_tap_buy_for_money");

    initPayPal();

    Intent intentPayPal = new Intent(this, PayPalService.class);
    intentPayPal.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
    startService(intentPayPal);

    Intent intent = getIntent();
    int type = intent.getIntExtra("checkout", -1);
    productPrice = intent.getStringExtra("productPrice");
    productImageUrl = intent.getStringExtra("productImageId");
    productName = intent.getStringExtra("productName");
    paymentType = intent.getStringExtra("paymentType");
    Log.e("checkout", "" + type);

    totalPrice.setText(productPrice + " $");

    backButton.setOnClickListener(view -> finish());

    View separator = new View(this);
    ViewGroup.LayoutParams params =
        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);
    separator.setLayoutParams(params);
    separator.setBackgroundColor(0xffEFEFEF);

    LayoutInflater vi =
        (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View v = vi.inflate(R.layout.card_linear_text, null);
    v.setBackgroundColor(0xccc);

    TextView textView = (TextView) v.findViewById(R.id.firstText);
    textView.setText("Friends invited");
    TextView textView1 = (TextView) v.findViewById(R.id.secondText);
    textView1.setText("5/5");

    LinearLayout verticalLayout = (LinearLayout) findViewById(R.id.vertical_layout);
    CardView insertPoint = (CardView) findViewById(R.id.purchase_details_card);
    // Log.e("Checkout", Integer.toString(insertPoint.getChildCount()));

    //verticalLayout.addView(v, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    //verticalLayout.addView(separator);
    View v1 = vi.inflate(R.layout.card_linear_text_with_image_and_icons, null);
    View v2 = vi.inflate(R.layout.card_linear_text_with_image, null);
    //v1.setBackgroundColor(0xccc);
    //verticalLayout.addView(v1, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    switch (type) {
      case 1:
        // verticalLayout.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        // addSeparator(verticalLayout,1);
        verticalLayout.addView(v1, 0,
            new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        final TextView numberOfItems = (TextView) v1.findViewById(R.id.secondText);
        ImageView productImage = (ImageView) v1.findViewById(R.id.product_image);
        TextView productNametv = (TextView) v1.findViewById(R.id.firstText);
        productNametv.setText(productName);

        Timber.e(productImageUrl);
        CustomPicasso.with(this)
            //.load(Uri.parse(productImageUrl))
            .load(Integer.valueOf(productImageUrl))
            .fit()
            .centerCrop()
            .error(R.drawable.ic_gray_avatar)
            .error(R.drawable.ic_red_avatar)
            .transform(new CropCircleTransformation(0x00ffffff, 4))
            .into(productImage);

        //productImage.setImageDrawable(getResources().getDrawable(productImageId));

        ImageView plusButton = (ImageView) v1.findViewById(R.id.plus);
        plusButton.setOnClickListener(view -> {
          if (paymentType.equals(Constants.FREE_PAYMENT_TYPE)){
            showToastMessage(R.string.can_not_increase_amount);
          }
          else {
            num++;
            numberOfItems.setText(Integer.toString(num));
          }
          totalPrice.setText(num * Double.parseDouble(productPrice) + " $");
        });

        ImageView minusButton = (ImageView) v1.findViewById(R.id.minus);
        minusButton.setOnClickListener(view -> {
          if (paymentType.equals(Constants.FREE_PAYMENT_TYPE)){
            showToastMessage(R.string.can_not_increase_amount);
          }else {
            if (num > 1) {
              num--;
              numberOfItems.setText(Integer.toString(num));
              totalPrice.setText(num * Double.parseDouble(productPrice) + " $");
            }
          }
        });
        addSeparator(verticalLayout, 1);
        //purchaseDetailsTV.setText("Purchase gift details");
        purchaseDetailsTV.setText(R.string.purchase_details);
        break;
      case 2:
        verticalLayout.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT));
        verticalLayout.addView(v2, 1,
            new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        purchaseDetailsTV.setText("Purchase gift details");
        break;
    }
  }

  @Override protected void onDestroy() {
    stopService(new Intent(this, PayPalService.class));
    super.onDestroy();
  }

  @OnClick(R.id.buy_now_for_x) public void bBuyClicked() {

    if (etCountryName.getText().toString().isEmpty()) {
      etCountryName.setError(
          String.format(getString(R.string.empty_field_denied), getString(R.string.country)));
    }
    if (etCityName.getText().toString().isEmpty()) {
      etCityName.setError(
          String.format(getString(R.string.empty_field_denied), getString(R.string.city)));
    }
    if (etStreetName.getText().toString().isEmpty()) {
      etStreetName.setError(
          String.format(getString(R.string.empty_field_denied), getString(R.string.address)));
    }
    if (etZipCode.getText().toString().isEmpty()) {
      etZipCode.setError(
          String.format(getString(R.string.empty_field_denied), getString(R.string.zip_code)));
    }
    if (etFullName.getText().toString().isEmpty()) {
      etFullName.setError(
          String.format(getString(R.string.empty_field_denied), getString(R.string.full_name)));
    }
    if (etPhoneNumber.getText().toString().isEmpty()) {
      etPhoneNumber.setError(
          String.format(getString(R.string.empty_field_denied), getString(R.string.phone)));
    }

    if (etCountryName.getText().length() > 0
        && etCityName.getText().length() > 0
        && etStreetName.getText().length() > 0
        && etZipCode.getText().length() > 0
        && etFullName.getText().length() > 0
        && etPhoneNumber.getText().length() > 0) {

      AppsFlyerEventSender.sendEvent("af_tap_buy_checkout");

      presenter.sendPurchaseData(etCountryName.getText().toString(),
          etCityName.getText().toString(), etStreetName.getText().toString(),
          etZipCode.getText().toString(), etFullName.getText().toString(),
          etPhoneNumber.getText().toString(),
          String.valueOf(num * Double.parseDouble(productPrice)), num, productName, productPrice,
          paymentType);
    }
  }

  private void getMoneyPayPal(String amountPrice) {
    //Creating a paypalpayment
    PayPalPayment payment =
        new PayPalPayment(new java.math.BigDecimal(amountPrice), "USD", "Simplified Coding Fee",
            PayPalPayment.PAYMENT_INTENT_SALE);

    //Creating Paypal Payment activity intent
    Intent intent = new Intent(this, PaymentActivity.class);

    //putting the paypal configuration to the intent
    intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

    //Puting paypal payment to the intent
    intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

    //Starting the intent activity for result
    //the request code will be used on the method onActivityResult
    startActivityForResult(intent, PAYPAL_REQUEST_CODE);
  }

  void addSeparator(ViewGroup v, int pos) {
    View separator = new View(this);
    ViewGroup.LayoutParams params =
        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);
    separator.setLayoutParams(params);
    separator.setBackgroundColor(0xffcccccc);
    v.addView(separator, pos);
  }

  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    //If the result is from paypal
    if (requestCode == PAYPAL_REQUEST_CODE) {

      //If the result is OK i.e. user has not canceled the payment
      if (resultCode == Activity.RESULT_OK) {
        //Getting the payment confirmation
        PaymentConfirmation confirm =
            data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

        //if confirmation is not null
        if (confirm != null) {
          finish();

          //try {
          //Getting the payment details
          //String paymentDetails = confirm.toJSONObject().toString(4);
          //Timber.e("paymentExample " + paymentDetails);

          //Starting a new activity for the payment details and also putting the payment details with intent
          //startActivity(new Intent(this, ConfirmationActivity.class).putExtra("PaymentDetails",
          //    paymentDetails)
          //    .putExtra("PaymentAmount", String.valueOf(num * Double.parseDouble(productPrice))));
          //} catch (JSONException e) {
          //  Timber.e("paymentExample " + "an extremely unlikely failure occurred: ", e);
          //}
        }
      } else if (resultCode == Activity.RESULT_CANCELED) {
        Timber.e("paymentExample " + "The user canceled.");
      } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
        Timber.e("paymentExample "
            + "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
      }
    }
  }

  private void initPayPal() {
    //For real pay change ENVIRONMENT_SANDBOX and PAYPAL_CLIENT_ID
    config = new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION)
        .clientId(Config.PAYPAL_CLIENT_ID)
        .acceptCreditCards(false);
  }

  @Override public void startPayPalTransaction(String amountPrice) {
    getMoneyPayPal(amountPrice);
    showToastMessage(getString(R.string.purchase_request_send));
  }

  @Override public void onClick(View view) {

  }
}

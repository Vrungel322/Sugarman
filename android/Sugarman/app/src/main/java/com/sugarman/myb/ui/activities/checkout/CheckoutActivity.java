package com.sugarman.myb.ui.activities.checkout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.paypal.android.MEP.CheckoutButton;
import com.paypal.android.MEP.PayPal;
import com.paypal.android.MEP.PayPalActivity;
import com.paypal.android.MEP.PayPalInvoiceData;
import com.paypal.android.MEP.PayPalPayment;
import com.sugarman.myb.R;
import com.sugarman.myb.base.BasicActivity;
import com.sugarman.myb.ui.dialogs.SugarmanDialog;
import java.math.BigDecimal;
import timber.log.Timber;


/*
        EditText editText = (EditText) findViewById(R.id.myTextViewId);
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);*/

//эта штука нужна чтоб поставить фокус на EditText по нажатию на другую вьюху

public class CheckoutActivity extends BasicActivity implements ICheckoutActivityView, View.OnClickListener {
  @InjectPresenter CheckoutActivityPresenter presenter;

  @BindView(R.id.iv_back) ImageView backButton;
  @BindView(R.id.purchase_details_tv) TextView purchaseDetailsTV;
  @BindView(R.id.buy_now_for_x) TextView buyButton;
  @BindView(R.id.tvTotalPrice) TextView totalPrice;
  int num = 1;
  int productPrice = 0;
  int productImageId = 0;
  String productName = "";
  @BindView(R.id.etCountryName) EditText etCountryName;
  @BindView(R.id.etCityName) EditText etCityName;
  @BindView(R.id.etStreetName) EditText etStreetName;
  @BindView(R.id.etZipCode) EditText etZipCode;
  @BindView(R.id.etFullName) EditText etFullName;
  @BindView(R.id.etPhoneNumber) EditText etPhoneNumber;

  @Override protected void onCreate(Bundle savedInstanceState) {
    setContentView(R.layout.activity_checkout);
    super.onCreate(savedInstanceState);

    showPayPalButton();

    Intent intent = getIntent();
    int type = intent.getIntExtra("checkout", -1);
    productPrice = intent.getIntExtra("productPrice", 0);
    productImageId = intent.getIntExtra("productImageId", 0);
    productName = intent.getStringExtra("productName");
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

        productImage.setImageDrawable(getResources().getDrawable(productImageId));

        ImageView plusButton = (ImageView) v1.findViewById(R.id.plus);
        plusButton.setOnClickListener(view -> {
          num++;
          numberOfItems.setText(Integer.toString(num));
          totalPrice.setText(num * productPrice + " $");
        });

        ImageView minusButton = (ImageView) v1.findViewById(R.id.minus);
        minusButton.setOnClickListener(view -> {
          if (num > 1) {
            num--;
            numberOfItems.setText(Integer.toString(num));
            totalPrice.setText(num * productPrice + " $");
          }
        });
        addSeparator(verticalLayout, 1);
        //purchaseDetailsTV.setText("Purchase gift details");
        purchaseDetailsTV.setText("Purchase details");
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

  @OnClick(R.id.buy_now_for_x) public void bBuyClicked() {
    if (etCountryName.getText().toString().isEmpty()) {
      etCountryName.setError(String.format(getString(R.string.empty_field_denied), "Country"));
    }
    if (etCityName.getText().toString().isEmpty()) {
      etCityName.setError(String.format(getString(R.string.empty_field_denied), "City"));
    }
    if (etStreetName.getText().toString().isEmpty()) {
      etStreetName.setError(String.format(getString(R.string.empty_field_denied), "Address"));
    }
    if (etZipCode.getText().toString().isEmpty()) {
      etZipCode.setError(String.format(getString(R.string.empty_field_denied), "Zip Code"));
    }
    if (etFullName.getText().toString().isEmpty()) {
      etFullName.setError(String.format(getString(R.string.empty_field_denied), "Full Name"));
    }
    if (etPhoneNumber.getText().toString().isEmpty()) {
      etPhoneNumber.setError(String.format(getString(R.string.empty_field_denied), "Phone"));
    }

    if (etCountryName.getText().length() > 0
        && etCityName.getText().length() > 0
        && etStreetName.getText().length() > 0
        && etZipCode.getText().length() > 0
        && etFullName.getText().length() > 0
        && etPhoneNumber.getText().length() > 0) {
      PayPalPayment newPayment = new
          PayPalPayment();
      newPayment.setSubtotal(new BigDecimal(10));
      newPayment.setCurrencyType("USD");
      newPayment.setRecipient("my@email.com");
      newPayment.setMerchantName("My Company");
      Intent paypalIntent = PayPal.getInstance().checkout(newPayment, this);
      this.startActivityForResult(paypalIntent, 1);


    }
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
    PayPalActivityResult(requestCode,resultCode, data);
  }

  public void PayPalActivityResult(int requestCode, int resultCode, Intent data) {
    switch (resultCode) {
      // The payment succeeded
      case Activity.RESULT_OK:
        //String payKey = intent.getStringExtra(PayPalActivity.EXTRA_PAY_KEY);
        String payKey =
            data.getStringExtra(PayPalActivity.EXTRA_PAY_KEY);
        Timber.e("Payment Succeed " + payKey);

        presenter.sendPurchaseData(etCountryName.getText().toString(),
            etCityName.getText().toString(), etStreetName.getText().toString(),
            etZipCode.getText().toString(), etFullName.getText().toString(),
            etPhoneNumber.getText().toString(), num*productPrice,num,productName);


        break;

      // The payment was canceled
      case Activity.RESULT_CANCELED:
        Timber.e("Payment Cancelled");
        break;

      // The payment failed, get the error from the EXTRA_ERROR_ID and EXTRA_ERROR_MESSAGE
      case PayPalActivity.RESULT_FAILURE:
        String errorID =
            data.getStringExtra(PayPalActivity.EXTRA_ERROR_ID);
        String errorMessage =
            data.getStringExtra(PayPalActivity.EXTRA_ERROR_MESSAGE);
        Timber.e("Payment Failed " + errorMessage);
        new SugarmanDialog.Builder(CheckoutActivity.this,"PayPal").content(errorMessage).build().show();

    }
  }
  private void showPayPalButton() {
    PayPal ppObj = PayPal.getInstance();

   // CheckoutButton launchPayPalButton =
   //     ppObj.getCheckoutButton(this, PayPal.BUTTON_278x43,
   //         CheckoutButton.TEXT_PAY);
   // RelativeLayout.LayoutParams params = new
   //     RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
   //     RelativeLayout.LayoutParams.WRAP_CONTENT);
   // params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
   // params.bottomMargin = 10;
   // launchPayPalButton.setLayoutParams(params);
   // launchPayPalButton.setOnClickListener(this);
   // ((RelativeLayout)findViewById(R.id.rl_buy_buttons)).addView(launchPayPalButton);
  }

  @Override public void finishCheckoutActivity() {
    showToastMessage(getString(R.string.purchase_request_send));
    finish();
  }

  @Override public void onClick(View view) {

    PayPalPayment newPayment = new
        PayPalPayment();
    newPayment.setSubtotal(new BigDecimal(num*productPrice));
    newPayment.setCurrencyType("USD");
    newPayment.setRecipient("my@email.com");
    newPayment.setMerchantName("Sugarman");
    Intent paypalIntent = PayPal.getInstance().checkout(newPayment, this);
    this.startActivityForResult(paypalIntent, 1);


  }
}

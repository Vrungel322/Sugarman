package com.sugarman.myb.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.sugarman.myb.R;
import com.sugarman.myb.ui.activities.checkout.CheckoutActivity;
import com.sugarman.myb.ui.activities.shopInviteFriend.ShopInviteFriendsActivity;

public class ProductDetailsActivity extends AppCompatActivity {
  ImageView backButton;
  TextView productName;
  TextView buyNowFor;
  TextView freeForFriends;
  ImageView productImage;
  int productPrice = 0;
  int productImageId = 0;
  String productNameString = "";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_product_details);

    productName = (TextView) findViewById(R.id.product_name);
    buyNowFor = (TextView) findViewById(R.id.buy_now_for_x);
    freeForFriends = (TextView) findViewById(R.id.free_for_x_friends);
    productImage = (ImageView) findViewById(R.id.product_image);

    backButton = (ImageView) findViewById(R.id.iv_back);
    backButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        finish();
      }
    });
    Intent intent = getIntent();
    final int productId = intent.getIntExtra("productId", -1);

    buyNowFor.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        //Intent intent = new Intent(ProductDetailsActivity.this, CheckoutActivity.class);
        //intent.putExtra("checkout", 1);
        //intent.putExtra("productId", productId);
        //intent.putExtra("productName", productNameString);
        //intent.putExtra("productImageId", productImageId);
        //intent.putExtra("productPrice", productPrice);
        //startActivity(intent);
      }
    });

    freeForFriends.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        Intent intent = new Intent(ProductDetailsActivity.this, ShopInviteFriendsActivity.class);
        intent.putExtra("productId", productId);
        startActivity(intent);
      }
    });

    switch (productId) {
      case 1:
        productPrice = 17;
        productImageId = R.drawable.shop_hat;
        productNameString = "Sugarman Cap";
        productName.setText(getResources().getString(R.string.sugarman_cap));
        //buyNowFor.setText("Buy now for 17$");
        freeForFriends.setText(String.format(getResources().getString(R.string.free_for),"5"));
        productImage.setImageDrawable(getResources().getDrawable(productImageId));
        break;
      case 2:
        productPrice = 12;
        productImageId = R.drawable.shop_case;
        productNameString = "Phone Holder";
        productName.setText(getResources().getString(R.string.phone_holder));
        //buyNowFor.setText("Buy now for 12$");
        freeForFriends.setText(String.format(getResources().getString(R.string.free_for),"5"));
        productImage.setImageDrawable(getResources().getDrawable(productImageId));
        break;
      case 3:
        productPrice = 3;
        productImageId = R.drawable.shop_comics;
        productNameString = "Sugarman Comics";
        productName.setText(getResources().getString(R.string.sugarman_comics));
        //buyNowFor.setText("Buy now for 3$");
        freeForFriends.setText("Free for 5 friends");
        productImage.setImageDrawable(getResources().getDrawable(productImageId));
        break;
    }
  }
}

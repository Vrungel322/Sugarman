package com.sugarman.myb.ui.activities.productDetail;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.mzelzoghbi.zgallery.CustomViewPager;
import com.sugarman.myb.R;
import com.sugarman.myb.base.BasicActivity;
import com.sugarman.myb.ui.activities.shopInviteFriend.ShopInviteFriendsActivity;
import java.util.List;

public class ProductDetailsActivity extends BasicActivity implements IProductDetailsActivityView {
  @InjectPresenter ProductDetailsActivityPresenter mPresenter;
  @BindView(R.id.viewPagerImages) CustomViewPager mViewPagerImages;
  @BindView(R.id.iv_back)ImageView backButton;
  @BindView(R.id.product_name)TextView productName;
  @BindView(R.id.buy_now_for_x)TextView buyNowFor;
  @BindView(R.id.free_for_x_friends) TextView freeForFriends;
  //@BindView(R.id.product_image)ImageView productImage;
  int productPrice = 0;
  int productImageId = 0;
  String productNameString = "";
  private ImageProductViewPagerAdapter mProductViewPagerAdapter;


  @Override protected void onCreate(Bundle savedInstanceState) {
    setContentView(R.layout.activity_product_details);
    super.onCreate(savedInstanceState);
    List<String> imgUrls = getIntent().getStringArrayListExtra("imgUrls");

    // pager adapter
    mProductViewPagerAdapter =
        new ImageProductViewPagerAdapter(this, imgUrls);
    mViewPagerImages.setAdapter(mProductViewPagerAdapter);


    backButton.setOnClickListener(view -> finish());
    Intent intent = getIntent();
    final int productId = intent.getIntExtra("productId", -1);

    buyNowFor.setOnClickListener(view -> {
      //Intent intent = new Intent(ProductDetailsActivity.this, CheckoutActivity.class);
      //intent.putExtra("checkout", 1);
      //intent.putExtra("productId", productId);
      //intent.putExtra("productName", productNameString);
      //intent.putExtra("productImageId", productImageId);
      //intent.putExtra("productPrice", productPrice);
      //startActivity(intent);
    });

    freeForFriends.setOnClickListener(view -> {
      Intent intent1 = new Intent(ProductDetailsActivity.this, ShopInviteFriendsActivity.class);
      intent1.putExtra("productId", productId);
      startActivity(intent1);
    });

    switch (productId) {
      case 1:
        productPrice = 17;
        productImageId = R.drawable.shop_hat;
        productNameString = "Sugarman Cap";
        productName.setText(getResources().getString(R.string.sugarman_cap));
        //buyNowFor.setText("Buy now for 17$");
        freeForFriends.setText(String.format(getResources().getString(R.string.free_for),"5"));
        //productImage.setImageDrawable(getResources().getDrawable(productImageId));
        break;
      case 2:
        productPrice = 12;
        productImageId = R.drawable.shop_case;
        productNameString = "Phone Holder";
        productName.setText(getResources().getString(R.string.phone_holder));
        //buyNowFor.setText("Buy now for 12$");
        freeForFriends.setText(String.format(getResources().getString(R.string.free_for),"5"));
        //productImage.setImageDrawable(getResources().getDrawable(productImageId));
        break;
      case 3:
        productPrice = 3;
        productImageId = R.drawable.shop_comics;
        productNameString = "Sugarman Comics";
        productName.setText(getResources().getString(R.string.sugarman_comics));
        //buyNowFor.setText("Buy now for 3$");
        freeForFriends.setText("Free for 5 friends");
        //productImage.setImageDrawable(getResources().getDrawable(productImageId));
        break;
    }
  }
}
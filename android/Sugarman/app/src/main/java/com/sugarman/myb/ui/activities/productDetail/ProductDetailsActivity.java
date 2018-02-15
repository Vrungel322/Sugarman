package com.sugarman.myb.ui.activities.productDetail;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.BindView;
import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AppsFlyerLib;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.mzelzoghbi.zgallery.CustomViewPager;
import com.sugarman.myb.App;
import com.sugarman.myb.R;
import com.sugarman.myb.api.models.responses.ShopProductEntity;
import com.sugarman.myb.base.BasicActivity;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.ui.activities.checkout.CheckoutActivity;
import com.sugarman.myb.ui.activities.shop.ShopActivity;
import com.sugarman.myb.ui.activities.shopInviteFriend.ShopInviteFriendsActivity;
import com.sugarman.myb.utils.apps_Fly.AppsFlyerEventSender;
import java.util.HashMap;
import java.util.Map;
import timber.log.Timber;

public class ProductDetailsActivity extends BasicActivity implements IProductDetailsActivityView {
  private static final int PURCHASE_FLOW_MONEY = 1100;
  private static final int PURCHASE_FLOW_FOR_FRIENDS = 1200;
  @InjectPresenter ProductDetailsActivityPresenter mPresenter;
  @BindView(R.id.viewPagerImages) CustomViewPager mViewPagerImages;
  @BindView(R.id.iv_back) ImageView backButton;
  @BindView(R.id.product_name) TextView productName;
  @BindView(R.id.buy_now_for_x) TextView buyNowFor;
  @BindView(R.id.free_for_x_friends) TextView freeForFriends;
  @BindView(R.id.pbProgress) ProgressBar mProgressBar;

  private ShopProductEntity mShopProductEntity;
  private ImageProductViewPagerAdapter mProductViewPagerAdapter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    setContentView(R.layout.activity_product_details);
    super.onCreate(savedInstanceState);

    AppsFlyerEventSender.sendEvent("af_product_details");

    mShopProductEntity = getIntent().getParcelableExtra(ShopActivity.PRODUCT);

    // pager adapter
    mProductViewPagerAdapter =
        new ImageProductViewPagerAdapter(this, mShopProductEntity.getImgDetailUrls());
    mViewPagerImages.setAdapter(mProductViewPagerAdapter);

    productName.setText(mShopProductEntity.getProductName());

    backButton.setOnClickListener(view -> finish());

    buyNowFor.setText(buyNowFor.getText() + " " + mShopProductEntity.getProductPrice() + "$");

    buyNowFor.setOnClickListener(view -> {
      Intent intent1 = new Intent(ProductDetailsActivity.this, CheckoutActivity.class);
      intent1.putExtra("checkout", 1);
      intent1.putExtra("productId", mShopProductEntity.getId());
      intent1.putExtra("productName", mShopProductEntity.getProductName());
      intent1.putExtra("productImageId", mShopProductEntity.getImgDetailUrls().get(0));
      intent1.putExtra("paymentType", Constants.PAY_PAL_PAYMENT_TYPE);
      Timber.e(mShopProductEntity.getProductPrice());
      intent1.putExtra("productPrice", mShopProductEntity.getProductPrice());
      startActivity(intent1);
    });

    freeForFriends.setOnClickListener(view -> {
      Intent intent1 = new Intent(ProductDetailsActivity.this, ShopInviteFriendsActivity.class);
      intent1.putExtra("productId", mShopProductEntity.getId());
      startActivityForResult(intent1, PURCHASE_FLOW_FOR_FRIENDS);
    });
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == PURCHASE_FLOW_FOR_FRIENDS) {
      mPresenter.checkNumberOfInviters();
    }
  }

  @Override public void startCheckoutActivityWithFreePrice() {
    Intent intent1 = new Intent(ProductDetailsActivity.this, CheckoutActivity.class);
    intent1.putExtra("checkout", 1);
    intent1.putExtra("productId", mShopProductEntity.getId());
    intent1.putExtra("productName", mShopProductEntity.getProductName());
    intent1.putExtra("productImageId", mShopProductEntity.getImgDetailUrls().get(0));
    intent1.putExtra("paymentType", Constants.FREE_PAYMENT_TYPE);
    Timber.e(mShopProductEntity.getProductPrice());
    intent1.putExtra("productPrice", "0");
    startActivity(intent1);
  }
}
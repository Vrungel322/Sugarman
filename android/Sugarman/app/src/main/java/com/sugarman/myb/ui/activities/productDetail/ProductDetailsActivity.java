package com.sugarman.myb.ui.activities.productDetail;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.mzelzoghbi.zgallery.CustomViewPager;
import com.sugarman.myb.R;
import com.sugarman.myb.api.models.responses.ShopProductEntity;
import com.sugarman.myb.base.BasicActivity;
import com.sugarman.myb.ui.activities.checkout.CheckoutActivity;
import com.sugarman.myb.ui.activities.shop.ShopActivity;
import com.sugarman.myb.ui.activities.shopInviteFriend.ShopInviteFriendsActivity;

public class ProductDetailsActivity extends BasicActivity implements IProductDetailsActivityView {
  @InjectPresenter ProductDetailsActivityPresenter mPresenter;
  @BindView(R.id.viewPagerImages) CustomViewPager mViewPagerImages;
  @BindView(R.id.iv_back) ImageView backButton;
  @BindView(R.id.product_name) TextView productName;
  @BindView(R.id.buy_now_for_x) TextView buyNowFor;
  @BindView(R.id.free_for_x_friends) TextView freeForFriends;
  private ImageProductViewPagerAdapter mProductViewPagerAdapter;
  private ShopProductEntity mShopProductEntity;

  @Override protected void onCreate(Bundle savedInstanceState) {
    setContentView(R.layout.activity_product_details);
    super.onCreate(savedInstanceState);
    mShopProductEntity = getIntent().getParcelableExtra(ShopActivity.PRODUCT);

    // pager adapter
    mProductViewPagerAdapter =
        new ImageProductViewPagerAdapter(this, mShopProductEntity.getImgDetailUrls());
    mViewPagerImages.setAdapter(mProductViewPagerAdapter);

    productName.setText(mShopProductEntity.getProductName());

    backButton.setOnClickListener(view -> finish());

    buyNowFor.setOnClickListener(view -> {
      Intent intent1 = new Intent(ProductDetailsActivity.this, CheckoutActivity.class);
      intent1.putExtra("checkout", 1);
      intent1.putExtra("productId", mShopProductEntity.getId());
      intent1.putExtra("productName", mShopProductEntity.getProductName());
      intent1.putExtra("productImageId", mShopProductEntity.getImgDetailUrls().get(0));
      intent1.putExtra("productPrice", mShopProductEntity.getProductPrice());
      startActivity(intent1);
    });

    freeForFriends.setOnClickListener(view -> {
      Intent intent1 = new Intent(ProductDetailsActivity.this, ShopInviteFriendsActivity.class);
      intent1.putExtra("productId", mShopProductEntity.getId());
      startActivity(intent1);
    });
  }
}
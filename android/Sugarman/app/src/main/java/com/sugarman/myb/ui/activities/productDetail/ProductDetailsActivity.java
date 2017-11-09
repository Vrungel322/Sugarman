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
import com.sugarman.myb.utils.inapp.IabHelper;
import com.sugarman.myb.utils.inapp.IabResult;
import com.sugarman.myb.utils.inapp.Inventory;
import com.sugarman.myb.utils.inapp.Purchase;
import timber.log.Timber;

public class ProductDetailsActivity extends BasicActivity implements IProductDetailsActivityView {
  //______________________________________________________________________
  static final String ITEM_SKU = "com.sugarman.myb.test_sub_1";
  @InjectPresenter ProductDetailsActivityPresenter mPresenter;
  @BindView(R.id.viewPagerImages) CustomViewPager mViewPagerImages;
  @BindView(R.id.iv_back) ImageView backButton;
  @BindView(R.id.product_name) TextView productName;
  @BindView(R.id.buy_now_for_x) TextView buyNowFor;
  @BindView(R.id.free_for_x_friends) TextView freeForFriends;
  IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
      new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {

          if (result.isSuccess()) {
          } else {
            // handle error
          }
        }
      };
  IabHelper mHelper;
  IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener =
      new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {

          if (result.isFailure()) {
            // Handle failure
          } else {
            mHelper.consumeAsync(inventory.getPurchase(ITEM_SKU), mConsumeFinishedListener);
          }
        }
      };
  IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener =
      new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
          if (result.isFailure()) {
            // Handle error
            return;
          } else if (purchase.getSku().equals(ITEM_SKU)) {
            consumeItem();
          }
        }
      };
  private ImageProductViewPagerAdapter mProductViewPagerAdapter;
  private ShopProductEntity mShopProductEntity;
  //______________________________________________________________________

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
      Timber.e(mShopProductEntity.getProductPrice());
      intent1.putExtra("productPrice", mShopProductEntity.getProductPrice());
      startActivity(intent1);
    });

    String base64EncodedPublicKey =
        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzz5RS18ubNQOXxntshbTr78JtHMuX4JfCJnZizT2YZGD1P/mvEAl1UXuZo3GVnob3RlAl+R9UkIKKoafb7YYL0Rz3cM7fJcfNZdsyYUFzrjwTQy77jtKzr6i+2mZEX14mIPjIvauAngx4cnQ2M35bkTfr+HyGB/kZtwxvGlosoTcPN3nvUH+FLKVVv1p8DkN6BVbmxrHl8NQXqYoFWNNjYHegYpfKBdrh/S89DyPVXx8G2ZcKMjmpq2CC/HiaXgGsL8NmQoBypbsgS7BlEL9Y4RAGjy4dEh1GhIBvD72aQ0TqKIM5ug8j3EY1Ge4uaKViKrgGSh3qyP6ITVZ/hXxxQIDAQAB";

    mHelper = new IabHelper(this, base64EncodedPublicKey);

    mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
      public void onIabSetupFinished(IabResult result) {
        if (!result.isSuccess()) {
          Timber.e("In-app Billing setup failed: " + result);
        } else {
          Timber.e("In-app Billing is set up OK");
        }
      }
    });
    mHelper.enableDebugLogging(true);


    freeForFriends.setOnClickListener(view -> {
      //Intent intent1 = new Intent(ProductDetailsActivity.this, ShopInviteFriendsActivity.class);
      //intent1.putExtra("productId", mShopProductEntity.getId());
      //startActivity(intent1);

      mHelper.launchSubscriptionPurchaseFlow(this, ITEM_SKU, 10001, mPurchaseFinishedListener,
          "mypurchasetoken");
    });
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
      super.onActivityResult(requestCode, resultCode, data);
    }
  }

  public void consumeItem() {
    mHelper.queryInventoryAsync(mReceivedInventoryListener);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    if (mHelper != null) mHelper.dispose();
    mHelper = null;
  }
}
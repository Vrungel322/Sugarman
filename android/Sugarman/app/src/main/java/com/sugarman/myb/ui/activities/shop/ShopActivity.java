package com.sugarman.myb.ui.activities.shop;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.sugarman.myb.R;
import com.sugarman.myb.api.models.responses.ShopProductEntity;
import com.sugarman.myb.base.BasicActivity;
import com.sugarman.myb.ui.activities.productDetail.ProductDetailsActivity;
import com.sugarman.myb.utils.ItemClickSupport;
import java.util.ArrayList;
import java.util.List;

public class ShopActivity extends BasicActivity implements IShopActivityView, View.OnClickListener {
  public static final String PRODUCT = "PRODUCT";
  @InjectPresenter ShopActivityPresenter mPresenter;
  @BindView(R.id.rvProducts) RecyclerView mRecyclerViewProducts;
  @BindView(R.id.mainLayout) ConstraintLayout mainLayout;
  @BindView(R.id.iv_back) ImageView backButton;
  List<TextView> allTexts;
  List<LinearLayout> containers;
  Typeface tfDin;

  private ProductsAdapter mProductsAdapter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    setContentView(R.layout.activity_shop);
    super.onCreate(savedInstanceState);
    tfDin = Typeface.createFromAsset(getAssets(), "din_medium.ttf");
    allTexts = new ArrayList<>();
    containers = new ArrayList<>();

    mProductsAdapter = new ProductsAdapter();
    mRecyclerViewProducts.setAdapter(mProductsAdapter);
    mRecyclerViewProducts.setLayoutManager(new GridLayoutManager(this,2));

    ItemClickSupport.addTo(mRecyclerViewProducts).setOnItemClickListener((recyclerView, position, v) -> {
      Intent intent = new Intent(ShopActivity.this, ProductDetailsActivity.class);
      intent.putExtra(PRODUCT, mProductsAdapter.getItem(position));
      startActivity(intent);
    });


    backButton.setOnClickListener(this);

    allTexts = getTextViews(mainLayout);

    for (TextView v : allTexts)
      v.setTypeface(tfDin);
  }

  @Override public void onClick(View view) {
    int id = view.getId();
    switch (id) {
      case R.id.iv_back:
        closeActivity();
        break;
    }
    return;
  }

  private ArrayList<TextView> getTextViews(ViewGroup root) {
    ArrayList<TextView> views = new ArrayList<>();
    for (int i = 0; i < root.getChildCount(); i++) {
      View v = root.getChildAt(i);
      if (v instanceof TextView) {
        views.add((TextView) v);
      } else if (v instanceof ViewGroup) {
        views.addAll(getTextViews((ViewGroup) v));
      }
    }
    return views;
  }

  private void closeActivity() {
    finish();
  }

  @Override public void showProducts(List<ShopProductEntity> shopProductEntities) {
    mProductsAdapter.addListShopProductEntity(shopProductEntities);
  }
}

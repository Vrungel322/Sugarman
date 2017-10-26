package com.sugarman.myb.ui.activities.shop;

import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.sugarman.myb.R;
import com.sugarman.myb.api.models.responses.ShopProductEntity;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nikita on 10.10.2017.
 */

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder> {
  private ArrayList<ShopProductEntity> mShopProductEntities = new ArrayList<>();

  public void addListShopProductEntity(List<ShopProductEntity> shopProductEntities) {
    mShopProductEntities.clear();
    mShopProductEntities.addAll(shopProductEntities);
    notifyDataSetChanged();
  }

  @Override public ProductsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop_product, parent, false);
    return new ProductsViewHolder(v);
  }

  @Override public void onBindViewHolder(ProductsViewHolder holder, int position) {
    Glide.with(holder.mImageViewThumb.getContext())
        .load(Integer.parseInt(mShopProductEntities.get(position).getImgDetailUrls().get(0)))
        .placeholder(AppCompatResources.getDrawable(holder.mImageViewThumb.getContext(),
            R.drawable.placeholder))
        .dontAnimate()
        .into(holder.mImageViewThumb);

    holder.mTextViewTitle.setText(mShopProductEntities.get(position).getProductName());
  }

  @Override public int getItemCount() {
    return mShopProductEntities.size();
  }

  public ShopProductEntity getItem(int position) {
    return mShopProductEntities.get(position);
  }

  static class ProductsViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.product_thumb) ImageView mImageViewThumb;
    @BindView(R.id.product_title) TextView mTextViewTitle;

    ProductsViewHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
    }
  }
}


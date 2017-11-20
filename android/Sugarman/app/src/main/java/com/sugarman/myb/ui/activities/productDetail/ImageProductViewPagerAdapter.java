package com.sugarman.myb.ui.activities.productDetail;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.CustomPicasso;
import com.sugarman.myb.R;
import java.util.List;

/**
 * Created by nikita on 10.10.2017.
 */

public class ImageProductViewPagerAdapter extends PagerAdapter {
  private Context mContext;
  private List<String> urls;
  private LayoutInflater mLayoutInflater;

  public ImageProductViewPagerAdapter(Context context, List<String> imgUrls) {
    this.mContext = context;

    urls = imgUrls;
    mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  }

  @Override public int getCount() {
    return urls.size();
  }

  @Override public Object instantiateItem(ViewGroup container, int position) {
    View itemView = mLayoutInflater.inflate(R.layout.item_page, container, false);
    final ImageView imageView = (ImageView) itemView.findViewById(R.id.iv);
    CustomPicasso.with(mContext)
        .load(Integer.parseInt(urls.get(position)))
        .fit()
        .centerCrop()
        .into(imageView);
    container.addView(itemView);
    return itemView;
  }

  @Override public void destroyItem(ViewGroup container, int position, Object object) {
    container.removeView((CardView) object);
  }

  @Override public boolean isViewFromObject(View view, Object object) {
    return view == object;
  }
}

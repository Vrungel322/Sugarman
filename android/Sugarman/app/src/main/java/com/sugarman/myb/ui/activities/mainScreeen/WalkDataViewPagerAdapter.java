package com.sugarman.myb.ui.activities.mainScreeen;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sugarman.myb.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nikita on 03.11.2017.
 */

public class WalkDataViewPagerAdapter extends PagerAdapter {
  private Context mContext;
  private List<String> mData = new ArrayList<>();
  private LayoutInflater mLayoutInflater;

  public WalkDataViewPagerAdapter(Context context) {
    this.mContext = context;
    mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  }

  public void setWalkData(List<String> data){
    this.mData.clear();
    this.mData = data;
    notifyDataSetChanged();
  }

  @Override public int getCount() {
    return mData.size();
  }

  @Override public Object instantiateItem(ViewGroup container, int position) {
    View itemView = mLayoutInflater.inflate(R.layout.item_walk_data, container, false);
    final TextView tvData1 = (TextView) itemView.findViewById(R.id.tvData1);
    final TextView tvData2 = (TextView) itemView.findViewById(R.id.tvData2);
    tvData1.setText(mData.get(position));

    container.addView(itemView);
    return itemView;
  }

  @Override public void destroyItem(ViewGroup container, int position, Object object) {
    container.removeView((ConstraintLayout) object);
  }

  @Override public boolean isViewFromObject(View view, Object object) {
    return view == object;
  }
}

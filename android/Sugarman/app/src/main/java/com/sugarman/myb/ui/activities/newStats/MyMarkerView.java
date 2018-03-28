package com.sugarman.myb.ui.activities.newStats;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.widget.ImageView;
import android.widget.TextView;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.sugarman.myb.R;

/**
 * Created by nikita on 28.03.2018.
 */

public class MyMarkerView extends MarkerView {

  private TextView tvContent;
  private ImageView iv;

  public MyMarkerView(Context context, int layoutResource) {
    super(context, layoutResource);

    tvContent = (TextView) findViewById(R.id.tvContent);
    iv = (ImageView) findViewById(R.id.iv);
  }

  // callbacks everytime the MarkerView is redrawn, can be used to update the
  // content (user-interface)
  @Override
  public void refreshContent(Entry e, Highlight highlight) {

    if (e instanceof CandleEntry) {

      CandleEntry ce = (CandleEntry) e;

      tvContent.setText("" + Utils.formatNumber(ce.getHigh(), 0, true));
    } else {

      tvContent.setText("" + Utils.formatNumber(e.getY(), 0, true));
    }



    super.refreshContent(e, highlight);
    iv.setBackgroundResource(R.drawable.animation_fly);
    AnimationDrawable animationDrawable = (AnimationDrawable) iv.getBackground();
    animationDrawable.start();
  }

  @Override
  public MPPointF getOffset() {
    return new MPPointF(-(getWidth() / 2), -getHeight());
  }
}

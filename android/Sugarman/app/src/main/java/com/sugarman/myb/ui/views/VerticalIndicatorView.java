package com.sugarman.myb.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.sugarman.myb.R;
import com.sugarman.myb.constants.Config;

public class VerticalIndicatorView extends View {

  private static final String TAG = VerticalIndicatorView.class.getName();

  private float maxValue = Config.MAX_STEPS_PER_DAY;
  private float currentValue = 0;

  private float indicatorWidth; //px

  private Paint redPaint;
  private Paint grayPaint;

  public VerticalIndicatorView(Context context) {
    super(context);
    init();
  }

  public VerticalIndicatorView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public VerticalIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  private VerticalIndicatorView(Context context, AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  @Override protected void onDraw(Canvas canvas) {
    //super.onDraw(canvas);

    if (!isInEditMode()) {
      drawIndicator(canvas);
    }
  }

  public void updateIndicator(int maxValue, int currentValue) {
    this.maxValue = maxValue;
    this.currentValue = currentValue > maxValue ? maxValue : currentValue;

    invalidate();
  }

  private void init() {
    if (!isInEditMode()) {
      Context context = getContext();

      setBackgroundColor(Color.TRANSPARENT);
      //setZOrderOnTop(true); //necessary
      //getHolder().setFormat(PixelFormat.TRANSPARENT);

      indicatorWidth = context.getResources().getDimensionPixelSize(R.dimen.stats_indicator_width);

      redPaint = new Paint();
      redPaint.setStyle(Paint.Style.FILL);
      redPaint.setColor(ContextCompat.getColor(context, R.color.red));

      grayPaint = new Paint();
      grayPaint.setStyle(Paint.Style.FILL);
      grayPaint.setColor(ContextCompat.getColor(context, R.color.gray));
    }
  }

  private void drawIndicator(Canvas canvas) {
    int height = getHeight();
    int width = getWidth();

    if (width <= 0 || height <= 0) {
      Log.d(TAG, "skip drawing. something wrong");
    } else {
      canvas.drawColor(Color.TRANSPARENT); // clear

      float left = (width - indicatorWidth) / 2;
      float right = left + indicatorWidth;
      float topGray = 0f;
      float bottomGray = (maxValue - currentValue) * height / maxValue;

      canvas.drawRect(left, bottomGray, right, (float) height, redPaint);
      canvas.drawRect(left, topGray, right, bottomGray, grayPaint);
    }
  }
}

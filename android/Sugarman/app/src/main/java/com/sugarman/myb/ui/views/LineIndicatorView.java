package com.sugarman.myb.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.sugarman.myb.R;
import com.sugarman.myb.constants.Config;

public class LineIndicatorView extends View {

  private static final String TAG = LineIndicatorView.class.getName();

  private float maxValue = Config.MAX_STEPS_PER_DAY;
  private float currentValue = 0;
  private final int numberLinesIndicator = Config.NUMBER_STROKES_INDICATOR_LINE;

  private int linePadding; // px
  private int indicatorWidth; // px

  private Paint dividerPaint;
  private Paint indicatorPaint;
  private Drawable gradientDrawable;

  public LineIndicatorView(Context context) {
    super(context);
    init();
  }

  public LineIndicatorView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public LineIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  private LineIndicatorView(Context context, AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    if (!isInEditMode()) {
      drawIndicator(canvas);
    }
  }

  public void updateIndicator(int maxValue, int currentValue) {
    this.maxValue = maxValue;
    this.currentValue = currentValue > maxValue ? maxValue : currentValue;

    invalidate();
  }

  private void drawIndicator(Canvas canvas) {
    int height = getHeight();
    int width = getWidth();

    if (width <= 0 || height <= 0) {
      Log.d(TAG, "skip drawing. something wrong");
    } else {
      canvas.drawColor(Color.TRANSPARENT); // clear

      gradientDrawable.setBounds(0, linePadding, width, height - linePadding);
      gradientDrawable.draw(canvas);

      int countDividers = numberLinesIndicator - 1;
      int countAllLines = numberLinesIndicator + countDividers;
      float widthStroke = (float) width / (float) countAllLines;

      dividerPaint.setStrokeWidth(widthStroke);
      float[] dividers = new float[countDividers * 4]; // 4 coordinates for every line

      for (int i = 0; i < countDividers; i++) {
        int index = i * 4;
        float x = i * 2 * widthStroke + 1.5f * widthStroke;
        dividers[index] = x; // x0
        dividers[index + 1] = 0; // y0
        dividers[index + 2] = x; // x1
        dividers[index + 3] = height; // y1
      }

      canvas.drawLines(dividers, dividerPaint);

      int position = (int) (countDividers * currentValue / maxValue);

      float x0;
      int index = position * 4;
      if (position >= countDividers) {
        index = (position - 1) * 4;
        x0 = dividers[index] + widthStroke;
      } else {
        x0 = dividers[index] - widthStroke;
      }
      indicatorPaint.setStrokeWidth(widthStroke - 2);
      canvas.drawLine(x0, 0, x0, height, indicatorPaint);

        /*    gradientDrawable.setBounds(0, 0, width, height); ////////for custom width stroke
            gradientDrawable.draw(canvas);

            int countAllLines = width / linePadding;
            int countDividers = countAllLines / 2;

            int sidesOffset = (width % linePadding) / 2;

            if (countAllLines % 2 == 0 && sidesOffset == 0) {
                sidesOffset += linePadding / 2;
                countDividers--;
            } else if (countAllLines % 2 == 0 && sidesOffset != 0) {
                sidesOffset += linePadding / 2;
                countDividers--;
            }

            dividerPaint.setStrokeWidth(linePadding);
            float[] dividers = new float[countDividers * 4]; // 4 coordinates for every line

            for (int i = 0; i < countDividers; i++) {
                int index = i * 4;
                float x = sidesOffset + i * 2 * linePadding + linePadding + linePadding / 2;
                dividers[index] = x; // x0
                dividers[index + 1] = 0; // y0
                dividers[index + 2] = x; // x1
                dividers[index + 3] = height; // y1
            }

            canvas.drawLines(dividers, dividerPaint);*/
    }
  }

  private void init() {
    if (!isInEditMode()) {
      Context context = getContext();

      setBackgroundColor(Color.TRANSPARENT);
      //setZOrderOnTop(true); //necessary
      //getHolder().setFormat(PixelFormat.TRANSPARENT);

      indicatorWidth =
          context.getResources().getDimensionPixelSize(R.dimen.challenge_indicator_width);

      dividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
      dividerPaint.setStyle(Paint.Style.STROKE);
      dividerPaint.setColor(ContextCompat.getColor(context, android.R.color.white));

      indicatorPaint = new Paint();
      indicatorPaint.setStyle(Paint.Style.STROKE);
      indicatorPaint.setStrokeWidth(indicatorWidth);
      indicatorPaint.setColor(ContextCompat.getColor(context, R.color.dark_gray));

      gradientDrawable = ContextCompat.getDrawable(context, R.drawable.red_gray_gradient);

      linePadding =
          getResources().getDimensionPixelSize(R.dimen.challenge_gradient_vertical_padding);
    }
  }
}

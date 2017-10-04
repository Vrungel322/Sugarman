package com.sugarman.myb.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.sugarman.myb.R;
import com.sugarman.myb.constants.Config;

public class CircleIndicatorView extends View {

  private static final String TAG = CircleIndicatorView.class.getName();

  private float maxValue = Config.MAX_STEPS_PER_DAY;
  private float currentValue = 0;

  private int indicatorStrokeLength;
  private int indicatorStrokeWidth;

  private Paint completedPaint;
  private Paint leftPain;
  private Paint completedWhitePaint;
  private Paint cursorPaint;
  private Path pathCursor;

  public CircleIndicatorView(Context context) {
    super(context);
    init();
  }

  public CircleIndicatorView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public CircleIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  private CircleIndicatorView(Context context, AttributeSet attrs, int defStyleAttr,
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

  public void addValue(int addValue) {
    currentValue += addValue;
    currentValue = currentValue > maxValue ? maxValue : currentValue;

    invalidate();
  }

  private void init() {
    if (!isInEditMode()) {
      setBackgroundColor(Color.TRANSPARENT);
      //setZOrderOnTop(true); //necessary
      //getHolder().setFormat(PixelFormat.TRANSPARENT);

      Context context = getContext();

      indicatorStrokeLength =
          getResources().getDimensionPixelSize(R.dimen.main_circle_indicator_stroke);
      indicatorStrokeWidth =
          getResources().getDimensionPixelSize(R.dimen.main_circle_indicator_stroke_width);

      completedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
      completedPaint.setStyle(Paint.Style.STROKE);
      completedPaint.setStrokeWidth(indicatorStrokeWidth);
      completedPaint.setColor(ContextCompat.getColor(context, R.color.red));
      completedPaint.setStrokeCap(Paint.Cap.ROUND);

      leftPain = new Paint(Paint.ANTI_ALIAS_FLAG);
      leftPain.setStyle(Paint.Style.STROKE);
      leftPain.setStrokeWidth(indicatorStrokeWidth);
      leftPain.setColor(ContextCompat.getColor(context, R.color.gray));
      leftPain.setStrokeCap(Paint.Cap.ROUND);

      completedWhitePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
      completedWhitePaint.setStyle(Paint.Style.STROKE);
      completedWhitePaint.setStrokeWidth(indicatorStrokeWidth);
      completedWhitePaint.setColor(Color.WHITE);
      completedWhitePaint.setStrokeCap(Paint.Cap.ROUND);

      cursorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
      cursorPaint.setStyle(Paint.Style.STROKE);
      cursorPaint.setStyle(Paint.Style.FILL);
      cursorPaint.setStrokeWidth(2);
      cursorPaint.setColor(ContextCompat.getColor(context, R.color.red));

      pathCursor = new Path();
    }
  }

  private void drawIndicator(Canvas canvas) {
    int width = getWidth();
    int height = getHeight();

    int externalRadius = width < height ? width : height;
    externalRadius = (externalRadius / 2) - indicatorStrokeWidth - 10;
    int internalRadius = externalRadius - indicatorStrokeLength;

    if (width <= 0 || height <= 0 || completedPaint == null || leftPain == null) {
      Log.d(TAG, "skip drawing. something wrong");
    } else {
      float centerX = width / 2;
      float centerY = height / 2;
      int countIndicators = 360 / Config.MAIN_INDICATOR_DEGREE_PERIOD;
      int completed = (int) (countIndicators * currentValue / maxValue);
      int left = countIndicators - completed;

      float[] linesCompeted = new float[completed * 4]; // 4 coordinates for every line
      float[] linesLeft = new float[left * 4]; // 4 coordinates for every line
      float[] linesAll = new float[countIndicators * 4];
      for (int i = 0; i < completed; i++) {
        double radians = i * Config.MAIN_INDICATOR_DEGREE_PERIOD * Math.PI / 180
            - Math.PI / 2;// radians with shift for start with north
        double sin = Math.sin(radians);
        double cos = Math.cos(radians);

        int index = i * 4;
        linesCompeted[index] = (float) ((internalRadius * cos) + centerX); // x0
        linesCompeted[index + 1] = (float) ((internalRadius * sin) + centerY); // y0
        linesCompeted[index + 2] = (float) ((externalRadius * cos) + centerX); // x1
        linesCompeted[index + 3] = (float) ((externalRadius * sin) + centerY); // y1
      }

      for (int i = 0; i < left; i++) {
        double radians = (i + completed) * Config.MAIN_INDICATOR_DEGREE_PERIOD * Math.PI / 180
            - Math.PI / 2; // radians with shift for start with north and shift from completed
        double sin = Math.sin(radians);
        double cos = Math.cos(radians);

        int index = i * 4;
        linesLeft[index] = (float) ((internalRadius * cos) + centerX); // x0
        linesLeft[index + 1] = (float) ((internalRadius * sin) + centerY); // y0
        linesLeft[index + 2] = (float) ((externalRadius * cos) + centerX); // x1
        linesLeft[index + 3] = (float) ((externalRadius * sin) + centerY); // y1
      }

      canvas.drawLines(linesCompeted, completedPaint);
      canvas.drawLines(linesLeft, leftPain);

      System.arraycopy(linesCompeted, 0, linesAll, 0, linesCompeted.length);
      System.arraycopy(linesLeft, 0, linesAll, linesCompeted.length, linesLeft.length);

      pathCursor = new Path();

      int indexLeftPin;
      int indexCenterPin;
      int indexRightPin;

      if (completed == 0 || completed == 1 || completed >= countIndicators) {
        indexLeftPin = countIndicators;
        indexCenterPin = 1;
        indexRightPin = 2;
      } else {
        indexLeftPin = completed - 1;
        indexCenterPin = completed;
        indexRightPin = completed + 1;
      }

      double halfWidthStroke = indicatorStrokeWidth / 2;
      double offset = indicatorStrokeLength / 3;

      double radians = (indexLeftPin) * Config.MAIN_INDICATOR_DEGREE_PERIOD * Math.PI / 180
          - Math.PI / 2; // radians with shift for start with north and shift from completed
      double sin = Math.sin(radians);
      double cos = Math.cos(radians);

      pathCursor.reset();

      float curXHexagon =
          (float) (linesAll[(indexLeftPin) * 4 - 4] - cos * offset + sin * halfWidthStroke);
      float curYHexagon =
          (float) (linesAll[(indexLeftPin) * 4 - 3] - sin * offset - cos * halfWidthStroke);

      pathCursor.moveTo(curXHexagon, curYHexagon);

      curXHexagon =
          (float) (linesAll[(indexLeftPin) * 4 - 2] + cos * offset + sin * halfWidthStroke);
      curYHexagon =
          (float) (linesAll[(indexLeftPin) * 4 - 1] + sin * offset - cos * halfWidthStroke);

      pathCursor.lineTo(curXHexagon, curYHexagon);

      radians = (completed) * Config.MAIN_INDICATOR_DEGREE_PERIOD * Math.PI / 180 - Math.PI / 2;
      sin = Math.sin(radians);
      cos = Math.cos(radians);

      curXHexagon =
          (float) (linesAll[indexCenterPin * 4 - 2] + cos * indicatorStrokeLength * 3 / 4);
      curYHexagon =
          (float) (linesAll[indexCenterPin * 4 - 1] + sin * indicatorStrokeLength * 3 / 4);

      pathCursor.lineTo(curXHexagon, curYHexagon);

      radians = (indexRightPin) * Config.MAIN_INDICATOR_DEGREE_PERIOD * Math.PI / 180
          - Math.PI / 2; // radians with shift for start with north and shift from completed
      sin = Math.sin(radians);
      cos = Math.cos(radians);

      curXHexagon =
          (float) (linesAll[(indexRightPin) * 4 - 2] + cos * offset - sin * halfWidthStroke);
      curYHexagon =
          (float) (linesAll[(indexRightPin) * 4 - 1] + sin * offset + cos * halfWidthStroke);

      pathCursor.lineTo(curXHexagon, curYHexagon);

      curXHexagon =
          (float) (linesAll[(indexRightPin) * 4 - 4] - cos * offset - sin * halfWidthStroke);
      curYHexagon =
          (float) (linesAll[(indexRightPin) * 4 - 3] - sin * offset + cos * halfWidthStroke);

      pathCursor.lineTo(curXHexagon, curYHexagon);

      radians = (indexCenterPin) * Config.MAIN_INDICATOR_DEGREE_PERIOD * Math.PI / 180
          - Math.PI / 2; // radians with shift for start with north and shift from completed
      sin = Math.sin(radians);
      cos = Math.cos(radians);

      curXHexagon =
          (float) (linesAll[indexCenterPin * 4 - 4] - cos * indicatorStrokeLength * 3 / 4);
      curYHexagon =
          (float) (linesAll[indexCenterPin * 4 - 3] - sin * indicatorStrokeLength * 3 / 4);

      pathCursor.lineTo(curXHexagon, curYHexagon);

      pathCursor.close();
      canvas.drawPath(pathCursor, cursorPaint);

      canvas.drawLine(linesAll[indexCenterPin * 4 - 4], linesAll[indexCenterPin * 4 - 3],
          linesAll[indexCenterPin * 4 - 2], linesAll[indexCenterPin * 4 - 1], completedWhitePaint);
    }
  }
}

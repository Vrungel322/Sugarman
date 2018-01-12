package com.sugarman.myb.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import timber.log.Timber;

public class ImageToDraw {
  private Paint mMaskingPaint = new Paint();
  private Context context;
  private Bitmap mResult;

  public ImageToDraw(Context context) {
    this.context = context;
  }

  public Bitmap transform(Bitmap source, float angle) {
    int width = source.getWidth();
    int height = source.getHeight();

    float strokeWidth = height * 0.03457814f / 2;
    float padding = height * 0.131396957f;
    float startX = width / 2;
    float startY = height / 2;

    float x = startX + (float) (Math.cos(Math.toRadians(angle) - Math.PI / 2)) * width / 2
        - (float) (Math.cos(Math.toRadians(angle) - Math.PI / 2)) * strokeWidth
        - (float) (Math.cos(Math.toRadians(angle) - Math.PI / 2)) * padding;
    float y = startY + (float) (Math.sin(Math.toRadians(angle) - Math.PI / 2)) * height / 2
        - (float) (Math.sin(Math.toRadians(angle) - Math.PI / 2)) * strokeWidth
        - (float) (Math.sin(Math.toRadians(angle) - Math.PI / 2)) * padding;

    Timber.e("w; h " + width + " " + height);

    // clear Bitmap
    if (mResult != null) {
      mResult.recycle();
      mResult = null;
    }

    mResult = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

    Canvas canvas = new Canvas(mResult);
    RectF mBigOval = new RectF(padding, padding, width - padding, height - padding);
    Paint p = new Paint();
    DashPathEffect dashPath = new DashPathEffect(new float[] { 5, 5 }, (float) 1.0);
    PathEffect path = new PathEffect();
    p.setPathEffect(path);
    p.setStyle(Paint.Style.FILL_AND_STROKE);
    mMaskingPaint.setAntiAlias(true);
    p.setAntiAlias(true);
    p.setColor(Color.GREEN);
    canvas.drawArc(mBigOval, -90, angle, true, p);

    //Log.e("Sin angle", "" + (angle * Math.PI / 180 - Math.PI / 2));
    //Log.e("Sin angle degr", "" + (angle - 90));
    //Log.e("coSin", "" + (float) (Math.cos(Math.toRadians(angle) - Math.PI / 2)));
    //Log.e("Sin", "" + (float) (Math.sin(Math.toRadians(angle) - Math.PI / 2)));
    //
    //Log.e("sin x", "" + x);
    //
    //Log.e("sin y", "" + y);
    //
    //Log.e("sin width", "" + width);

    if (angle > 5) {
      //p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

      canvas.drawCircle(x, y, strokeWidth + 1, p);
      canvas.drawCircle(startX, 0 + padding + strokeWidth + 1, strokeWidth, p);
    }

    mMaskingPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    canvas.drawBitmap(source, 0, 0, mMaskingPaint);
    return mResult;
  }
}

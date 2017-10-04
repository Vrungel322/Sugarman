package com.sugarman.myb.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import com.squareup.picasso.Transformation;
import com.sugarman.myb.R;

public class MaskTransformation implements Transformation {

  private static Paint mMaskingPaint = new Paint();
  private Context mContext;
  private int mMaskId;
  private boolean hasBorder;
  private int borderColor;

  static {
    mMaskingPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
  }

  /**
   * @param maskId If you change the mask file, please also rename the mask file, or Glide will get
   * the cache with the old mask. Because getId() return the same values if using the
   * same make file name. If you have a good idea please tell us, thanks.
   */
  public MaskTransformation(Context context, int maskId, boolean hasBorder, int borderColor) {
    mContext = context.getApplicationContext();
    mMaskId = maskId;
    this.hasBorder = hasBorder;
    this.borderColor = borderColor;
  }

  @Override public Bitmap transform(Bitmap source) {
    int width = source.getWidth();
    int height = source.getHeight();

    Paint borderPaint = new Paint();

    Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    Drawable drawable = mContext.getResources().getDrawable(R.drawable.hexagon_border);
    System.out.println("Drawable + " + drawable);

    Drawable mask = getMaskDrawable(mContext, mMaskId);

    Canvas canvas = new Canvas(result);
    mask.setBounds(0, 0, width, height);
    mask.draw(canvas);
    canvas.drawBitmap(source, 0, 0, mMaskingPaint);

    //canvas = new Canvas(bitmap);

    if (hasBorder) {
      drawable.setBounds(-5, -7, width + 5, height + 7);
      DrawableCompat.setTint(drawable, borderColor);
      drawable.draw(canvas);
    }
    //canvas.drawBitmap(bitmap,0,0,borderPaint);

    source.recycle();

    return result;
  }

  @Override public String key() {
    return "MaskTransformation(maskId="
        + mContext.getResources().getResourceEntryName(mMaskId)
        + ")";
  }

  public Drawable getMaskDrawable(Context context, int maskId) {
    Drawable drawable = ContextCompat.getDrawable(context, maskId);

    if (drawable == null) {
      throw new IllegalArgumentException("maskId is invalid");
    }

    return drawable;
  }
}
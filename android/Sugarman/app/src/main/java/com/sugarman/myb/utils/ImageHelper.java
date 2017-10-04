package com.sugarman.myb.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by Y500 on 05.07.2017.
 */

public class ImageHelper {

  public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
    Bitmap output = null;

    output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
    output = scaleCenterCrop(output,
        output.getHeight() < output.getWidth() ? output.getHeight() : output.getWidth(),
        output.getHeight() < output.getWidth() ? output.getHeight() : output.getWidth());
    Canvas canvas = new Canvas(output);

    final int color = 0xff424242;
    final Paint paint = new Paint();
    final Rect rect = new Rect(0, 0, output.getWidth(), output.getHeight());
    final RectF rectF = new RectF(rect);
    final float roundPx = pixels;

    paint.setAntiAlias(true);
    canvas.drawARGB(0, 0, 0, 0);
    paint.setColor(color);
    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    canvas.drawBitmap(bitmap, rect, rect, paint);
    canvas.save();

    return output;
  }

  public static Bitmap addBorderToRoundedBitmap(Bitmap srcBitmap, int cornerRadius, int borderWidth,
      int borderColor) {
    borderWidth = borderWidth;
    Bitmap dstBitmap =
        Bitmap.createBitmap(srcBitmap.getWidth() + borderWidth, srcBitmap.getHeight() + borderWidth,
            Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(dstBitmap);

    // Initialize a new Paint instance to draw border
    Paint paint1 = new Paint();
    paint1.setColor(borderColor);
    paint1.setStyle(Paint.Style.STROKE);
    paint1.setStrokeWidth(borderWidth);
    paint1.setAntiAlias(true);
    Rect rect1 = new Rect(borderWidth / 2, borderWidth / 2, dstBitmap.getWidth() - borderWidth / 2,
        dstBitmap.getHeight() - borderWidth / 2);
    RectF rectF1 = new RectF(rect1);
    canvas.drawRoundRect(rectF1, cornerRadius, cornerRadius, paint1);

    // Draw source bitmap to canvas
    canvas.drawBitmap(srcBitmap, borderWidth / 2, borderWidth / 2, null);
    srcBitmap.recycle();
    return dstBitmap;
  }

  public static Bitmap scaleCenterCrop(Bitmap source, int newHeight, int newWidth) {
    int sourceWidth = source.getWidth();
    int sourceHeight = source.getHeight();

    // Compute the scaling factors to fit the new height and width, respectively.
    // To cover the final image, the final scaling will be the bigger
    // of these two.
    float xScale = (float) newWidth / sourceWidth;
    float yScale = (float) newHeight / sourceHeight;
    float scale = Math.max(xScale, yScale);

    // Now get the size of the source bitmap when scaled
    float scaledWidth = scale * sourceWidth;
    float scaledHeight = scale * sourceHeight;

    // Let's find out the upper left coordinates if the scaled bitmap
    // should be centered in the new size give by the parameters
    float left = (newWidth - scaledWidth) / 2;
    float top = (newHeight - scaledHeight) / 2;

    // The target rectangle for the new, scaled version of the source bitmap will now
    // be
    RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

    // Finally, we create a new bitmap of the specified size and draw our new,
    // scaled bitmap onto it.
    Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
    Canvas canvas = new Canvas(dest);
    canvas.drawBitmap(source, null, targetRect, null);

    return dest;
  }
}

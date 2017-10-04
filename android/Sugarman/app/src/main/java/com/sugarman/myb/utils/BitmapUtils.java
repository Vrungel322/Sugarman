package com.sugarman.myb.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;
import java.io.IOException;

public abstract class BitmapUtils {

  private static final String TAG = BitmapUtils.class.getName();

  private BitmapUtils() {
    // only static methods and fields
  }

  public static Bitmap scaleBitmap(Bitmap image, int maxWidth, int maxHeight) {
    if (image != null && maxHeight > 0 && maxWidth > 0) {
      int width = image.getWidth();
      int height = image.getHeight();
      if (width == maxWidth && height == maxHeight) {
        return image;
      } else {
        float ratioBitmap = (float) width / (float) height;
        float ratioMax = (float) maxWidth / (float) maxHeight;

        int finalWidth = maxWidth;
        int finalHeight = maxHeight;
        if (ratioMax > 1) {
          finalWidth = (int) ((float) maxHeight * ratioBitmap);
        } else {
          finalHeight = (int) ((float) maxWidth / ratioBitmap);
        }
        image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
        return image;
      }
    } else {
      return image;
    }
  }

  public static Bitmap getRotatedBitmap(String filePath, Bitmap img) {
    try {
      ExifInterface ei = new ExifInterface(filePath);
      int orientation =
          ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
      switch (orientation) {
        case ExifInterface.ORIENTATION_ROTATE_90:
          return rotateImage(img, 90);
        case ExifInterface.ORIENTATION_ROTATE_180:
          return rotateImage(img, 180);
        case ExifInterface.ORIENTATION_ROTATE_270:
          return rotateImage(img, 270);
        default:
          return img;
      }
    } catch (IOException ex) {
      Log.e(TAG, "Failure get file metadata: " + filePath, ex);
    }

    return img;
  }

  private static Bitmap rotateImage(Bitmap img, int degree) {
    Matrix matrix = new Matrix();
    matrix.postRotate(degree);
    Bitmap rotatedImg =
        Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
    img.recycle();
    return rotatedImg;
  }
}

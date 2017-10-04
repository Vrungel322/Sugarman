package com.sugarman.myb.ui.views;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;

public class CustomFontSpan extends TypefaceSpan implements Parcelable {

  public static final Creator<CustomFontSpan> CREATOR = new Creator<CustomFontSpan>() {
    @Override public CustomFontSpan createFromParcel(Parcel in) {
      return new CustomFontSpan(in);
    }

    @Override public CustomFontSpan[] newArray(int size) {
      return new CustomFontSpan[size];
    }
  };

  private Typeface newType;

  public CustomFontSpan(String family, Typeface type) {
    super(family);
    newType = type;
  }

  private CustomFontSpan(Parcel in) {
    super(in);
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void updateDrawState(TextPaint ds) {
    applyCustomTypeFace(ds, newType);
  }

  @Override public void updateMeasureState(TextPaint paint) {
    applyCustomTypeFace(paint, newType);
  }

  private static void applyCustomTypeFace(Paint paint, Typeface tf) {
    int oldStyle;
    Typeface old = paint.getTypeface();
    if (old == null) {
      oldStyle = 0;
    } else {
      oldStyle = old.getStyle();
    }

    int fake = oldStyle & ~tf.getStyle();
    if ((fake & Typeface.BOLD) != 0) {
      paint.setFakeBoldText(true);
    }

    if ((fake & Typeface.ITALIC) != 0) {
      paint.setTextSkewX(-0.25f);
    }

    paint.setTypeface(tf);
  }
}

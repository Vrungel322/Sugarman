package com.sugarman.myb.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.annotation.StringRes;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;
import com.sugarman.myb.R;

public class CustomFontTextView extends AppCompatTextView {

  private static final String TAG = CustomFontTextView.class.getName();

  public CustomFontTextView(Context context) {
    this(context, null);
  }

  public CustomFontTextView(Context context, AttributeSet attrs) {
    this(context, attrs, android.R.attr.textViewStyle);
  }

  public CustomFontTextView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);

    if (!isInEditMode()) {
      setTypeface(attrs);
    }
  }

  private void setTypeface(AttributeSet attrs) {
    Context ctx = getContext();

    TypedArray array = ctx.obtainStyledAttributes(attrs, R.styleable.CustomFontTextView);
    String fontName = array.getString(R.styleable.CustomFontTextView_font);
    array.recycle();

    if (!TextUtils.isEmpty(fontName)) {
      setTypeface(fontName);
    }
  }

  public void setTypeface(@StringRes int fontName) {
    setTypeface(getResources().getString(fontName));
  }

  private void setTypeface(String typefaceName) {
    Context ctx = getContext();

    if (TextUtils.isEmpty(typefaceName)) {
      typefaceName = ctx.getString(R.string.font_roboto_regular);
    }

    Typeface font = TypefaceCache.get(typefaceName);
    if (font == null) {
      try {
        font = Typeface.createFromAsset(ctx.getAssets(), typefaceName);
      } catch (Exception e) {
        // Log.e(TAG, String.format("Error loading font: %s. Reverting to system default.", typefaceName), e);
        return;
      }
      TypefaceCache.put(typefaceName, font);
    }
    setTypeface(font);
  }
}

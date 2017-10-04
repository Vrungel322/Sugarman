package com.sugarman.myb.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;
import com.sugarman.myb.R;

public class CustomFontEditText extends EditText {

  private static final String TAG = CustomFontEditText.class.getName();

  public CustomFontEditText(Context context) {
    this(context, null);
  }

  public CustomFontEditText(Context context, AttributeSet attrs) {
    this(context, attrs, android.R.attr.editTextStyle);
  }

  public CustomFontEditText(Context context, AttributeSet attrs, int defStyle) {
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
    } else {
      setTypeface(getResources().getString(R.string.font_roboto_regular));
    }
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
        Log.e(TAG,
            String.format("Error loading font: %s. Reverting to system default.", typefaceName), e);
        return;
      }
      TypefaceCache.put(typefaceName, font);
    }
    setTypeface(font);
  }
}

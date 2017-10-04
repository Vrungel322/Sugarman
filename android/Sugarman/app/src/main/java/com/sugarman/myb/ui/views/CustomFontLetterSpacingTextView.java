package com.sugarman.myb.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ScaleXSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;
import com.sugarman.myb.R;

public class CustomFontLetterSpacingTextView extends TextView {

  private final String TAG = CustomFontLetterSpacingTextView.class.getSimpleName();

  /**
   * Default kerning values which can be used for convenience
   */
  public class Kerning {
    public final static float NO_KERNING = 0;
    public final static float SMALL = 1;
    public final static float MEDIUM = 4;
    public final static float LARGE = 6;
  }

  private float mKerningFactor = Kerning.NO_KERNING;
  private CharSequence mOriginalText;

  public CustomFontLetterSpacingTextView(Context context) {
    super(context);
    init(null, 0);
  }

  public CustomFontLetterSpacingTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(attrs, 0);
  }

  public CustomFontLetterSpacingTextView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init(attrs, 0);
  }

  private void init(AttributeSet attrs, int defStyle) {
    Context context = getContext();
    TypedArray originalTypedArray =
        context.obtainStyledAttributes(attrs, new int[] { android.R.attr.text });
    TypedArray currentTypedArray =
        context.obtainStyledAttributes(attrs, R.styleable.CustomFontLetterSpacingTextView, 0,
            defStyle);

    try {
      mKerningFactor =
          currentTypedArray.getFloat(R.styleable.CustomFontLetterSpacingTextView_spacing,
              Kerning.NO_KERNING);
      mOriginalText = originalTypedArray.getText(0);
    } finally {
      originalTypedArray.recycle();
      currentTypedArray.recycle();
    }

    TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CustomFontTextView);
    String fontName = array.getString(R.styleable.CustomFontTextView_font);
    array.recycle();

    if (!TextUtils.isEmpty(fontName)) {
      setTypeface(fontName);
    } else {
      setTypeface(getResources().getString(R.string.font_roboto_regular));
    }

    applyKerning();
  }

  /**
   * Programatically get the value of the {@code mKerningFactor}
   */
  public float getKerningFactor() {
    return mKerningFactor;
  }

  /**
   * Programatically set the value of the {@code mKerningFactor}
   */
  public void setKerningFactor(float kerningFactor) {
    this.mKerningFactor = kerningFactor;
    applyKerning();
  }

  @Override public void setText(CharSequence text, BufferType type) {
    mOriginalText = text;
    applyKerning();
  }

  @Override public CharSequence getText() {
    return mOriginalText;
  }

  /**
   * The algorithm which applies the kerning to the {@link TextView}
   */
  private void applyKerning() {
    if (mOriginalText == null) {
      return;
    }

    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < mOriginalText.length(); i++) {
      builder.append(mOriginalText.charAt(i));
      if (i + 1 < mOriginalText.length()) {
        builder.append("\u00A0");
      }
    }

    SpannableString finalText = new SpannableString(builder.toString());
    if (builder.toString().length() > 1) {
      for (int i = 1; i < builder.toString().length(); i += 2) {
        finalText.setSpan(new ScaleXSpan((mKerningFactor) / 10), i, i + 1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
      }
    }
    super.setText(finalText, BufferType.SPANNABLE);
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
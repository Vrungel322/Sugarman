package com.sugarman.myb.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.AnyRes;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.sugarman.myb.R;

public class StrokeImage extends LinearLayout {

  private ImageView imageView;

  public StrokeImage(Context context) {
    this(context, null);
  }

  public StrokeImage(Context context, AttributeSet attrs) {
    this(context, attrs, R.attr.squareImageStyle);
  }

  public StrokeImage(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr);
  }

  private void init(Context context, AttributeSet attrs, int defStyleAttr) {
    if (!isInEditMode()) {
      imageView = new ImageView(context);
      LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.MATCH_PARENT);
      imageView.setLayoutParams(params);

      imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

      //Retrieve styles attributes
      TypedArray a =
          context.obtainStyledAttributes(attrs, R.styleable.StrokeImage, defStyleAttr, 0);

      Drawable src = a.getDrawable(R.styleable.StrokeImage_android_src);
      if (src != null) {
        imageView.setImageDrawable(src);
      }

      a.recycle();

      addView(imageView);
    }
  }

  public ImageView getImageView() {
    return imageView;
  }

  public void setImageResource(@AnyRes int resource) {
    imageView.setImageResource(resource);
  }
}

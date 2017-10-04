package com.sugarman.myb.ui.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import com.sugarman.myb.R;

public class SquarePagerIndicatorArrows extends View implements PageIndicator {

  private static final int INVALID_POINTER = -1;

  private final Paint mPaintUnselected = new Paint(Paint.ANTI_ALIAS_FLAG);
  private final Paint mPaintSelected = new Paint(Paint.ANTI_ALIAS_FLAG);
  private final Paint mPaintArrow = new Paint(Paint.ANTI_ALIAS_FLAG);
  private ViewPager mViewPager;
  private ViewPager.OnPageChangeListener mListener;
  private int mCurrentPage;
  private boolean mCentered;
  private float mLineWidth;
  private float mGapWidth;

  private int mTouchSlop;
  private float mLastMotionX = -1;
  private int mActivePointerId = INVALID_POINTER;
  private boolean mIsDragging;
  private int maxIndicatorCircles;

  public SquarePagerIndicatorArrows(Context context) {
    super(context);
    init(context);
  }

  public SquarePagerIndicatorArrows(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  public SquarePagerIndicatorArrows(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init(context);
  }

  private void init(Context context) {
    if (!isInEditMode()) {
      final Resources res = getResources();

      //Load defaults from resources
      final int defaultSelectedColor = ContextCompat.getColor(context, R.color.dark_gray);
      final int defaultUnselectedColor = ContextCompat.getColor(context, R.color.dark_red);
      mLineWidth = res.getDimension(R.dimen.stats_pager_indicator_size);
      mGapWidth = res.getDimension(R.dimen.stats_pager_indicator_gape);
      final float defaultStrokeWidth = res.getDimension(R.dimen.stats_pager_indicator_size);
      mCentered = true;

      setStrokeWidth(defaultStrokeWidth);
      mPaintUnselected.setColor(defaultUnselectedColor);
      mPaintSelected.setColor(defaultSelectedColor);
      mPaintArrow.setStrokeWidth(
          getResources().getDimensionPixelSize(R.dimen.main_size_pager_arrow_width));
      mPaintArrow.setColor(defaultUnselectedColor);
      mPaintArrow.setStyle(Paint.Style.STROKE);

      final ViewConfiguration configuration = ViewConfiguration.get(context);
      mTouchSlop = configuration.getScaledPagingTouchSlop();
    }
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    if (mViewPager == null) {
      return;
    }
    final int count = mViewPager.getAdapter().getCount();
    if (count == 0) {
      return;
    }

    if (mCurrentPage >= count) {
      setCurrentItem(count - 1);
      return;
    }
    final float indicatorWidth;
    if (maxIndicatorCircles >= count) {
      maxIndicatorCircles = 0;
    }
    final float lineWidthAndGap = mLineWidth + mGapWidth;
    if (maxIndicatorCircles != 0) {
      indicatorWidth = (maxIndicatorCircles * lineWidthAndGap) - mGapWidth;
    } else {
      indicatorWidth = (count * lineWidthAndGap) - mGapWidth;
    }
    final float paddingTop = getPaddingTop();
    final float paddingLeft = getPaddingLeft();
    final float paddingRight = getPaddingRight();

    float verticalOffset = paddingTop + ((getHeight() - paddingTop - getPaddingBottom()) / 2.0f);
    float horizontalOffset = paddingLeft;
    if (mCentered) {
      horizontalOffset +=
          ((getWidth() - paddingLeft - paddingRight) / 2.0f) - (indicatorWidth / 2.0f);
    }

    if (maxIndicatorCircles != 0) {
      for (int i = 0; i < maxIndicatorCircles; i++) {

        float dx1 = horizontalOffset + (i * lineWidthAndGap);
        float dx2 = dx1 + mLineWidth;
        float radius = (dx2 - dx1) / 2;
        float x = dx1 + radius;
        if (i == mCurrentPage) {
          canvas.drawCircle(x, verticalOffset, radius, mPaintSelected);
        } else {
          canvas.drawCircle(x, verticalOffset, radius, mPaintUnselected);
        }
        if (i == maxIndicatorCircles - 1 && mCurrentPage >= maxIndicatorCircles - 1) {
          canvas.drawCircle(x, verticalOffset, radius, mPaintSelected);
        }
      }

      if (mCurrentPage >= maxIndicatorCircles) {
        Path pathCursor = new Path();
        pathCursor.reset();
        pathCursor.moveTo(mLineWidth + mLineWidth / 2, verticalOffset - mLineWidth / 2);
        pathCursor.lineTo(mLineWidth, verticalOffset);
        pathCursor.lineTo(mLineWidth + mLineWidth / 2, verticalOffset + mLineWidth / 2);
        pathCursor.lineTo(mLineWidth, verticalOffset);
        pathCursor.close();
        canvas.drawPath(pathCursor, mPaintArrow);
      }
      if (mCurrentPage < count - 1) {
        Path pathCursor = new Path();
        pathCursor.reset();
        pathCursor.moveTo(canvas.getWidth() - mLineWidth - mLineWidth / 2,
            verticalOffset - mLineWidth / 2);
        pathCursor.lineTo(canvas.getWidth() - mLineWidth, verticalOffset);
        pathCursor.lineTo(canvas.getWidth() - mLineWidth - mLineWidth / 2,
            verticalOffset + mLineWidth / 2);
        pathCursor.lineTo(canvas.getWidth() - mLineWidth, verticalOffset);
        pathCursor.close();
        canvas.drawPath(pathCursor, mPaintArrow);
      }
    } else {
      for (int i = 0; i < count; i++) {

        float dx1 = horizontalOffset + (i * lineWidthAndGap);
        float dx2 = dx1 + mLineWidth;
        float radius = (dx2 - dx1) / 2;
        float x = dx1 + radius;
        if (i == mCurrentPage) {
          canvas.drawCircle(x, verticalOffset, radius, mPaintSelected);
        } else {
          canvas.drawCircle(x, verticalOffset, radius, mPaintUnselected);
        }
      }
    }
  }

  @Override public boolean onTouchEvent(android.view.MotionEvent ev) {
    if (super.onTouchEvent(ev)) {
      return true;
    }
    if ((mViewPager == null) || (mViewPager.getAdapter().getCount() == 0)) {
      return false;
    }

    final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;
    switch (action) {
      case MotionEvent.ACTION_DOWN:
        mActivePointerId = ev.getPointerId(0);
        mLastMotionX = ev.getX();
        break;

      case MotionEvent.ACTION_MOVE: {
        final int activePointerIndex = ev.findPointerIndex(mActivePointerId);
        final float x = ev.getX(activePointerIndex);
        final float deltaX = x - mLastMotionX;

        if (!mIsDragging) {
          if (Math.abs(deltaX) > mTouchSlop) {
            mIsDragging = true;
          }
        }

        if (mIsDragging) {
          mLastMotionX = x;
          if (mViewPager.isFakeDragging() || mViewPager.beginFakeDrag()) {
            mViewPager.fakeDragBy(deltaX);
          }
        }

        break;
      }

      case MotionEvent.ACTION_CANCEL:
      case MotionEvent.ACTION_UP:
        if (!mIsDragging) {
          final int count = mViewPager.getAdapter().getCount();
          final int width = getWidth();
          final float halfWidth = width / 2f;
          final float sixthWidth = width / 6f;

          if ((mCurrentPage > 0) && (ev.getX() < halfWidth - sixthWidth)) {
            if (action != MotionEvent.ACTION_CANCEL) {
              mViewPager.setCurrentItem(mCurrentPage - 1);
            }
            return true;
          } else if ((mCurrentPage < count - 1) && (ev.getX() > halfWidth + sixthWidth)) {
            if (action != MotionEvent.ACTION_CANCEL) {
              mViewPager.setCurrentItem(mCurrentPage + 1);
            }
            return true;
          }
        }

        mIsDragging = false;
        mActivePointerId = INVALID_POINTER;
        if (mViewPager.isFakeDragging()) mViewPager.endFakeDrag();
        break;

      case MotionEventCompat.ACTION_POINTER_DOWN: {
        final int index = MotionEventCompat.getActionIndex(ev);
        mLastMotionX = ev.getX(index);
        mActivePointerId = ev.getPointerId(index);
        break;
      }

      case MotionEventCompat.ACTION_POINTER_UP:
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
          final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
          mActivePointerId = ev.getPointerId(newPointerIndex);
        }
        mLastMotionX = ev.getX(ev.findPointerIndex(mActivePointerId));
        break;
    }

    return true;
  }

  @Override public void setViewPager(ViewPager viewPager) {
    if (mViewPager == viewPager) {
      return;
    }
    if (mViewPager != null) {
      //Clear us from the old pager.
      mViewPager.removeOnPageChangeListener(this);
    }
    if (viewPager.getAdapter() == null) {
      throw new IllegalStateException("ViewPager does not have adapter instance.");
    }
    mViewPager = viewPager;
    mViewPager.addOnPageChangeListener(this);
    invalidate();
  }

  @Override public void setViewPager(ViewPager view, int initialPosition) {
    setViewPager(view);
    setCurrentItem(initialPosition);
  }

  @Override public void setCurrentItem(int item) {
    if (mViewPager == null) {
      throw new IllegalStateException("ViewPager has not been bound.");
    }
    mViewPager.setCurrentItem(item);
    mCurrentPage = item;
    invalidate();
  }

  @Override public void notifyDataSetChanged() {
    invalidate();
  }

  @Override public void onPageScrollStateChanged(int state) {
    if (mListener != null) {
      mListener.onPageScrollStateChanged(state);
    }
  }

  @Override
  public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    if (mListener != null) {
      mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
    }
  }

  @Override public void onPageSelected(int position) {
    mCurrentPage = position;
    invalidate();

    if (mListener != null) {
      mListener.onPageSelected(position);
    }
  }

  @Override public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
    mListener = listener;
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
  }

  /**
   * Determines the width of this view
   *
   * @param measureSpec A measureSpec packed into an int
   * @return The width of the view, honoring constraints from measureSpec
   */
  private int measureWidth(int measureSpec) {
    float result;
    int specMode = MeasureSpec.getMode(measureSpec);
    int specSize = MeasureSpec.getSize(measureSpec);

    if ((specMode == MeasureSpec.EXACTLY) || (mViewPager == null)) {
      //We were told how big to be
      result = specSize;
    } else {
      //Calculate the width according the views count
      int count = mViewPager.getAdapter().getCount();
      if (maxIndicatorCircles != 0) {
        if (maxIndicatorCircles < count - 1) {
          count = maxIndicatorCircles + 2;
        } else {
          count = count + 2;
        }
      }
      result =
          getPaddingLeft() + getPaddingRight() + (count * mLineWidth) + ((count - 1) * mGapWidth);
      //Respect AT_MOST value if that was what is called for by measureSpec
      if (specMode == MeasureSpec.AT_MOST) {
        result = Math.min(result, specSize);
      }
    }
    return (int) Math.ceil(result);
  }

  /**
   * Determines the height of this view
   *
   * @param measureSpec A measureSpec packed into an int
   * @return The height of the view, honoring constraints from measureSpec
   */
  private int measureHeight(int measureSpec) {
    float result;
    int specMode = MeasureSpec.getMode(measureSpec);
    int specSize = MeasureSpec.getSize(measureSpec);

    if (specMode == MeasureSpec.EXACTLY) {
      //We were told how big to be
      result = specSize;
    } else {
      //Measure the height
      result = mPaintSelected.getStrokeWidth() + getPaddingTop() + getPaddingBottom();
      //Respect AT_MOST value if that was what is called for by measureSpec
      if (specMode == MeasureSpec.AT_MOST) {
        result = Math.min(result, specSize);
      }
    }
    return (int) Math.ceil(result);
  }

  @Override public void onRestoreInstanceState(Parcelable state) {
    SquarePageIndicator.SavedState savedState = (SquarePageIndicator.SavedState) state;
    super.onRestoreInstanceState(savedState.getSuperState());
    mCurrentPage = savedState.currentPage;
    requestLayout();
  }

  @Override public Parcelable onSaveInstanceState() {
    Parcelable superState = super.onSaveInstanceState();
    SquarePageIndicator.SavedState savedState = new SquarePageIndicator.SavedState(superState);
    savedState.currentPage = mCurrentPage;
    return savedState;
  }

  public void setMaxIndicatorCircles(int maxNumberCircles) {
    maxIndicatorCircles = maxNumberCircles;
  }

  public void setCentered(boolean centered) {
    mCentered = centered;
    invalidate();
  }

  public boolean isCentered() {
    return mCentered;
  }

  public void setUnselectedColor(int unselectedColor) {
    mPaintUnselected.setColor(unselectedColor);
    invalidate();
  }

  public int getUnselectedColor() {
    return mPaintUnselected.getColor();
  }

  public void setSelectedColor(int selectedColor) {
    mPaintSelected.setColor(selectedColor);
    invalidate();
  }

  public int getSelectedColor() {
    return mPaintSelected.getColor();
  }

  public void setLineWidth(float lineWidth) {
    mLineWidth = lineWidth;
    invalidate();
  }

  public float getLineWidth() {
    return mLineWidth;
  }

  private void setStrokeWidth(float lineHeight) {
    mPaintSelected.setStrokeWidth(lineHeight);
    mPaintUnselected.setStrokeWidth(lineHeight);
    invalidate();
  }

  public float getStrokeWidth() {
    return mPaintSelected.getStrokeWidth();
  }

  public void setGapWidth(float gapWidth) {
    mGapWidth = gapWidth;
    invalidate();
  }

  public float getGapWidth() {
    return mGapWidth;
  }

  static class SavedState extends BaseSavedState {
    int currentPage;

    public SavedState(Parcelable superState) {
      super(superState);
    }

    private SavedState(Parcel in) {
      super(in);
      currentPage = in.readInt();
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
      super.writeToParcel(dest, flags);
      dest.writeInt(currentPage);
    }

    @SuppressWarnings("UnusedDeclaration")
    public static final Parcelable.Creator<SquarePagerIndicatorArrows.SavedState> CREATOR =
        new Parcelable.Creator<SquarePagerIndicatorArrows.SavedState>() {
          @Override public SquarePagerIndicatorArrows.SavedState createFromParcel(Parcel in) {
            return new SquarePagerIndicatorArrows.SavedState(in);
          }

          @Override public SquarePagerIndicatorArrows.SavedState[] newArray(int size) {
            return new SquarePagerIndicatorArrows.SavedState[size];
          }
        };
  }
}
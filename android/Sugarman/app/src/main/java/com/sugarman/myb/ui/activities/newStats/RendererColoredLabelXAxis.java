package com.sugarman.myb.ui.activities.newStats;

import android.graphics.Canvas;
import android.graphics.Color;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import java.util.Collections;
import java.util.List;
import timber.log.Timber;

public class RendererColoredLabelXAxis extends XAxisRenderer {
  List<Integer> labelColors;

  public RendererColoredLabelXAxis(ViewPortHandler viewPortHandler, XAxis xAxis,
      Transformer trans) {
    super(viewPortHandler, xAxis, trans);
    labelColors = Collections.EMPTY_LIST;
  }

  public RendererColoredLabelXAxis(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans,
      List<Integer> colors) {
    super(viewPortHandler, xAxis, trans);
    this.labelColors = colors;
  }

  @Override protected void drawLabels(Canvas c, float pos, MPPointF anchor) {
    final float labelRotationAngleDegrees = mXAxis.getLabelRotationAngle();
    boolean centeringEnabled = mXAxis.isCenterAxisLabelsEnabled();

    float[] positions = new float[mXAxis.mEntryCount * 2];

    for (int i = 0; i < positions.length; i += 2) {

      // only fill x values
      if (centeringEnabled) {
        positions[i] = mXAxis.mCenteredEntries[i / 2];
      } else {
        positions[i] = mXAxis.mEntries[i / 2];
      }
    }

    mTrans.pointValuesToPixel(positions);

    for (int i = 0; i < positions.length; i += 2) {

      float x = positions[i];

      if (mViewPortHandler.isInBoundsX(x)) {

        String label = mXAxis.getValueFormatter().getFormattedValue(mXAxis.mEntries[i / 2], mXAxis);
        int color = getColorForXValue((int) mXAxis.mEntries[i / 2]); //added

        if (label.toLowerCase().contains("week")
            || label.toLowerCase().contains("неделя")
            || label.toLowerCase().contains("שָׁבוּעַ")) {
          mAxisLabelPaint.setColor(Color.BLACK);
        } else {
          mAxisLabelPaint.setColor(Color.RED);
        }

        Timber.e("drawLabels " + label);

        if (mXAxis.isAvoidFirstLastClippingEnabled()) {

          // avoid clipping of the last
          if (i == mXAxis.mEntryCount - 1 && mXAxis.mEntryCount > 1) {
            float width = Utils.calcTextWidth(mAxisLabelPaint, label);

            if (width > mViewPortHandler.offsetRight() * 2
                && x + width > mViewPortHandler.getChartWidth()) {
              x -= width / 2;
            }

            // avoid clipping of the first
          } else if (i == 0) {

            float width = Utils.calcTextWidth(mAxisLabelPaint, label);
            x += width / 2;
          }
        }

        drawLabel(c, label, x, pos, anchor, labelRotationAngleDegrees);
      }
    }
  }

  private int getColorForXValue(int index) {
    if (index >= labelColors.size()) return mXAxis.getTextColor();

    if (index < 0) return mXAxis.getTextColor();

    return labelColors.get(index);
  }
}

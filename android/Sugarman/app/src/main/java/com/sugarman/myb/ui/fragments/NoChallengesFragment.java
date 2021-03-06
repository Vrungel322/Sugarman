package com.sugarman.myb.ui.fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.sugarman.myb.R;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.ui.activities.mainScreeen.MainActivity;
import com.sugarman.myb.utils.IntentExtractorHelper;
import timber.log.Timber;

public class NoChallengesFragment extends BaseChallengeFragment implements View.OnClickListener {

  private RelativeLayout textContainer;
  ImageView background;
  View root;
  CardView cardViewNoChallenge;

  private static final String TAG = NoChallengesFragment.class.getName();
  private Bitmap mBmp;

  public static NoChallengesFragment newInstance(int position) {
    NoChallengesFragment fragment = new NoChallengesFragment();

    Bundle args = new Bundle();
    args.putInt(Constants.BUNDLE_TRACKING_POSITION, position);
    fragment.setArguments(args);

    return fragment;
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    root = inflater.inflate(R.layout.fragment_no_challenges, container, false);
    textContainer = (RelativeLayout) root.findViewById(R.id.text_container);
    background = (ImageView) root.findViewById(R.id.imageBack);
    cardViewNoChallenge = (CardView) root.findViewById(R.id.card_view_no_challenge);
    Log.e("CONTAINER", "" + container.getHeight());

    position = IntentExtractorHelper.getTrackingPosition(getArguments());

    //View ivCreateGroup = root.findViewById(R.id.iv_create_group);
    ViewTreeObserver vto;
    vto = textContainer.getViewTreeObserver();
    vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override public void onGlobalLayout() {
        textContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        int width = textContainer.getMeasuredWidth();
        int height = textContainer.getMeasuredHeight();
        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types

        if (width > 0 && height > 0) {
          // this creates a MUTABLE bitmap
          mBmp = Bitmap.createBitmap(width, height, conf);
          Canvas canvas = new Canvas(mBmp);
          int padding = height / 35 * 2;
          Paint p = new Paint();
          p.setStrokeWidth(height / 80);
          p.setStrokeCap(Paint.Cap.ROUND);
          p.setColor(0xffff0000);

          canvas.drawLine(padding, padding, padding + height / 10, padding, p);
          canvas.drawLine(padding, padding, padding, padding + height / 10, p);

          canvas.drawLine(width - padding, padding, width - padding - height / 10, padding, p);
          canvas.drawLine(width - padding, padding, width - padding, padding + height / 10, p);

          canvas.drawLine(padding, height - padding, padding + height / 10, height - padding, p);
          canvas.drawLine(padding, height - padding, padding, height - padding - height / 10, p);

          canvas.drawLine(width - padding, height - padding, width - padding - height / 10,
              height - padding, p);
          canvas.drawLine(width - padding, height - padding, width - padding,
              height - padding - height / 10, p);

          textContainer.setBackground(new BitmapDrawable(getActivity().getResources(), mBmp));
        }
      }
    });

    cardViewNoChallenge.setOnClickListener(this);

    return root;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    clearBitmap();
  }

  private void clearBitmap() {
    Timber.e("clearBitmap");
    if (mBmp != null) {
      Timber.e("clearBitmap mBmp != null");

      if (!mBmp.isRecycled()) {
        mBmp.recycle();
      }
      mBmp = null;
    }
  }

  @Override public void onClick(View v) {
    int id = v.getId();
    switch (id) {
      case R.id.card_view_no_challenge:
        openCreateGroupActivity();
        break;
      default:
        Log.d(TAG,
            "Click on not processed view with id " + getResources().getResourceEntryName(id));
        break;
    }
  }

  private void openCreateGroupActivity() {
    Activity activity = getActivity();
    if (activity != null
        && activity instanceof MainActivity
        && ((MainActivity) activity).isReady()) {
      ((MainActivity) activity).openCreateGroupActivity();
    }
  }
}

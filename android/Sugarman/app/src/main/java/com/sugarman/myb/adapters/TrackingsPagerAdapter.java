package com.sugarman.myb.adapters;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import com.sugarman.myb.api.models.responses.Tracking;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.models.BaseChallengeItem;
import com.sugarman.myb.models.ChallengeItem;
import com.sugarman.myb.models.ChallengeItemType;
import com.sugarman.myb.models.ChallengeWillStartItem;
import com.sugarman.myb.ui.fragments.BaseChallengeFragment;
import com.sugarman.myb.ui.fragments.BaseFragment;
import com.sugarman.myb.ui.fragments.NoChallengesFragment;
import com.sugarman.myb.ui.fragments.StartedChallengeFragment;
import com.sugarman.myb.ui.fragments.WillStartChallengeFragment;
import com.sugarman.myb.ui.fragments.mentors_challenge.MentorsChallengeFragment;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

public class TrackingsPagerAdapter extends FragmentStatePagerAdapter {

  private static final String TAG = TrackingsPagerAdapter.class.getName();

  private final List<BaseChallengeItem> mData = new ArrayList<>();

  private final Hashtable<Integer, SoftReference<BaseChallengeFragment>> cashedFragments =
      new Hashtable<>();

  public TrackingsPagerAdapter(FragmentManager fm) {
    super(fm);
  }

  @Override public BaseFragment getItem(int position) {
    BaseChallengeFragment requestedFragment = getFragment(position);
    if (requestedFragment == null) {
      BaseChallengeItem item = mData.get(position);
      ChallengeItemType type = item.getType();
      int total = getCount();
      switch (type) {
        case CHALLENGE:
          requestedFragment =
              StartedChallengeFragment.newInstance((ChallengeItem) item, position, total);
          break;
        case CHALLENGE_WILL_START:
          requestedFragment =
              WillStartChallengeFragment.newInstance((ChallengeWillStartItem) item, position,
                  total);
          break;
        case NO_CHALLENGES:
          requestedFragment = NoChallengesFragment.newInstance(position);
          break;

        case MENTORS_CHALLENGE:
          requestedFragment = MentorsChallengeFragment.newInstance();

          break;

        case MENTORS_CHALLENGE_WILL_START:
          break;
        default:
          Log.e(TAG, "not supported type: " + type);
      }

      cashedFragments.put(position, new SoftReference<>(requestedFragment));
    }
    return requestedFragment;
  }

  @Override public int getCount() {
    return mData.size();
  }

  @Override public int getItemPosition(Object object) {
    int position = POSITION_NONE;
    if (object != null && object instanceof BaseChallengeFragment) {
      position = ((BaseChallengeFragment) object).getPosition();
    }

    return position;
  }

  @Override public float getPageWidth(int position) {
    if (position == getCount() - 1) {
      return Constants.CHALLENGES_PAGER_OUTSIDE_POSITION_OFFSET;
    } else {
      return Constants.CHALLENGES_PAGER_POSITION_OFFSET;
    }
  }

  public void setItems(List<BaseChallengeItem> items) {
    mData.clear();
    mData.addAll(items);

    clearFragments();

    notifyDataSetChanged();
  }

  public Tracking getTracking(int position) {
    Tracking tracking = null;
    if (position >= 0 && position < mData.size()) {
      BaseChallengeItem item = mData.get(position);
      ChallengeItemType type = item.getType();
      switch (type) {
        case CHALLENGE:
          tracking = ((ChallengeItem) item).getTracking();
          break;
        case CHALLENGE_WILL_START:
          tracking = ((ChallengeWillStartItem) item).getTracking();
          break;
        case NO_CHALLENGES:
          tracking = null;
          break;
      }
    }

    return tracking;
  }

  public int getTrackingPosition(String trackingId) {
    Tracking tracking = new Tracking();
    tracking.setId(trackingId);
    ChallengeWillStartItem item = new ChallengeWillStartItem();
    item.setTracking(tracking);

    return mData.indexOf(item);
  }

  private BaseChallengeFragment getFragment(int position) {
    SoftReference<BaseChallengeFragment> reference = cashedFragments.get(position);
    return reference == null ? null : reference.get();
  }

  private void clearFragments() {
    Enumeration<Integer> positions = cashedFragments.keys();
    while (positions.hasMoreElements()) {
      SoftReference<BaseChallengeFragment> ref = cashedFragments.get(positions.nextElement());
      if (ref != null && ref.get() != null) {
        ref.get().setPosition(POSITION_NONE);
        ref.clear();
      }
    }

    cashedFragments.clear();
  }
}

package com.sugarman.myb.adapters;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.sugarman.myb.R;
import com.sugarman.myb.api.models.responses.Tracking;
import com.sugarman.myb.api.models.responses.me.requests.Request;
import com.sugarman.myb.api.models.responses.me.requests.User;
import com.sugarman.myb.listeners.ItemRequestsActionListener;
import com.sugarman.myb.listeners.OnRequestsActionListener;
import com.sugarman.myb.ui.views.StrokeImage;
import com.sugarman.myb.utils.StringHelper;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class RequestsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    implements ItemRequestsActionListener {

  private static final String TAG = RequestsAdapter.class.getName();

  private final List<Request> mData = new ArrayList<>();

  private final Context context;

  private final WeakReference<OnRequestsActionListener> mActionListener;

  private final Typeface fontBold;
  private final Typeface fontRegular;

  private final String descTemplate;
  private final String flag1;
  private final String flag2;

  public RequestsAdapter(Context context, OnRequestsActionListener listener) {
    mActionListener = new WeakReference<>(listener);
    this.context = context;

    AssetManager assets = context.getAssets();
    fontBold = Typeface.createFromAsset(assets, context.getString(R.string.font_roboto_bold));
    fontRegular = Typeface.createFromAsset(assets, context.getString(R.string.font_roboto_regular));

    descTemplate = context.getString(R.string.request_description_template);
    flag1 = context.getString(R.string.request_description_flag1);
    flag2 = context.getString(R.string.request_description_flag2);
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    View view = LayoutInflater.from(context).inflate(R.layout.layout_item_requests, parent, false);
    return new RequestsHolder(view, this);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    Request request = getValue(position);
    if (request != null) {
      RequestsHolder requestsHolder = (RequestsHolder) holder;
      Tracking tracking = request.getTracking();
      User user = request.getUser();
      String groupName = tracking.getGroup().getName();
      String userName = user.getName();

      String description = String.format(descTemplate, userName, groupName);
      SpannableStringBuilder span =
          StringHelper.getDoubleBoldSpan(description, flag1, flag2, fontBold, fontRegular);

      requestsHolder.tvDescription.setText(span);
      String pictureUrl = user.getPictureUrl();

      if (TextUtils.isEmpty(pictureUrl)) {
        requestsHolder.ivAvatar.setImageResource(R.drawable.ic_gray_avatar);
      } else {
        Picasso.with(context)
            .load(pictureUrl)
            .fit()
            .centerCrop()
            .placeholder(R.drawable.ic_gray_avatar)
            .error(R.drawable.ic_gray_avatar)
            .into(requestsHolder.ivAvatar.getImageView());
      }
    }
  }

  @Override public int getItemCount() {
    return mData.size();
  }

  @Override public void onClickApprove(int position) {
    if (position >= 0 && position < mData.size()) {
      if (mActionListener.get() != null) {
        mActionListener.get().onApproveRequest(mData.get(position), position);
      }
    } else {
      notifyDataSetChanged();
    }
  }

  @Override public void onClickDecline(int position) {
    if (position >= 0 && position < mData.size()) {
      if (mActionListener.get() != null) {
        mActionListener.get().onDeclineRequest(mData.get(position), position);
      }
    } else {
      notifyDataSetChanged();
    }
  }

  public void setValue(List<Request> values) {
    mData.clear();
    mData.addAll(values);
    notifyDataSetChanged();
  }

  public void removeItem(int position) {
    int total = getItemCount();
    if (position <= total - 1 && position >= 0) {
      mData.remove(position);
      notifyItemRemoved(position);
    } else {
      Log.e(TAG, "try remove item with position: " + position + " total items: " + total);
    }
  }

  public Request getValue(int position) {
    if (position >= 0 && position < mData.size()) {
      return mData.get(position);
    } else {
      return null;
    }
  }

  private static class RequestsHolder extends RecyclerView.ViewHolder
      implements View.OnClickListener {

    private final WeakReference<ItemRequestsActionListener> mActionItemListener;

    private final TextView tvDescription;
    private final StrokeImage ivAvatar;

    RequestsHolder(View itemView, ItemRequestsActionListener clickItemListener) {
      super(itemView);

      mActionItemListener = new WeakReference<>(clickItemListener);

      tvDescription = (TextView) itemView.findViewById(R.id.tv_request_description);
      ivAvatar = (StrokeImage) itemView.findViewById(R.id.iv_avatar);

      View vDecline = itemView.findViewById(R.id.tv_decline);
      View vApprove = itemView.findViewById(R.id.tv_approve);

      vDecline.setOnClickListener(this);
      vApprove.setOnClickListener(this);
    }

    @Override public void onClick(View v) {
      int id = v.getId();
      int position = getAdapterPosition();

      switch (id) {
        case R.id.tv_decline:
          if (mActionItemListener.get() != null) {
            mActionItemListener.get().onClickDecline(position);
          }
          break;
        case R.id.tv_approve:
          if (mActionItemListener.get() != null) {
            mActionItemListener.get().onClickApprove(position);
          }
          break;
        default:
          Log.d(TAG,
              "Click on not processed view with id " + v.getResources().getResourceEntryName(id));
          break;
      }
    }
  }
}

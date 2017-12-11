package com.sugarman.myb.ui.fragments.rescue_challenge.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.arellomobile.mvp.MvpDelegate;
import com.squareup.picasso.CustomPicasso;
import com.sugarman.myb.R;
import com.sugarman.myb.api.models.responses.Member;
import com.sugarman.myb.base.MvpBaseRecyclerAdapter;
import com.sugarman.myb.ui.views.CropCircleTransformation;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

/**
 * Created by yegoryeriomin on 12/8/17.
 */

public class RescueMembersAdapter extends MvpBaseRecyclerAdapter<RecyclerView.ViewHolder> {

  List<Member> members = new ArrayList<>();

  public RescueMembersAdapter(MvpDelegate<?> parentDelegate, List<Member> members) {
    super(parentDelegate, "RescueMembersAdapter");
    this.members = members;

    Timber.e("Members size: " + members.size());
  }

  public RescueMembersAdapter(MvpDelegate<?> parentDelegate) {
    super(parentDelegate, "RescueMembersAdapter");
  }

  public void setMembers(List<Member> members) {
    this.members.clear();
    this.members = members;
    notifyDataSetChanged();
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    Timber.e("zashol v onCreateViewHolder");
    View view;
    RecyclerView.ViewHolder holder = null;
    view = LayoutInflater.from(context).inflate(R.layout.item_rescue_member, parent, false);
    holder = new RescueMemberHolder(view);

    return holder;
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    Timber.e("zashol v onBindViewHolder size " + members.size());
    Member member = members.get(position);
    RescueMemberHolder viewHolder = (RescueMemberHolder) holder;

    CustomPicasso.with(viewHolder.ivAvatar.getContext())
        //.load(Uri.parse(productImageUrl))
        .load(member.getPictureUrl())
        .fit()
        .centerCrop()
        .error(R.drawable.ic_gray_avatar)
        .error(R.drawable.ic_red_avatar)
        .transform(new CropCircleTransformation(0x00ffffff, 4))
        .into(viewHolder.ivAvatar);
  }

  @Override public int getItemCount() {
    return members.size();
  }

  static class RescueMemberHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.ivAvatar) ImageView ivAvatar;
    @BindView(R.id.ivClock) ImageView ivClock;

    RescueMemberHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);

      View vContainer = view.findViewById(R.id.ll_group_member_container);
    }
  }
}

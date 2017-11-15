package com.sugarman.myb.ui.dialogs.sendVkInvitation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.arellomobile.mvp.MvpDialogFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.sugarman.myb.R;
import com.sugarman.myb.api.models.responses.facebook.FacebookFriend;
import java.util.ArrayList;

/**
 * Created by nikita on 29.09.2017.
 */

public class SendVkInvitationDialog extends MvpDialogFragment
    implements ISendVkInvitationDialogView {
  private static final String LIST_TO_INVITE = "LIST_TO_INVITE";
  private static IVkResponse mIVkResponse;
  @InjectPresenter SendVkInvitationDialogPresenter mPresenter;
  @BindView(R.id.etMessage) EditText mEditTextMessage;
  @BindView(R.id.bSend) Button mButtonSend;
  @BindView(R.id.bDismiss) Button mButtonDismiss;
  private ArrayList<FacebookFriend> friends;

  public static SendVkInvitationDialog newInstance(ArrayList<FacebookFriend> selectedFriends,IVkResponse iVkResponse) {
    mIVkResponse = iVkResponse;
    Bundle args = new Bundle();
    args.putParcelableArrayList(LIST_TO_INVITE, selectedFriends);
    SendVkInvitationDialog fragment = new SendVkInvitationDialog();
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    friends = getArguments().getParcelableArrayList(LIST_TO_INVITE);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.dialog_send_vk_invite, container, false);
    ButterKnife.bind(this, rootView);
    getDialog().setTitle("Message for friends");
    mEditTextMessage.setText(getResources().getString(R.string.invite_message));

    return rootView;
  }

  @OnClick(R.id.bSend) public void bSendClicked() {
    mPresenter.sendInvitations(friends, mEditTextMessage.getText().toString());
  }

  @OnClick(R.id.bDismiss) public void bDismissClicked() {
    dismiss();
    getActivity().finish();
  }

  @Override public void doAction() {
    mIVkResponse.action(getDialog());
  }

  @Override public void onDestroyView() {
    mIVkResponse = null;
    super.onDestroyView();
  }
}

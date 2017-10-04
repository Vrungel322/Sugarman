package com.sugarman.myb.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sugarman.myb.R;
import com.sugarman.myb.ui.activities.IntroActivity;

public class FragmentIntro extends Fragment {
  public final static String TAG = FragmentIntro.class.getName();
  public final static String KEY_NUM = TAG + "num";

  public static FragmentIntro newInstance(int num) {
    FragmentIntro frag = new FragmentIntro();
    Bundle args = new Bundle();
    args.putInt(KEY_NUM, num);
    frag.setArguments(args);
    return frag;
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View v = null;
    int numberPage = getArguments().getInt(KEY_NUM);

    switch (numberPage) {
      case 0:
        v = inflater.inflate(R.layout.fragment_intro_1, container, false);
        break;
      case 1:
        v = inflater.inflate(R.layout.fragment_intro_2, container, false);
        break;
      case 2:
        v = inflater.inflate(R.layout.fragment_intro_3, container, false);
        break;
      case 3:
        v = inflater.inflate(R.layout.fragment_intro_4, container, false);
        v.findViewById(R.id.tv_move).setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View v) {
            ((IntroActivity) getActivity()).goToNextScreen();
          }
        });
        break;
    }
    return v;
  }
}

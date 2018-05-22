package com.xzmc.airuishi.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.xzmc.airuishi.R;
import com.xzmc.airuishi.view.HeaderLayout;

public class BaseFragment extends Fragment {
  public HeaderLayout headerLayout;
  public Context ctx;
  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    ctx = getActivity();
    headerLayout = (HeaderLayout) getView().findViewById(R.id.headerLayout);
  }
}

package com.xzmc.airuishi.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

public class NewsFragmentPagerAdapter extends FragmentPagerAdapter {
	private ArrayList<Fragment> fragments;

	public NewsFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	public NewsFragmentPagerAdapter(FragmentManager fm,
			ArrayList<Fragment> fragments) {
		super(fm);
		this.fragments = fragments;
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

	@Override
	public Fragment getItem(int position) {
		return fragments.get(position);
	}

}

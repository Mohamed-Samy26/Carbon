package com.example.myapplication.adapters;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.myapplication.screens.authentication.LoginTapFragment;
import com.example.myapplication.screens.authentication.SignupTapFragment;

import java.util.ArrayList;

public class LoginAdapter extends FragmentPagerAdapter {

    private final ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    private final ArrayList<String> fragmentTitle = new ArrayList<>();

    public LoginAdapter(FragmentManager fragmentManager, int behavior) {
        super(fragmentManager, behavior);

    }

    public Fragment getItem(int position) {
        return fragmentArrayList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentArrayList.size();
    }

    public void addFragment(Fragment fragment, String title) {
        fragmentArrayList.add(fragment);
        fragmentTitle.add(title);
    }

    public CharSequence getPageTitle(int position) {

        return fragmentTitle.get(position);
    }
}

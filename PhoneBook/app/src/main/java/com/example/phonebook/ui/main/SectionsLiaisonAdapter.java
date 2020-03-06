package com.example.phonebook.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.phonebook.LiaisonMan;
import com.example.phonebook.R;

public class SectionsLiaisonAdapter extends FragmentPagerAdapter {
    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.liaison_tab1,R.string.liaison_tab2};
    private final Context mContext;
    private LiaisonMan liaisonMan;
    public LiaisonDetailFragment liaisonDetailFragment;
    public SectionsLiaisonAdapter(Context context, FragmentManager fm, LiaisonMan liaisonMan) {
        super(fm);
        mContext = context;
        this.liaisonMan = liaisonMan;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                liaisonDetailFragment = new LiaisonDetailFragment(liaisonMan);
                return liaisonDetailFragment;
            case 1:
                return new LiaisonPhoneRecordFragment(mContext, liaisonMan);
        }
        return null;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return 2;
    }
}
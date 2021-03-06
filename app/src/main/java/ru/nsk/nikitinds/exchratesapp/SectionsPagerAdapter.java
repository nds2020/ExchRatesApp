package ru.nsk.nikitinds.exchratesapp;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.android.material.appbar.AppBarLayout;

import org.jetbrains.annotations.NotNull;


public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm, int behavior) {
        super(fm, behavior);
        mContext = context;
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new ExchangeRatesSection();
        }

        return new ConverterSection();
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
package ru.karamyshev.time.ui.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import ru.karamyshev.time.R;
import ru.karamyshev.time.model.TimeType;
import ru.karamyshev.time.ui.adapters.MainPagerAdapter;

public class MemoirsTabFragment extends BaseFragment {

    private static final int DAY = 0;
    private static final int WEEK = 1;
    private static final int MONTH = 2;
    private static final int YEAR = 3;

    int[] tabsInfo = new int[]{
            R.string.tab_memoirs_day,
            R.string.tab_memoirs_week,
            R.string.tab_memoirs_month,
            R.string.tab_memoirs_year,
    };

    private TabLayout tabLayout;
    MainPagerAdapter pagerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_memoirs, container, false);
        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.content_pager);

        pagerAdapter = new MainPagerAdapter(getChildFragmentManager());
        pagerAdapter.addFragment(MemoirsFragment.newInstance(TimeType.DAY));
        pagerAdapter.addFragment(MemoirsFragment.newInstance(TimeType.WEEK));
        pagerAdapter.addFragment(MemoirsFragment.newInstance(TimeType.MONTH));
        pagerAdapter.addFragment(MemoirsFragment.newInstance(TimeType.YEAR));
        viewPager.setAdapter(pagerAdapter);


        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < tabLayout.getTabCount(); ++i) {
            //noinspection ConstantConditions
            tabLayout.getTabAt(i).setText(tabsInfo[i]);
        }
        //noinspection ConstantConditions
        tabLayout.getTabAt(0).select();

        view.findViewById(R.id.memoirs_add_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMemoir();
            }
        });
        return view;
    }

    private void addMemoir() {
        final int selected = tabLayout.getSelectedTabPosition();
        final TimeType timeType;
        switch (selected) {
            default:
            case DAY:
                timeType = TimeType.DAY;
                break;
            case WEEK:
                timeType = TimeType.WEEK;
                break;
            case MONTH:
                timeType = TimeType.MONTH;
                break;
            case YEAR:
                timeType = TimeType.YEAR;
                break;
        }
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_memoir_add, null, false);
        final EditText memoirEdit = (EditText) view.findViewById(R.id.memoir_edit);
        final Switch memoirTodaySwitch = (Switch) view.findViewById(R.id.memoir_today_switch);
        memoirTodaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setText(isChecked? R.string.memoir_this_period: R.string.memoir_previous_period);
            }
        });
        new AlertDialog.Builder(getActivity())
                .setView(view)
                .setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String memoirText = memoirEdit.getText().toString();
                        if (!TextUtils.isEmpty(memoirText.trim())) {
                            boolean previousPeriod = !memoirTodaySwitch.isChecked();
                            database.newMemoir(memoirText, timeType, previousPeriod);
                        }
                        Fragment fragment = pagerAdapter.getItem(selected);
                        if (fragment instanceof MemoirsFragment) {
                            ((MemoirsFragment) fragment).updateMemoirs();
                        }
                    }
                })
                .setNegativeButton(R.string.negative_button, null)
                .show();
    }
}

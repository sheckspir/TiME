package ru.karamyshev.time.ui.fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.Calendar;
import java.util.GregorianCalendar;

import ru.karamyshev.time.R;
import ru.karamyshev.time.model.EisenhowerType;
import ru.karamyshev.time.model.TimeType;
import ru.karamyshev.time.ui.adapters.MainPagerAdapter;

public class PlansTabFragment extends BaseFragment {
    private static final String TAG = PlansTabFragment.class.getSimpleName();
    private static final int TODAY = 0;
    private static final int TOMORROW = 1;
    private static final int WEEK = 2;
    private static final int MONTH = 3;
    private static final int YEAR = 4;


    int[] tabsInfo = new int[]{
            R.string.tab_plan_today,
            R.string.tab_plan_tomorrow,
            R.string.tab_plan_week,
            R.string.tab_plan_month,
            R.string.tab_plan_year,
    };

    TabLayout tabLayout;
    MainPagerAdapter pagerAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate "  + this.toString());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView "  + this.toString());
        View view = inflater.inflate(R.layout.fragment_tab_plans, container, false);

        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.content_pager);

        Calendar calendar = new java.util.GregorianCalendar();
        pagerAdapter = new MainPagerAdapter(getChildFragmentManager());
        pagerAdapter.addFragment(PlansFragment.newInstance(calendar.getTime(), TimeType.DAY));
        calendar.add(Calendar.DATE, 1);
        pagerAdapter.addFragment(PlansFragment.newInstance(calendar.getTime(), TimeType.DAY));
        pagerAdapter.addFragment(PlansFragment.newInstance(TimeType.WEEK));
        pagerAdapter.addFragment(PlansFragment.newInstance(TimeType.MONTH));
        pagerAdapter.addFragment(PlansFragment.newInstance(TimeType.YEAR));
        viewPager.setAdapter(pagerAdapter);


        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < tabLayout.getTabCount(); ++i) {
            //noinspection ConstantConditions
            tabLayout.getTabAt(i).setText(tabsInfo[i]);
        }
        //noinspection ConstantConditions
        tabLayout.getTabAt(0).select();

        view.findViewById(R.id.plan_add_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewPlan();
            }
        });

        database.moveOldPlans();

        return view;
    }

    private void addNewPlan() {
        final int selected = tabLayout.getSelectedTabPosition();
        final Calendar calendar = new GregorianCalendar();
        final TimeType timeType;
        switch (selected) {
            default:
            case TODAY:
                timeType = TimeType.DAY;
                break;
            case TOMORROW:
                timeType = TimeType.DAY;
                calendar.add(Calendar.DATE, 1);
                break;
            case WEEK:
                timeType = TimeType.WEEK;
                if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                    calendar.add(Calendar.DATE, 1);
                }
                break;
            case MONTH:
                timeType = TimeType.MONTH;
                if (calendar.getActualMaximum(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)) {
                    calendar.add(Calendar.DATE, 1);
                }
                break;
            case YEAR:
                timeType = TimeType.YEAR;
                break;

        }
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_plan_add, null, false);
        final EditText planEditText = (EditText) view.findViewById(R.id.plan_edit);
        new AlertDialog.Builder(getActivity())
                .setView(view)
                .setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String planText = planEditText.getText().toString();
                        if (!TextUtils.isEmpty(planText.trim())) {
                            database.newPlan(calendar.getTime(), EisenhowerType.NOT_URGENT_IMPORTANT, planEditText.getText().toString(), timeType);
                        }
                        Fragment fragment = pagerAdapter.getItem(selected);
                        if (fragment instanceof PlansFragment) {
                            ((PlansFragment) fragment).updatePlans();
                        }
                    }
                })
                .setNegativeButton(R.string.negative_button, null)
                .show();
    }
}

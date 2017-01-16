package ru.karamyshev.time.ui.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;

import ru.karamyshev.time.R;
import ru.karamyshev.time.model.TimeType;
import ru.karamyshev.time.ui.adapters.MainPagerAdapter;

public class PlansFragment extends BaseFragment {

    int[] tabsInfo = new int[]{
            R.string.tab_plan_today,
            R.string.tab_plan_tomorrow,
            R.string.tab_plan_week,
            R.string.tab_plan_month,
            R.string.tab_plan_year,
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.framgment_plans, container, false);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.content_pager);

        Calendar calendar = new java.util.GregorianCalendar();
        MainPagerAdapter pagerAdapter = new MainPagerAdapter(getFragmentManager());
        pagerAdapter.addFragment(PlanPeriodFragment.newInstance(calendar.getTime(), TimeType.DAY));
        calendar.add(Calendar.DATE, 1);
        pagerAdapter.addFragment(PlanPeriodFragment.newInstance(calendar.getTime(), TimeType.DAY));
        pagerAdapter.addFragment(PlanPeriodFragment.newInstance(TimeType.WEEK));
        pagerAdapter.addFragment(PlanPeriodFragment.newInstance(TimeType.MONTH));
        pagerAdapter.addFragment(PlanPeriodFragment.newInstance(TimeType.YEAR));
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < tabLayout.getTabCount(); ++i) {
            tabLayout.getTabAt(i).setText(tabsInfo[i]);
        }

        database.moveOldPlans();

        return view;
    }
}

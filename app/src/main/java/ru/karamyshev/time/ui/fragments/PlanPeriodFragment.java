package ru.karamyshev.time.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;
import java.util.List;

import io.realm.RealmResults;
import ru.karamyshev.time.R;
import ru.karamyshev.time.database.model.RealmPlan;
import ru.karamyshev.time.model.EisenhowerType;
import ru.karamyshev.time.model.Plan;
import ru.karamyshev.time.model.TimeType;
import ru.karamyshev.time.ui.adapters.PlanAdapter;

public class PlanPeriodFragment extends BaseFragment implements PlanAdapter.PlanListener{
    private static final String ARG_TIME_TIPE = "time_tipe";
    private static final String ARG_DATE = "date";

    RecyclerView planRecycler;
    PlanAdapter planAdapter;
    TimeType timeType;
    Date date;

    public static PlanPeriodFragment newInstance(TimeType timeType) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME_TIPE, timeType);
        PlanPeriodFragment planPeriodFragment = new PlanPeriodFragment();
        planPeriodFragment.setArguments(args);
        return planPeriodFragment;
    }

    public static PlanPeriodFragment newInstance(Date date, TimeType timeType) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME_TIPE, timeType);
        args.putLong(ARG_DATE, date.getTime());
        PlanPeriodFragment planPeriodFragment = new PlanPeriodFragment();
        planPeriodFragment.setArguments(args);
        return planPeriodFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timeType = (TimeType) getArguments().getSerializable(ARG_TIME_TIPE);
        date = new Date(getArguments().getLong(ARG_DATE, new Date().getTime()));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plan_period, container, false);
        planRecycler = (RecyclerView) view.findViewById(R.id.plan_recycler);
        planRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        planAdapter = new PlanAdapter(this);
        planRecycler.setAdapter(planAdapter);

        updatePlans();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updatePlans();
    }

    @Override
    public void planChangeEisenhower(Plan plan, EisenhowerType eisenhowerType) {
        database.updatePlanEisenhower(plan, eisenhowerType);
    }

    @Override
    public void planChecked(Plan plan, boolean isChecked) {
        database.updatePlanComplete(plan, isChecked);
    }

    @Override
    public void onClickPlan(Plan plan) {
        // TODO: 15.01.2017 implement it later
    }

    public void updatePlans() {
        RealmResults<RealmPlan> databasePlanList = database.getPlans(timeType, date);
        updateAdapter(databasePlanList);
    }

    private void updateAdapter(List<? extends Plan> planList) {
        if (planAdapter == null) {
            planAdapter = new PlanAdapter(this);
            planAdapter.setPlanList(planList);
            planRecycler.setAdapter(planAdapter);
        } else {
            planAdapter.setPlanList(planList);
            planAdapter.notifyDataSetChanged();
        }
    }

}

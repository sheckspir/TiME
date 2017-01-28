package ru.karamyshev.time.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ru.karamyshev.time.R;
import ru.karamyshev.time.model.EisenhowerType;
import ru.karamyshev.time.model.Plan;
import ru.karamyshev.time.model.TimeType;
import ru.karamyshev.time.ui.activities.PlanActivity;
import ru.karamyshev.time.ui.adapters.PlanAdapter;

public class PlansFragment extends BaseFragment implements PlanAdapter.PlanListener{
    private static final String ARG_TIME_TIPE = "time_tipe";
    private static final String ARG_SHIFT_PERIOD = "shift_date_period";

    private RecyclerView planRecycler;
    private PlanAdapter planAdapter;
    private TimeType timeType;
    private int shiftPeriod;


    public static PlansFragment newInstance(TimeType timeType) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME_TIPE, timeType);
        PlansFragment plansFragment = new PlansFragment();
        plansFragment.setArguments(args);
        return plansFragment;
    }

    public static PlansFragment newInstance(int shiftPeriod, TimeType timeType) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME_TIPE, timeType);
        args.putInt(ARG_SHIFT_PERIOD, shiftPeriod);
        PlansFragment plansFragment = new PlansFragment();
        plansFragment.setArguments(args);
        return plansFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timeType = (TimeType) getArguments().getSerializable(ARG_TIME_TIPE);
        shiftPeriod = getArguments().getInt(ARG_SHIFT_PERIOD, 0);
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
        Intent intent = new Intent(getActivity(), PlanActivity.class);
        intent.putExtra(PlanActivity.ARG_PLAN_ID, plan.getId());
        getActivity().startActivity(intent);
    }

    public void updatePlans() {
        List<Plan> databasePlanList = database.getPlans(timeType, shiftPeriod);
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

package ru.karamyshev.time.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ru.karamyshev.time.R;
import ru.karamyshev.time.database.model.RealmPlan;
import ru.karamyshev.time.model.EisenhowerType;
import ru.karamyshev.time.model.Plan;
import ru.karamyshev.time.ui.adapters.PlanAdapter;

public class PlansGoalsFragment extends BaseFragment implements PlanAdapter.PlanListener {
    private static final int PLANS_LIMIT = 20;

    private RecyclerView planRecycler;
    private PlanAdapter planAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plans_goals, container, false);
        planRecycler = (RecyclerView) view.findViewById(R.id.plan_recycler);
        planRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        planAdapter = new PlanAdapter(this);
        planRecycler.setAdapter(planAdapter);
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
    public void planChecked(Plan plan, boolean complete) {
        database.updatePlanComplete(plan, complete);
    }

    @Override
    public void onClickPlan(Plan plan) {
        // TODO: 21.01.2017 implement this later
    }

    private void updatePlans() {
        List<RealmPlan> databasePlanList = database.getPlansForMainScreen(PLANS_LIMIT);
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

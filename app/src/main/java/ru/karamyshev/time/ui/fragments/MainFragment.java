package ru.karamyshev.time.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import ru.karamyshev.time.R;
import ru.karamyshev.time.model.EisenhowerType;
import ru.karamyshev.time.model.Plan;
import ru.karamyshev.time.model.TimeType;
import ru.karamyshev.time.ui.activities.PlanActivity;
import ru.karamyshev.time.ui.adapters.PlanAdapter;

public class MainFragment extends BaseFragment implements PlanAdapter.PlanListener {
    private static final int PLANS_LIMIT = 20;
    private static final int HOUR_FOR_SHOW_MESSAGE = 20;

    private TextView messageText;
    private RecyclerView planRecycler;
    private PlanAdapter planAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plans_goals, container, false);
        messageText = (TextView) view.findViewById(R.id.message_text);

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
        checkMemoirs();
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
        Intent intent = new Intent(getActivity(), PlanActivity.class);
        intent.putExtra(PlanActivity.ARG_PLAN_ID, plan.getId());
        getActivity().startActivity(intent);
    }

    @Override
    public void planChangeTimeTodo(Plan plan, int timeTodo) {
        database.updatePlanTimeTodo(plan, timeTodo);
    }

    private void checkMemoirs() {
        int memoirTextId = 0;
        Calendar calendar = new GregorianCalendar();
        if (calendar.get(Calendar.HOUR_OF_DAY) >= HOUR_FOR_SHOW_MESSAGE) {
            TimeType timeType = database.neededCreatedTypeMemoir();
            if (timeType != null) {
                memoirTextId = R.string.memoir_message_add;
            }
        }
        showMessage(memoirTextId);
    }

    private void showMessage(@StringRes int messageResource) {
        if (messageResource == 0) {
            messageText.setVisibility(View.GONE);
        } else {
            messageText.setVisibility(View.VISIBLE);
            messageText.setText(messageResource);
        }
    }

    private void updatePlans() {
        List<Plan> databasePlanList = database.getPlansForMainScreen(PLANS_LIMIT);
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

package ru.karamyshev.time.ui.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.karamyshev.time.R;
import ru.karamyshev.time.database.Database;
import ru.karamyshev.time.database.model.RealmPlan;
import ru.karamyshev.time.model.EisenhowerType;
import ru.karamyshev.time.model.Plan;
import ru.karamyshev.time.model.PlanImpl;
import ru.karamyshev.time.model.TimeType;
import ru.karamyshev.time.ui.adapters.PlanAdapter;

public class PlanPeriodFragment extends BaseFragment implements PlanAdapter.PlanListener {
    private static final String ARG_TIME_TIPE = "time_tipe";
    private static final String ARG_DATE = "date";

    RecyclerView planRecycler;
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

        view.findViewById(R.id.plan_add_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewPlan();
            }
        });
        showPlans();
        return view;
    }

    @Override
    public void planWasUpdated(Plan plan) {
        database.update(plan);
    }

    @Override
    public void onClickPlan(Plan plan) {
        // TODO: 15.01.2017 implement it later
    }

    private void showPlans() {
        List<RealmPlan> databasePlanList = database.getPlans(timeType, date);
        List<Plan>  planList = new ArrayList<>();
        for (RealmPlan realmPlan : databasePlanList) {
            planList.add(new PlanImpl(realmPlan));
        }
        updateAdapter(planList);
    }

    private void updateAdapter(List<? extends Plan> planList) {
        planRecycler.setAdapter(new PlanAdapter(planList, this));
    }

    private void addNewPlan() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_plan_add, null, false);
        final EditText planEditText = (EditText) view.findViewById(R.id.plan_edit);
        new AlertDialog.Builder(getActivity())
                .setView(view)
                .setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String planText = planEditText.getText().toString();
                        if (!TextUtils.isEmpty(planText.trim())) {
                            Database database = new Database();
                            database.newPlan(date, EisenhowerType.NOT_URGENT_IMPORTANT, planEditText.getText().toString(), timeType);
                            database.close();
                            showPlans();
                        }
                    }
                })
                .setNegativeButton(R.string.negative_button, null)
                .show();
    }
}

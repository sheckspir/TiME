package ru.karamyshev.time.ui.adapters;


import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;

import ru.karamyshev.time.R;
import ru.karamyshev.time.model.EisenhowerType;
import ru.karamyshev.time.model.Plan;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.PlanViewHolder> {

    public interface PlanListener {
        void planChangeEisenhower(Plan plan, EisenhowerType eisenhowerType);

        void planChecked(Plan plan, boolean completed);

        void planChangeTimeTodo(Plan plan, int timeTodo);

        void onClickPlan(Plan plan);
    }

    static class PlanViewHolder extends RecyclerView.ViewHolder {
        PlanListener planUpdateListener;
        CheckBox checkBox;
        TextView planText;
        View urgentImportantButton;
        View urgentNotImportantButton;
        View notUrgentImportantButton;
        View notUrgentNotImportantButton;
        SeekBar timeTodoSeekBar;
        TextView timeTodoTextView;

        PlanViewHolder(View itemView, PlanListener planUpdateListener) {
            super(itemView);
            this.planUpdateListener = planUpdateListener;
            checkBox = (CheckBox) itemView.findViewById(R.id.plan_complete_check);
            planText = (TextView) itemView.findViewById(R.id.plan_text);
            urgentImportantButton = itemView.findViewById(R.id.urgent_important_layout);
            notUrgentImportantButton = itemView.findViewById(R.id.not_urgent_important_layout);
            urgentNotImportantButton = itemView.findViewById(R.id.urgent_not_important_layout);
            notUrgentNotImportantButton = itemView.findViewById(R.id.not_urgent_not_important_layout);
            timeTodoSeekBar = (SeekBar) itemView.findViewById(R.id.todo_time_seekbar);
            timeTodoTextView = (TextView) itemView.findViewById(R.id.todo_time_text);
        }

        void bind(final Plan plan) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    planUpdateListener.onClickPlan(plan);
                }
            });
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    planUpdateListener.planChecked(plan, isChecked);
                }
            });
            checkBox.setChecked(plan.isComplete());
            planText.setText(plan.getText());
            showPlanColor(plan);
            urgentImportantButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updatePlanType(plan, EisenhowerType.URGENT_IMPORTANT);
                }
            });
            notUrgentImportantButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updatePlanType(plan, EisenhowerType.NOT_URGENT_IMPORTANT);
                }
            });
            urgentNotImportantButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updatePlanType(plan, EisenhowerType.URGENT_NOT_IMPORTANT);
                }
            });
            notUrgentNotImportantButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updatePlanType(plan, EisenhowerType.NOT_URGENT_NOT_IMPORTANT);
                }
            });

            updateTimeTodoText(plan.getTimeTodo());
            timeTodoSeekBar.setProgress(plan.getTimeTodo());
            timeTodoSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    updateTimeTodoText(progress/Plan.PERIOD_TIME_TODO * Plan.PERIOD_TIME_TODO);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    //nope
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    int newProgress = seekBar.getProgress()/Plan.PERIOD_TIME_TODO * Plan.PERIOD_TIME_TODO;
                    updateTimeTodoText(newProgress);
                    seekBar.setProgress(newProgress);
                    planUpdateListener.planChangeTimeTodo(plan, newProgress);
                }
            });
        }

        private void updateTimeTodoText(int newProgress) {
            if (newProgress > 0) {
                if (newProgress / 60 > 0) {
                    timeTodoTextView.setText(timeTodoTextView.getContext().getString(R.string.plan_time_todo_hm, newProgress/60, newProgress%60));
                } else {
                    timeTodoTextView.setText(timeTodoTextView.getContext().getString(R.string.plan_time_todo_m, newProgress));
                }
            } else {
                timeTodoTextView.setText(R.string.plan_time_todo_zero);
            }
        }

        private void updatePlanType(Plan plan, EisenhowerType newType) {
            planUpdateListener.planChangeEisenhower(plan, newType);
            showPlanColor(plan);
        }

        private void showPlanColor(Plan plan) {
            int colorPlan;
            switch (plan.getEisenhowerType()) {
                case URGENT_IMPORTANT:
                    colorPlan = ContextCompat.getColor(planText.getContext(), R.color.urgent_important);
                    break;
                case URGENT_NOT_IMPORTANT:
                    colorPlan = ContextCompat.getColor(planText.getContext(), R.color.urgent_not_important);
                    break;
                case NOT_URGENT_IMPORTANT:
                    colorPlan = ContextCompat.getColor(planText.getContext(), R.color.not_urgent_important);
                    break;
                default:
                case NOT_URGENT_NOT_IMPORTANT:
                    colorPlan = ContextCompat.getColor(planText.getContext(), R.color.not_urgent_not_important);
                    break;
            }
            planText.setTextColor(colorPlan);
        }
    }

    private List<? extends Plan> planList;
    private PlanListener updateListener;

    public PlanAdapter(PlanListener planUpdateListener) {
        this.updateListener = planUpdateListener;
    }

    @Override
    public PlanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PlanViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plan, parent, false), updateListener);
    }

    @Override
    public void onBindViewHolder(PlanViewHolder holder, int position) {
        holder.bind(planList.get(position));
    }

    @Override
    public int getItemCount() {
        return planList.size();
    }

    public void setPlanList(List<? extends Plan> planList) {
        this.planList = planList;
    }
}

package ru.karamyshev.time.model;


import java.util.Date;

import ru.karamyshev.time.database.model.RealmPlan;

public class PlanImpl implements Plan {
    private int id;
    private Date startDate;
    private EisenhowerType eisenhowerType;
    private boolean isComplete;
    private String text;
    private TimeType timeType;

    public PlanImpl(RealmPlan realmPlan) {
        id = realmPlan.getId();
        startDate = realmPlan.getStartDate();
        eisenhowerType = realmPlan.getEisenhowerType();
        isComplete = realmPlan.isComplete();
        text = realmPlan.getText();
        timeType = realmPlan.getTimeType();
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public Date getStartDate() {
        return startDate;
    }

    @Override
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Override
    public EisenhowerType getEisenhowerType() {
        return eisenhowerType;
    }

    @Override
    public void setEisenhowerType(EisenhowerType eisenhowerType) {
        this.eisenhowerType = eisenhowerType;
    }

    @Override
    public boolean isComplete() {
        return isComplete;
    }

    @Override
    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public TimeType getTimeType() {
        return timeType;
    }

    @Override
    public void setTimeType(TimeType timeType) {
        this.timeType = timeType;
    }
}

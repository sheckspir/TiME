package ru.karamyshev.time.database.model;


import java.util.Date;

import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;
import ru.karamyshev.time.model.EisenhowerType;
import ru.karamyshev.time.model.Plan;
import ru.karamyshev.time.model.TimeType;

@RealmClass
public class RealmPlan implements RealmModel, Plan {
    @PrimaryKey
    private int id;

    private Date startDate;
    private int eisenhowerType;
    private boolean isComplete;
    private String text;
    private int timeType;

    public RealmPlan() {
    }

    public void update(Plan plan) {
        this.startDate = plan.getStartDate();
        this.eisenhowerType = plan.getEisenhowerType().getId();
        this.isComplete = plan.isComplete();
        this.text = plan.getText();
        this.timeType = plan.getTimeType().getId();
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
    public EisenhowerType getEisenhowerType() {
        return EisenhowerType.getFromId(eisenhowerType);
    }

    @Override
    public boolean isComplete() {
        return isComplete;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public TimeType getTimeType() {
        return TimeType.getFromId(timeType);
    }

    @Override
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Override
    public void setEisenhowerType(EisenhowerType eisenhowerType) {
        this.eisenhowerType = eisenhowerType.getId();
    }

    @Override
    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void setTimeType(TimeType timeType) {
        this.timeType = timeType.getId();
    }
}

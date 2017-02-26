package ru.karamyshev.time.model;

import java.util.Date;

public class PlanImpl implements Plan {

    private int id;

    private Date startDate;
    private EisenhowerType eisenhowerType;
    private boolean isComplete;
    private String text;
    private TimeType timeType;
    private int timeTodo;

    public PlanImpl(Plan plan) {
        this.id = plan.getId();
        this.startDate = plan.getStartDate();
        this.eisenhowerType = plan.getEisenhowerType();
        this.isComplete = plan.isComplete();
        this.text = plan.getText();
        this.timeType = plan.getTimeType();
        this.timeTodo = plan.getTimeTodo();
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Date getStartDate() {
        return startDate;
    }

    @Override
    public EisenhowerType getEisenhowerType() {
        return eisenhowerType;
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
        return timeType;
    }

    @Override
    public int getTimeTodo() {
        return timeTodo;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Override
    public void setEisenhowerType(EisenhowerType eisenhowerType) {
        this.eisenhowerType = eisenhowerType;
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
        this.timeType = timeType;
    }

    @Override
    public void setTimeTodo(int timeTodo) {
        this.timeTodo = timeTodo;
    }
}

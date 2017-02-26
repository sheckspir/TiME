package ru.karamyshev.time.model;


import java.util.Date;

public interface Plan {
    public static final int PERIOD_TIME_TODO = 15;

    int getId();

    Date getStartDate();

    EisenhowerType getEisenhowerType();

    boolean isComplete();

    String getText();

    int getTimeTodo();

    TimeType getTimeType();

    void setId(int id);

    void setStartDate(Date startDate);

    void setText(String text);

    void setTimeType(TimeType timeType);

    void setComplete(boolean complete);

    void setEisenhowerType(EisenhowerType eisenhowerType);

    void setTimeTodo(int timeTodo);
}

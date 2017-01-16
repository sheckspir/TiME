package ru.karamyshev.time.model;


import java.util.Date;

public interface Plan {
    int getId();

    void setId(int id);

    Date getStartDate();

    EisenhowerType getEisenhowerType();

    boolean isComplete();

    String getText();

    TimeType getTimeType();

    void setStartDate(Date startDate);

    void setText(String text);

    void setTimeType(TimeType timeType);

    void setComplete(boolean complete);

    void setEisenhowerType(EisenhowerType eisenhowerType);
}

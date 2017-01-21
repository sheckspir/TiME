package ru.karamyshev.time.model;

import java.util.Date;

public interface Memoir {
    int getId();
    Date getDate();
    String getText();
    TimeType getTimeType();

    void setDate(Date date);
    void setText(String text);
    void setTimeType(TimeType timeType);
}

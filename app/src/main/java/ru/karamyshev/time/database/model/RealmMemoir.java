package ru.karamyshev.time.database.model;

import java.util.Date;

import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;
import ru.karamyshev.time.model.Memoir;
import ru.karamyshev.time.model.TimeType;

@RealmClass
public class RealmMemoir implements RealmModel, Memoir {
    @PrimaryKey
    private int id;

    private Date date;
    private String text;
    private int timeType;

    public RealmMemoir() {
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Date getDate() {
        return date;
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
    public void setDate(Date date) {
        this.date = date;
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

package ru.karamyshev.time.model;


public enum TimeType {
    DAY(0),
    WEEK(1),
    MONTH(2),
    YEAR(3);

    int id;

    TimeType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static TimeType getFromId(int id) {
        switch (id) {
            default:
            case 0:
                return DAY;
            case 1:
                return WEEK;
            case 2:
                return MONTH;
            case 3:
                return YEAR;
        }
    }
}

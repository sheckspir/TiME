package ru.karamyshev.time.model;


public enum EisenhowerType {
    URGENT_IMPORTANT(0),
    NOT_URGENT_IMPORTANT(1),
    URGENT_NOT_IMPORTANT(2),
    NOT_URGENT_NOT_IMPORTANT(3);

    int id;

    EisenhowerType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static EisenhowerType getFromId(int id) {
        switch (id) {
            case 0:
                return URGENT_IMPORTANT;
            case 1:
                return NOT_URGENT_IMPORTANT;
            case 2:
                return URGENT_NOT_IMPORTANT;
            default:
            case 3:
                return NOT_URGENT_NOT_IMPORTANT;
        }
    }
}

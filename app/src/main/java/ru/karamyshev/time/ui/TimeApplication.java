package ru.karamyshev.time.ui;


import android.app.Application;

import ru.karamyshev.time.database.Database;

public class TimeApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Database.init(this);
    }
}

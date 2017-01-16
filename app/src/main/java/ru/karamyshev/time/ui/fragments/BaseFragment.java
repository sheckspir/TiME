package ru.karamyshev.time.ui.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import ru.karamyshev.time.database.Database;

public class BaseFragment extends Fragment {
    Database database;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = new Database();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        database.close();
    }
}

package ru.karamyshev.time.database;

import java.util.AbstractList;

import io.realm.RealmChangeListener;
import io.realm.RealmModel;
import io.realm.RealmResults;

public class DatabaseResults<T extends RealmModel> extends AbstractList<T> {

    public interface OnChangeListener{
        void onChange();
    }

    private RealmResults<T> realmResults;

    public DatabaseResults(RealmResults<T> realmResults) {
        this.realmResults = realmResults;
    }

    @Override
    public T get(int index) {
        return realmResults.get(index);
    }

    @Override
    public int size() {
        return realmResults.size();
    }

    public void setOnChangeListener(final OnChangeListener onChangeListener) {
        realmResults.addChangeListener(new RealmChangeListener<RealmResults<T>>() {
            @Override
            public void onChange(RealmResults<T> element) {
                onChangeListener.onChange();
            }
        });
    }

    public void close() {
        realmResults.removeChangeListeners();
    }
}

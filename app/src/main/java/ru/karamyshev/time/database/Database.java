package ru.karamyshev.time.database;


import android.content.Context;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.exceptions.RealmMigrationNeededException;
import ru.karamyshev.time.database.model.RealmPlan;
import ru.karamyshev.time.model.EisenhowerType;
import ru.karamyshev.time.model.Plan;
import ru.karamyshev.time.model.TimeType;

public class Database {

    private static RealmConfiguration configuration;

    public static void init(Context context) {
        Realm.init(context);
        configuration = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(configuration);
    }

    private Realm realm;

    public Database() {
        try {
            realm = Realm.getDefaultInstance();
        } catch (RealmMigrationNeededException e) {
            Realm.deleteRealm(configuration);
            realm = Realm.getDefaultInstance();
        }
    }

    public Plan newPlan(Date date, EisenhowerType eisenhowerType, String text, TimeType timeType) {
        realm.beginTransaction();
        Plan plan = realm.createObject(RealmPlan.class, incrementId(RealmPlan.class));
        plan.setStartDate(date);
        plan.setEisenhowerType(eisenhowerType);
        plan.setText(text);
        plan.setTimeType(timeType);
        realm.commitTransaction();
        return plan;
    }

    public void update(Plan plan) {
        realm.beginTransaction();
        RealmPlan databasePlan = realm.where(RealmPlan.class).equalTo("id", plan.getId()).findFirst();
        if (databasePlan != null) {
            databasePlan.update(plan);
        } else {
            throw new IllegalArgumentException("plan not inside database " + plan.getId());
        }
        realm.commitTransaction();
    }

    public RealmResults<RealmPlan> getAllPlans() {
        return realm.where(RealmPlan.class).findAll();
    }

    public RealmResults<RealmPlan> getPlans(TimeType timeType, Date date) {
        Calendar startDate = new GregorianCalendar();
        startDate.setTimeInMillis(date.getTime());
        startDate = new GregorianCalendar(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DATE));
        Calendar endDate = new GregorianCalendar(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DATE));
        endDate.add(Calendar.DATE, 1);
        endDate.add(Calendar.MILLISECOND, -1);
        return realm.where(RealmPlan.class).equalTo("timeType", timeType.getId())
                .between("startDate", startDate.getTime(), endDate.getTime())
                .findAll();
        // TODO: 15.01.2017 sort
    }

    public void moveOldPlans() {
        Calendar dateNow = new GregorianCalendar();
        Calendar startDate = new GregorianCalendar();
        startDate.setTimeInMillis(0);
        Calendar endDate = new GregorianCalendar(dateNow.get(Calendar.YEAR), dateNow.get(Calendar.MONTH), dateNow.get(Calendar.DATE));
        endDate.add(Calendar.MILLISECOND, -1);
        List<RealmPlan> dayOldPlans = realm.where(RealmPlan.class)
                .equalTo("timeType", TimeType.DAY.getId())
                .between("startDate", startDate.getTime(), endDate.getTime())
                .findAll();
        endDate = new GregorianCalendar(dateNow.get(Calendar.YEAR), dateNow.get(Calendar.MONTH), dateNow.get(Calendar.DATE));
        endDate.add(Calendar.DATE, -endDate.get(Calendar.DAY_OF_WEEK));
        endDate.add(Calendar.MILLISECOND, -1);
        List<RealmPlan> weekOldPlans = realm.where(RealmPlan.class)
                .equalTo("timeType", TimeType.WEEK.getId())
                .between("startDate", startDate.getTime(), endDate.getTime())
                .findAll();
        endDate = new GregorianCalendar(dateNow.get(Calendar.YEAR), dateNow.get(Calendar.MONTH), 1);
        List<RealmPlan> monthOldPlans = realm.where(RealmPlan.class)
                .equalTo("timeType", TimeType.MONTH.getId())
                .between("startDate", startDate.getTime(), endDate.getTime())
                .findAll();
        endDate = new GregorianCalendar(dateNow.get(Calendar.YEAR), Calendar.JANUARY, 1);
        List<RealmPlan> yearOldPlans = realm.where(RealmPlan.class)
                .equalTo("timeType", TimeType.YEAR.getId())
                .between("startDate", startDate.getTime(), endDate.getTime())
                .findAll();
        if (dayOldPlans.size() > 0 || weekOldPlans.size() > 0 || monthOldPlans.size() > 0 || yearOldPlans.size() > 0) {
            realm.beginTransaction();
            for (RealmPlan plan : dayOldPlans) {
                plan.setStartDate(dateNow.getTime());
                plan.setTimeType(TimeType.WEEK);
            }
            for (RealmPlan plan : weekOldPlans) {
                plan.setStartDate(dateNow.getTime());
                plan.setTimeType(TimeType.MONTH);
            }
            for (RealmPlan plan : monthOldPlans) {
                plan.setStartDate(dateNow.getTime());
                plan.setTimeType(TimeType.YEAR);
            }
            for (RealmPlan plan : yearOldPlans) {
                plan.setStartDate(dateNow.getTime());
            }
            realm.commitTransaction();
        }

    }

    public void close() {
        realm.close();
    }

    private int incrementId(Class clazz) {
        int id = 0;
        Number number = realm.where(clazz).max("id");
        if (number != null) {
            id = number.intValue() + 1;
        }
        return id;
    }
}

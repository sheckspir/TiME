package ru.karamyshev.time.database;


import android.content.Context;
import android.util.Log;

import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.exceptions.RealmMigrationNeededException;
import ru.karamyshev.time.database.model.RealmMemoir;
import ru.karamyshev.time.database.model.RealmPlan;
import ru.karamyshev.time.model.EisenhowerType;
import ru.karamyshev.time.model.Memoir;
import ru.karamyshev.time.model.Plan;
import ru.karamyshev.time.model.TimeType;

public class Database {
    private static final String TAG = Database.class.getSimpleName();

    private static RealmConfiguration configuration;

    public static void init(Context context) {
        try {
            Realm.init(context);
        } catch (RuntimeException e) {
            configuration = new RealmConfiguration.Builder().build();
            try {
                Realm.migrateRealm(configuration);
            } catch (FileNotFoundException e1) {
                e.printStackTrace();
            }
        }
        if (configuration == null) {
            configuration = new RealmConfiguration.Builder().build();
        }
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

    public void updatePlanEisenhower(Plan plan, EisenhowerType eisenhowerType) {
        realm.beginTransaction();
        RealmPlan databasePlan = realm.where(RealmPlan.class).equalTo("id",plan.getId()).findFirst();
        if (databasePlan != null) {
            databasePlan.setEisenhowerType(eisenhowerType);
        } else {
            Log.wtf(TAG, "can't update plan. Plan not find. id = " + plan.getId() + " " + plan);
        }
        realm.commitTransaction();
    }

    public void updatePlanComplete(Plan plan, boolean complete) {
        realm.beginTransaction();
        RealmPlan databasePlan = realm.where(RealmPlan.class).equalTo("id",plan.getId()).findFirst();
        if (databasePlan != null) {
            databasePlan.setComplete(complete);
        } else {
            Log.wtf(TAG, "can't update plan. Plan not find. id = " + plan.getId() + " " + plan);
        }
        realm.commitTransaction();
    }

    public Memoir newMemoir(Date date, String text, TimeType timeType) {
        realm.beginTransaction();
        Memoir memoir = realm.createObject(RealmMemoir.class, incrementId(RealmMemoir.class));
        memoir.setDate(date);
        memoir.setText(text);
        memoir.setTimeType(timeType);
        realm.commitTransaction();
        return memoir;
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
                .findAllSorted("eisenhowerType", Sort.ASCENDING, "isComplete", Sort.ASCENDING);
    }

    public List<RealmPlan> getPlansForMainScreen(int limit) {
        String[] fieldNames = new String[]{"eisenhowerType","timeType","startDate"};
        Sort[] sorts = new Sort[]{Sort.ASCENDING, Sort.ASCENDING, Sort.ASCENDING};
        RealmResults<RealmPlan> allPlans = realm.where(RealmPlan.class)
                .notEqualTo("isComplete", true)
                .findAllSorted(fieldNames, sorts);
        if (allPlans.size() > 0) {
            int localLimit = limit < allPlans.size()? limit: allPlans.size();
            return allPlans.subList(0, localLimit);
        } else {
            return allPlans;
        }
    }

    public void moveOldPlans() {
        Calendar dateNow = new GregorianCalendar();
        Calendar endDate = new GregorianCalendar(dateNow.get(Calendar.YEAR), dateNow.get(Calendar.MONTH), dateNow.get(Calendar.DATE));
        RealmResults<RealmPlan> dayOldPlans = realm.where(RealmPlan.class)
                .equalTo("timeType", TimeType.DAY.getId())
                .lessThan("startDate", endDate.getTime())
                .findAll();
        endDate = new GregorianCalendar(dateNow.get(Calendar.YEAR), dateNow.get(Calendar.MONTH), dateNow.get(Calendar.DATE));
        endDate.setFirstDayOfWeek(Calendar.MONDAY);
        endDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        RealmResults<RealmPlan> weekOldPlans = realm.where(RealmPlan.class)
                .equalTo("timeType", TimeType.WEEK.getId())
                .lessThan("startDate", endDate.getTime())
                .findAll();
        endDate = new GregorianCalendar(dateNow.get(Calendar.YEAR), dateNow.get(Calendar.MONTH), 1);
        RealmResults<RealmPlan> monthOldPlans = realm.where(RealmPlan.class)
                .equalTo("timeType", TimeType.MONTH.getId())
                .lessThan("startDate", endDate.getTime())
                .findAll();
        endDate = new GregorianCalendar(dateNow.get(Calendar.YEAR), Calendar.JANUARY, 1);
        RealmResults<RealmPlan> yearOldPlans = realm.where(RealmPlan.class)
                .equalTo("timeType", TimeType.YEAR.getId())
                .lessThan("startDate", endDate.getTime())
                .findAll();
        if (dayOldPlans.size() > 0 || weekOldPlans.size() > 0 || monthOldPlans.size() > 0 || yearOldPlans.size() > 0) {
            realm.beginTransaction();
            int i = 0;
            for (RealmPlan plan : dayOldPlans) {
                if (plan.isComplete()) {
                    dayOldPlans.deleteFromRealm(i);
                } else {
                    plan.setStartDate(dateNow.getTime());
                    plan.setTimeType(TimeType.WEEK);
                }
                i++;
            }
            i = 0;
            for (RealmPlan plan : weekOldPlans) {
                if (plan.isComplete()) {
                    weekOldPlans.deleteFromRealm(i);
                } else {
                    plan.setStartDate(dateNow.getTime());
                    plan.setTimeType(TimeType.MONTH);
                }
                i++;
            }
            i = 0;
            for (RealmPlan plan : monthOldPlans) {
                if (plan.isComplete()) {
                    monthOldPlans.deleteFromRealm(i);
                } else {
                    plan.setStartDate(dateNow.getTime());
                    plan.setTimeType(TimeType.YEAR);
                }
                i++;
            }
            i = 0;
            for (RealmPlan plan : yearOldPlans) {
                if (plan.isComplete()) {
                    yearOldPlans.deleteFromRealm(i);
                } else {
                    plan.setStartDate(dateNow.getTime());
                }
                i++;
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

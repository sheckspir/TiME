package ru.karamyshev.time.database;


import android.content.Context;
import android.util.Log;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.exceptions.RealmMigrationNeededException;
import ru.karamyshev.time.core.Utils;
import ru.karamyshev.time.database.model.RealmMemoir;
import ru.karamyshev.time.database.model.RealmPlan;
import ru.karamyshev.time.model.EisenhowerType;
import ru.karamyshev.time.model.Memoir;
import ru.karamyshev.time.model.Plan;
import ru.karamyshev.time.model.PlanImpl;
import ru.karamyshev.time.model.TimeType;

public class Database {
    private static final String TAG = Database.class.getSimpleName();

    private static RealmConfiguration configuration;

    public static void init(Context context) {
        try {
            Realm.init(context);
        } catch (RuntimeException e) {
            configuration = createConfiguration();
            try {
                Realm.migrateRealm(configuration);
            } catch (FileNotFoundException e1) {
                e.printStackTrace();
            }
        }
        if (configuration == null) {
            configuration = createConfiguration();
        }
        Realm.setDefaultConfiguration(configuration);
    }

    private static RealmConfiguration createConfiguration() {
        return new RealmConfiguration.Builder()
                .schemaVersion(1)
                .migration(new TimeMigration())
                .build();
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

    public void updatePlan(Plan plan, String text, TimeType timeType, int shift) {
        realm.beginTransaction();
        RealmPlan databasePlan = realm.where(RealmPlan.class).equalTo("id", plan.getId()).findFirst();
        plan.setText(text);
        plan.setTimeType(timeType);
        plan.setStartDate(Utils.getStartDateForType(timeType, shift).getTime());
        if (databasePlan != null) {
            databasePlan.setText(text);
            databasePlan.setTimeType(timeType);
            databasePlan.setStartDate(Utils.getStartDateForType(timeType, shift).getTime());
        } else {
            Log.wtf(TAG, "can't update plan. Plan not find. id = " + plan.getId() + " " + plan);
        }
        realm.commitTransaction();
    }

    public void updatePlanEisenhower(Plan plan, EisenhowerType eisenhowerType) {
        realm.beginTransaction();
        RealmPlan databasePlan = realm.where(RealmPlan.class).equalTo("id", plan.getId()).findFirst();
        plan.setEisenhowerType(eisenhowerType);
        if (databasePlan != null) {
            databasePlan.setEisenhowerType(eisenhowerType);
        } else {
            Log.wtf(TAG, "can't update plan. Plan not find. id = " + plan.getId() + " " + plan);
        }
        realm.commitTransaction();
    }

    public void updatePlanComplete(Plan plan, boolean complete) {
        realm.beginTransaction();
        RealmPlan databasePlan = realm.where(RealmPlan.class).equalTo("id", plan.getId()).findFirst();
        plan.setComplete(complete);
        if (databasePlan != null) {
            databasePlan.setComplete(complete);
        } else {
            Log.wtf(TAG, "can't update plan. Plan not find. id = " + plan.getId() + " " + plan);
        }
        realm.commitTransaction();
    }

    public boolean removePlan(int planId) {
        realm.beginTransaction();
        boolean success = realm.where(RealmPlan.class).equalTo("id", planId).findAll().deleteFirstFromRealm();
        realm.commitTransaction();
        return success;
    }

    public boolean newMemoir(String text, TimeType timeType, boolean previousPeriod) {
        Calendar memoirDate = Utils.getEndDateForType(timeType, previousPeriod? -1 : 0);
        long countMemoirs = realm.where(RealmMemoir.class).equalTo("date", memoirDate.getTime()).count();
        if (countMemoirs == 0) {
            realm.beginTransaction();
            Memoir memoir = realm.createObject(RealmMemoir.class, incrementId(RealmMemoir.class));
            memoir.setDate(memoirDate.getTime());
            memoir.setText(text);
            memoir.setTimeType(timeType);
            realm.commitTransaction();
            return true;
        }
        return false;
    }

    public TimeType neededCreatedTypeMemoir() {
        TimeType neededCreateType = null;
        Calendar calendar = new GregorianCalendar();
        TimeType timeTypeToCheck;
        //noinspection WrongConstant
        if (calendar.get(Calendar.DAY_OF_YEAR) == calendar.getActualMaximum(Calendar.DAY_OF_YEAR)) {
            timeTypeToCheck = TimeType.YEAR;
        } else if (calendar.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)) {
            timeTypeToCheck = TimeType.MONTH;
        } else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            timeTypeToCheck = TimeType.WEEK;
        } else {
            timeTypeToCheck = TimeType.DAY;
        }
        Calendar memoirEnd = Utils.getEndDateForType(timeTypeToCheck, 0);
        Calendar memoirStart = Utils.getStartDateForType(timeTypeToCheck, 0);
        long countMemoir = realm.where(RealmMemoir.class)
                .equalTo("timeType", timeTypeToCheck.getId())
                .between("date", memoirStart.getTime(), memoirEnd.getTime())
                .count();
        if (countMemoir > 0) {
            neededCreateType = timeTypeToCheck;
        }
        return neededCreateType;
    }

    public DatabaseResults<RealmMemoir> getMemoirs(TimeType timeType) {
        return new DatabaseResults<>(realm.where(RealmMemoir.class)
                .equalTo("timeType", timeType.getId())
                .findAll());
    }

    public List<Plan> getPlans(TimeType timeType, int shiftPeriod) {
        RealmQuery<RealmPlan> query = realm.where(RealmPlan.class).equalTo("timeType", timeType.getId());
        if (timeType == TimeType.DAY) {
            Calendar endDate = Utils.getEndDateForType(timeType, shiftPeriod);
            Calendar startDate = Utils.getStartDateForType(timeType, shiftPeriod);
            query.between("startDate", startDate.getTime(), endDate.getTime());
        }
        RealmResults<RealmPlan> realmPlans = query
                .findAllSorted("eisenhowerType", Sort.ASCENDING, "isComplete", Sort.ASCENDING);
        List<Plan> planList = new ArrayList<>();
        for (RealmPlan realmPlan : realmPlans) {
            planList.add(new PlanImpl(realmPlan));
        }
        return planList;
    }

    public List<Plan> getPlansForMainScreen(int limit) {
        String[] fieldNames = new String[]{"eisenhowerType", "timeType", "startDate"};
        Sort[] sorts = new Sort[]{Sort.ASCENDING, Sort.ASCENDING, Sort.ASCENDING};
        RealmResults<RealmPlan> allPlans = realm.where(RealmPlan.class)
                .notEqualTo("isComplete", true)
                .findAllSorted(fieldNames, sorts);
        List<Plan> planList = new ArrayList<>();
        int localLimit = limit < allPlans.size()? limit: allPlans.size();
        for (int i = 0; i < localLimit; i++) {
            planList.add(new PlanImpl(allPlans.get(i)));
        }
        return planList;
    }

    public Plan getPlan(int planId) {
        return realm.where(RealmPlan.class)
                .equalTo("id", planId)
                .findFirst();
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

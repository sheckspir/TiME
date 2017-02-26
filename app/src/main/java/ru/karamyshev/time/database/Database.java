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
                .schemaVersion(2)
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

    public boolean updatePlan(Plan plan, String text, TimeType timeType, int shift) {
        RealmPlan databasePlan = realm.where(RealmPlan.class).equalTo("id", plan.getId()).findFirst();
        plan.setText(text);
        plan.setTimeType(timeType);
        plan.setStartDate(Utils.getStartDateForType(timeType, shift).getTime());
        if (databasePlan != null) {
            realm.beginTransaction();
            databasePlan.setText(text);
            databasePlan.setTimeType(timeType);
            databasePlan.setStartDate(Utils.getStartDateForType(timeType, shift).getTime());
            realm.commitTransaction();
            return true;
        } else {
            Log.wtf(TAG, "can't update plan. Plan not find. id = " + plan.getId() + " " + plan);
            return false;
        }
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

    public void updatePlanTimeTodo(Plan plan, int newTimeTodo) {
        RealmPlan databasePlan = realm.where(RealmPlan.class).equalTo("id", plan.getId()).findFirst();
        realm.beginTransaction();
        plan.setTimeTodo(newTimeTodo);
        if (databasePlan != null) {
            databasePlan.setTimeTodo(newTimeTodo);
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
        Calendar memoirEndDate = Utils.getEndDateForType(timeType, previousPeriod? -1 : 0);
        Calendar memoirStartDate = Utils.getStartDateForType(timeType, previousPeriod? -1 : 0);
        long countMemoirs = realm.where(RealmMemoir.class)
                .equalTo("timeType", timeType.getId())
                .between("date",memoirStartDate.getTime(), memoirEndDate.getTime())
                .count();
        if (countMemoirs == 0) {
            realm.beginTransaction();
            Memoir memoir = realm.createObject(RealmMemoir.class, incrementId(RealmMemoir.class));
            memoir.setDate(memoirEndDate.getTime());
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
        } else //noinspection WrongConstant
            if (calendar.get(Calendar.DAY_OF_MONTH) == calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
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
        if (countMemoir == 0) {
            neededCreateType = timeTypeToCheck;
        }
        return neededCreateType;
    }

    public DatabaseResults<RealmMemoir> getMemoirs(TimeType timeType) {
        return new DatabaseResults<>(realm.where(RealmMemoir.class)
                .equalTo("timeType", timeType.getId())
                .findAllSorted("date", Sort.DESCENDING));
    }

    public List<Plan> getPlans(TimeType timeType, int shiftPeriod) {
        List<RealmPlan> realmPlans = getDatabasePlans(timeType, shiftPeriod);
        List<Plan> planList = new ArrayList<>();
        for (RealmPlan realmPlan : realmPlans) {
            planList.add(new PlanImpl(realmPlan));
        }
        return planList;
    }

    public DatabaseResults<RealmPlan> getDatabasePlans(TimeType timeType, int shiftPeriod) {
        RealmQuery<RealmPlan> query = realm.where(RealmPlan.class).equalTo("timeType", timeType.getId());
        if (timeType == TimeType.DAY) {
            Calendar endDate = Utils.getEndDateForType(timeType, shiftPeriod);
            Calendar startDate = Utils.getStartDateForType(timeType, shiftPeriod);
            query.between("startDate", startDate.getTime(), endDate.getTime());
        }
        RealmResults<RealmPlan> realmPlans = query
                .findAllSorted("eisenhowerType", Sort.ASCENDING, "isComplete", Sort.ASCENDING);
        return new DatabaseResults<>(realmPlans);
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
        Date startUniverse = new Date(0);
        Calendar dateNow = new GregorianCalendar();

        // yesterday
        Calendar endDate = new GregorianCalendar(dateNow.get(Calendar.YEAR), dateNow.get(Calendar.MONTH), dateNow.get(Calendar.DATE));
        endDate.add(Calendar.MILLISECOND, -1);
        RealmResults<RealmPlan> dayOldPlans = getRealmPlans(TimeType.DAY, startUniverse, endDate.getTime());
        // previous week
        endDate = new GregorianCalendar(dateNow.get(Calendar.YEAR), dateNow.get(Calendar.MONTH), dateNow.get(Calendar.DATE));
        endDate.setFirstDayOfWeek(Calendar.MONDAY);
        endDate.get(Calendar.DATE);
        endDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        endDate.add(Calendar.MILLISECOND, -1);
        RealmResults<RealmPlan> weekOldPlans = getRealmPlans(TimeType.WEEK, startUniverse, endDate.getTime());
        // previous month
        endDate = new GregorianCalendar(dateNow.get(Calendar.YEAR), dateNow.get(Calendar.MONTH), 1);
        endDate.add(Calendar.MILLISECOND, -1);
        RealmResults<RealmPlan> monthOldPlans = getRealmPlans(TimeType.MONTH, startUniverse, endDate.getTime());
        // previous year
        endDate = new GregorianCalendar(dateNow.get(Calendar.YEAR), Calendar.JANUARY, 1);
        endDate.add(Calendar.MILLISECOND, -1);
        RealmResults<RealmPlan> yearOldPlans = getRealmPlans(TimeType.YEAR, startUniverse, endDate.getTime());
        if (dayOldPlans.size() > 0 || weekOldPlans.size() > 0 || monthOldPlans.size() > 0 || yearOldPlans.size() > 0) {
            realm.beginTransaction();
            Calendar newDateForPlan = new GregorianCalendar();
            if (newDateForPlan.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                newDateForPlan.add(Calendar.DATE, 1);
            }
            updateOrRemovePlan(dayOldPlans, newDateForPlan.getTime(), TimeType.WEEK);
            newDateForPlan = new GregorianCalendar();
            //noinspection WrongConstant
            if (newDateForPlan.getActualMaximum(Calendar.DATE) == newDateForPlan.get(Calendar.DATE)) {
                newDateForPlan.add(Calendar.DATE, 1);
            }
            updateOrRemovePlan(weekOldPlans, newDateForPlan.getTime(), TimeType.MONTH);
            newDateForPlan = new GregorianCalendar();
            updateOrRemovePlan(monthOldPlans, newDateForPlan.getTime(), TimeType.YEAR);
            updateOrRemovePlan(yearOldPlans, newDateForPlan.getTime(), TimeType.YEAR);
            realm.commitTransaction();
        }

    }

    private RealmResults<RealmPlan> getRealmPlans(TimeType timeType, Date startDate, Date endDate) {
        return realm.where(RealmPlan.class)
                .equalTo("timeType", timeType.getId())
                .between("startDate", startDate, endDate)
                .findAll();
    }

    private void updateOrRemovePlan(RealmResults<RealmPlan> realmPlans, Date dateNow, TimeType newTimeType) {
        int i = 0;
        for (RealmPlan plan : realmPlans) {
            if (plan.isComplete()) {
                realmPlans.deleteFromRealm(i);
            } else {
                plan.setTimeType(newTimeType);
                plan.setStartDate(dateNow);
            }
            i++;
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

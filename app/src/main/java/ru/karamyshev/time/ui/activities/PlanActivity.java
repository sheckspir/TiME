package ru.karamyshev.time.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.Calendar;

import ru.karamyshev.time.R;
import ru.karamyshev.time.core.Utils;
import ru.karamyshev.time.database.Database;
import ru.karamyshev.time.model.Plan;
import ru.karamyshev.time.model.TimeType;

public class PlanActivity extends AppCompatActivity {
    public static final String ARG_PLAN_ID = "plan_id";

    private Database database;
    private EditText planEdit;
    private RadioGroup timeTypeGroup;
    private Plan databasePlan;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        database = new Database();
        int planId = getIntent().getIntExtra(ARG_PLAN_ID, -1);
        databasePlan = database.getPlan(planId);
        if (databasePlan == null) {
            Toast.makeText(this,R.string.error_find_plan, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        planEdit = (EditText) findViewById(R.id.plan_edit);
        planEdit.setText(databasePlan.getText());
        timeTypeGroup = (RadioGroup) findViewById(R.id.time_type_group);
        switch (databasePlan.getTimeType()) {
            case DAY:
                Calendar endToday = Utils.getEndDateForType(TimeType.DAY, 0);
                if (endToday.getTime().before(databasePlan.getStartDate())){
                    timeTypeGroup.check(R.id.tomorrow_rbutton);
                } else {
                    timeTypeGroup.check(R.id.today_rbutton);
                }
                break;
            case WEEK:
                timeTypeGroup.check(R.id.week_rbutton);
                break;
            case MONTH:
                timeTypeGroup.check(R.id.month_rbutton);
                break;
            case YEAR:
                timeTypeGroup.check(R.id.year_rbutton);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_plan, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_save_plan:
                savePlan();
                finish();
                return true;
            case R.id.action_remove_plan:
                removePlan();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void savePlan() {
        TimeType timeType = databasePlan.getTimeType();
        int shift = 0;
        switch (timeTypeGroup.getCheckedRadioButtonId()) {
            case R.id.today_rbutton:
                timeType = TimeType.DAY;
                break;
            case R.id.tomorrow_rbutton:
                timeType = TimeType.DAY;
                shift = 1;
                break;
            case R.id.week_rbutton:
                timeType = TimeType.WEEK;
                break;
            case R.id.month_rbutton:
                timeType = TimeType.MONTH;
                break;
            case R.id.year_rbutton:
                timeType = TimeType.YEAR;
                break;
        }
        database.updatePlan(databasePlan, planEdit.getText().toString(), timeType, shift);
    }

    private void removePlan() {
        database.removePlan(databasePlan.getId());
    }
}

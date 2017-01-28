package ru.karamyshev.time.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import java.util.List;

import ru.karamyshev.time.R;
import ru.karamyshev.time.database.Database;
import ru.karamyshev.time.model.Plan;
import ru.karamyshev.time.model.TimeType;
import ru.karamyshev.time.ui.activities.MainActivity;

public class AlarmReceiver extends BroadcastReceiver {
    private static final int ALARM_NOTIF_ID = 58731;

    @Override
    public void onReceive(Context context, Intent intent) {
        Database database = new Database();
        TimeType timeType = database.neededCreatedTypeMemoir();
        List<Plan> planList = database.getPlans(TimeType.DAY, 1);
        int title = -1;
        int message = -1;
        if (timeType == null && planList.size() == 0) {
            title = R.string.notif_title_write_memoir_to_plan_day;
            message = R.string.notif_message_write_memoir_to_plan_day;
        } else if (timeType == null) {
            title = R.string.notif_title_write_memoir;
            message = R.string.notif_message_write_memoir;
        } else if (planList.size() == 0) {
            title = R.string.notif_title_to_plan_day;
            message = R.string.notif_message_to_plan_day;
        }

        if (title > 0) {
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.ic_menu_plan)
                            .setContentTitle(context.getString(title));
            if (message > 0) {
                builder.setContentText(context.getString(message));
            }
            Intent resultIntent = new Intent(context, MainActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            builder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(ALARM_NOTIF_ID, builder.build());
        }
    }
}

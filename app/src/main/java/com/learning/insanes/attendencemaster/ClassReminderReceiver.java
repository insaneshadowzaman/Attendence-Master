package com.learning.insanes.attendencemaster;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.learning.insanes.attendencemaster.Utils.CoursesDatabaseHelper;

public class ClassReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Integer id = intent.getIntExtra(CourseDetailActivity.COURSE_ID, -1);
        if(id != -1) {
            //Getting the total classes
            CoursesDatabaseHelper helper = new CoursesDatabaseHelper(context);
            SQLiteDatabase db = helper.getWritableDatabase();
            Cursor cursor = db.query(CoursesDatabaseHelper.TABLE_NAME,
                    new String[]{
                        CoursesDatabaseHelper.COURSES_TABLE_COLUMNS[18],
                        CoursesDatabaseHelper.COURSES_TABLE_COLUMNS[19]},
                    CoursesDatabaseHelper.COURSES_TABLE_COLUMNS[0] + " = ?",
                    new String[]{String.valueOf(id)},
                    null, null, null
            );
            Integer total = null;
            Integer present = null;
            if(cursor.moveToFirst()) {
                total = cursor.getInt(1);
                present = cursor.getInt(0);
            }
            cursor.close();
            ContentValues values = new ContentValues();
            if(total != null)
                total++;
                values.put(CoursesDatabaseHelper.COURSES_TABLE_COLUMNS[19], total);
            if(present != null)
                present++;
                values.put(CoursesDatabaseHelper.COURSES_TABLE_COLUMNS[18], present);
            db.update(CoursesDatabaseHelper.TABLE_NAME,
                    values,
                    CoursesDatabaseHelper.COURSES_TABLE_COLUMNS[0] + " = ?",
                    new String[]{String.valueOf(id)});

            // Sending out the notification
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Intent goDetailIntent = new Intent(context, CourseDetailActivity.class);
            goDetailIntent.putExtra(CourseDetailActivity.COURSE_ID, id);
            Intent absentIntent = new Intent(context, AbsentReceiverActivity.class);
            absentIntent.putExtra(CourseDetailActivity.COURSE_ID, id);
            absentIntent.putExtra(AbsentReceiverActivity.PRESENT_COUNT, present);
            Intent dismissIntent = new Intent(context, DismissReceiverActivity.class);
            dismissIntent.putExtra(CourseDetailActivity.COURSE_ID, id);
            dismissIntent.putExtra(AbsentReceiverActivity.PRESENT_COUNT, present);
            dismissIntent.putExtra(DismissReceiverActivity.TOTAL_COUNT, total);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(CourseDetailActivity.class);
            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(goDetailIntent);

            PendingIntent goDetailPendingIntent = stackBuilder.getPendingIntent(
                    id, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent absentPendingIntent = PendingIntent.getBroadcast(
                            context,
                            id,
                            absentIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(
                    context,
                    id,
                    dismissIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );

            Notification.Builder builder = new Notification.Builder(context)
                    .setContentIntent(goDetailPendingIntent)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Class Ahead!")
                    .setContentText("Your class is on next schedule")
                    .setAutoCancel(true)
                    .setStyle(new Notification.BigTextStyle().bigText("A big text"))
                    .addAction(R.mipmap.ic_launcher, "Absent", absentPendingIntent)
                    .addAction(R.mipmap.ic_launcher, "Dismissed", dismissPendingIntent);

            if (nm != null) {
                nm.notify(id, builder.build());
            }

        }
    }
}

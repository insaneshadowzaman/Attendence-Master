package com.learning.insanes.attendencemaster;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import com.learning.insanes.attendencemaster.Utils.CoursesDatabaseHelper;

public class DismissReceiverActivity extends BroadcastReceiver {
    public static final String TOTAL_COUNT = "TOTAL_COUNT";
    @Override
    public void onReceive(Context context, Intent intent) {
        Integer id = intent.getIntExtra(CourseDetailActivity.COURSE_ID, -1);
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            nm.cancel(id);
        }
        Integer total = intent.getIntExtra(TOTAL_COUNT, -1);
        Integer present = intent.getIntExtra(AbsentReceiverActivity.PRESENT_COUNT, -1);
        CoursesDatabaseHelper helper = new CoursesDatabaseHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        if(total != -1)
            values.put(CoursesDatabaseHelper.COURSES_TABLE_COLUMNS[19], total-1);
        if(present != -1)
            values.put(CoursesDatabaseHelper.COURSES_TABLE_COLUMNS[18], present-1);
        db.update(CoursesDatabaseHelper.TABLE_NAME,
                values,
                CoursesDatabaseHelper.COURSES_TABLE_COLUMNS[0] + " = ?",
                new String[]{String.valueOf(id)});
    }
}

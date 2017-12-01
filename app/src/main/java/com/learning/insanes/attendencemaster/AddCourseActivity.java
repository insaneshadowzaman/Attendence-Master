package com.learning.insanes.attendencemaster;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.learning.insanes.attendencemaster.Utils.CoursesDatabaseHelper;

import java.util.Calendar;

public class AddCourseActivity extends Activity {

    Spinner[] hourSpinners;
    Spinner[] minuteSpinners;
    EditText courseNameEditText;
    EditText teacherNameEditText;
    EditText courseCodeEditText;
    CheckBox[] checkBoxs;
    Button addCourseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        initViews();

        final Integer prevId = getIntent().getIntExtra(CourseDetailActivity.COURSE_ID, -1);
        if(prevId != -1) populateViews(prevId);

        addCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CoursesDatabaseHelper dbHelper = new CoursesDatabaseHelper(AddCourseActivity.this);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                String code = courseCodeEditText.getText().toString();
                String name = courseNameEditText.getText().toString();
                String teacher = teacherNameEditText.getText().toString();
                Integer[] hours = new Integer[7];
                Integer[] minutes = new Integer[7];

                for(int i = 0; i<7;i++) {
                    if(checkBoxs[i].isChecked()) {
                        hours[i] = hourSpinners[i].getSelectedItemPosition();
                        minutes[i] = minuteSpinners[i].getSelectedItemPosition();
                    } else {
                        hours[i] = null;
                        minutes[i] = null;
                    }
                }

                addCourseToDb(db, code, name, teacher, hours, minutes, prevId);
                Cursor cursor = db.query(CoursesDatabaseHelper.TABLE_NAME,
                        new String[]{CoursesDatabaseHelper.COURSES_TABLE_COLUMNS[0]},
                        CoursesDatabaseHelper.COURSES_TABLE_COLUMNS[1] + " = ?",
                        new String[] {code},
                        null, null, null);
                Log.d("ADDCOURSEACTIVITYCURSOR", DatabaseUtils.dumpCursorToString(cursor));

                Integer id = null;
                if(cursor.moveToFirst())
                    id = cursor.getInt(0);
                cursor.close();

                // Setting the reminder broadcaster
                Intent reminderIntent = new Intent(AddCourseActivity.this,
                        ClassReminderReceiver.class);
                if(id != null) reminderIntent.putExtra(CourseDetailActivity.COURSE_ID, id);

                PendingIntent reminderPendingIntent =
                        PendingIntent.getBroadcast(
                                AddCourseActivity.this,
                                id,
                                reminderIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );

                AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

                for(int i = 0; i <7; i++) {
                    if(hours[i] != null || minutes[i] != null) {
                        Calendar cal = Calendar.getInstance();
//                        cal.add(Calendar.SECOND, 10);
                        cal.set(Calendar.HOUR_OF_DAY, hours[i]);
                        cal.set(Calendar.MINUTE, minutes[i]);
                        cal.set(Calendar.DAY_OF_WEEK, i+1);
                        cal.set(Calendar.SECOND, 0);

                        if (am != null) {
                            am.setRepeating(AlarmManager.RTC_WAKEUP,
                                    cal.getTimeInMillis(),
                                    AlarmManager.INTERVAL_HOUR*24*7,
                                    reminderPendingIntent);
                        }
                    }
                }
                finish();
            }
        });

    }

    private void populateViews(Integer id) {
        SQLiteDatabase db = new CoursesDatabaseHelper(this).getReadableDatabase();
        String[] column = new String[18];
        for(int i = 0; i<18; i++) {
            column[i] = CoursesDatabaseHelper.COURSES_TABLE_COLUMNS[i+1];
        }
        Cursor cursor = db.query(CoursesDatabaseHelper.TABLE_NAME,
                column,
                CoursesDatabaseHelper.COURSES_TABLE_COLUMNS[0] + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null);

        if(cursor.moveToFirst()) {
            courseCodeEditText.setText(cursor.getString(0));
            courseNameEditText.setText(cursor.getString(1));
            teacherNameEditText.setText(cursor.getString(2));
            for(int i = 0; i<7; i++) {
                if(!cursor.isNull((i*2)+3)) {
                    hourSpinners[i].setSelection(cursor.getInt((i * 2) + 3));
                    minuteSpinners[i].setSelection(cursor.getInt((i * 2) + 4));
                    checkBoxs[i].setChecked(true);
                }
            }
        }

        cursor.close();
    }

    private void addCourseToDb(SQLiteDatabase db,
                               String code,
                               String name,
                               String teacher,
                               Integer[] hours,
                               Integer[] minutes,
                               Integer prevId) {
        ContentValues courseValues = new ContentValues();
        courseValues.put(CoursesDatabaseHelper.COURSES_TABLE_COLUMNS[1], code);
        courseValues.put(CoursesDatabaseHelper.COURSES_TABLE_COLUMNS[2], name);
        courseValues.put(CoursesDatabaseHelper.COURSES_TABLE_COLUMNS[3], teacher);
        for(int i = 0; i<7; i++) {
            if(hours[i] != null || minutes[i] != null) {
                courseValues.put(CoursesDatabaseHelper.COURSES_TABLE_COLUMNS[(i*2)+4], hours[i]);
                courseValues.put(CoursesDatabaseHelper.COURSES_TABLE_COLUMNS[(i*2)+5], minutes[i]);
            }
        }
        courseValues.put(CoursesDatabaseHelper.COURSES_TABLE_COLUMNS[18], 0);
        courseValues.put(CoursesDatabaseHelper.COURSES_TABLE_COLUMNS[19], 0);
        if(prevId == -1)
            db.insert(CoursesDatabaseHelper.TABLE_NAME, null, courseValues);
        else {
            db.update(CoursesDatabaseHelper.TABLE_NAME, courseValues,
                    CoursesDatabaseHelper.COURSES_TABLE_COLUMNS[0] + " = ?",
                    new String[]{String.valueOf(prevId)});

        }
    }

    private void initViews() {
        courseCodeEditText = findViewById(R.id.edittext_course_code);
        courseNameEditText = findViewById(R.id.edittext_course_name);
        teacherNameEditText = findViewById(R.id.edittext_teachers_name);
        addCourseButton = findViewById(R.id.button_submit_course);

        hourSpinners = new Spinner[7];
        minuteSpinners = new Spinner[7];
        hourSpinners[0] = findViewById(R.id.spinner_sunday_hour);
        hourSpinners[1] = findViewById(R.id.spinner_monday_hour);
        hourSpinners[2] = findViewById(R.id.spinner_tuesday_hour);
        hourSpinners[3] = findViewById(R.id.spinner_wednesday_hour);
        hourSpinners[4] = findViewById(R.id.spinner_thursday_hour);
        hourSpinners[5] = findViewById(R.id.spinner_friday_hour);
        hourSpinners[6] = findViewById(R.id.spinner_saturday_hour);
        minuteSpinners[0] = findViewById(R.id.spinner_sunday_minute);
        minuteSpinners[1] = findViewById(R.id.spinner_monday_minute);
        minuteSpinners[2] = findViewById(R.id.spinner_tuesday_minute);
        minuteSpinners[3] = findViewById(R.id.spinner_wednesday_minute);
        minuteSpinners[4] = findViewById(R.id.spinner_thursday_minute);
        minuteSpinners[5] = findViewById(R.id.spinner_friday_minute);
        minuteSpinners[6] = findViewById(R.id.spinner_saturday_minute);

        checkBoxs = new CheckBox[7];
        checkBoxs[0] = findViewById(R.id.checkbox_sunday);
        checkBoxs[1] = findViewById(R.id.checkbox_monday);
        checkBoxs[2] = findViewById(R.id.checkbox_tuesday);
        checkBoxs[3] = findViewById(R.id.checkbox_wednesday);
        checkBoxs[4] = findViewById(R.id.checkbox_thursday);
        checkBoxs[5] = findViewById(R.id.checkbox_friday);
        checkBoxs[6] = findViewById(R.id.checkbox_saturday);
    }
}

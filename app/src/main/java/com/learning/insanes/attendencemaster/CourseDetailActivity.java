package com.learning.insanes.attendencemaster;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.learning.insanes.attendencemaster.Utils.CoursesDatabaseHelper;

public class CourseDetailActivity extends Activity {

    public static final String COURSE_ID = "COURSE_ID";
//    public static final Integer NOTIFICATION_REQUEST_CODE = 765;
    TextView nameView, codeView, attendenceView, attendedView, missedView;
    Button editButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        final Integer id = getIntent().getIntExtra(COURSE_ID, -1);
        initView();
        if(id != -1) populateViews(id);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CourseDetailActivity.this, AddCourseActivity.class);
                intent.putExtra(COURSE_ID, id);
                startActivity(intent);
                finish();
            }
        });

    }
    private void populateViews(final Integer id) {
        SQLiteDatabase db = new CoursesDatabaseHelper(this).getReadableDatabase();
        Cursor cursor = db.query(CoursesDatabaseHelper.TABLE_NAME,
                new String[]{CoursesDatabaseHelper.COURSES_TABLE_COLUMNS[1],
                        CoursesDatabaseHelper.COURSES_TABLE_COLUMNS[2],
                        CoursesDatabaseHelper.COURSES_TABLE_COLUMNS[18],
                        CoursesDatabaseHelper.COURSES_TABLE_COLUMNS[19]},
                CoursesDatabaseHelper.COURSES_TABLE_COLUMNS[0] + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null);
        if(cursor.moveToFirst()) {
            String code = cursor.getString(0);
            String name = cursor.getString(1);
            Integer attended = cursor.getInt(2);
            Integer total = cursor.getInt(2);

            nameView.setText(name);
            codeView.setText(code);
            missedView.setText(String.valueOf(total-attended));
            String attendedText = attended + "/" + total;
            attendedView.setText(attendedText);
            String attendenceText = String.valueOf(Double.valueOf(attended)/Double.valueOf(total));
            attendenceView.setText(attendenceText);
        }

        cursor.close();
    }

    private void initView() {
        nameView = findViewById(R.id.detail_name);
        codeView = findViewById(R.id.detail_code);
        attendedView = findViewById(R.id.detail_attended_ratio);
        attendenceView = findViewById(R.id.detail_percent);
        missedView = findViewById(R.id.detail_missed);
        editButton = findViewById(R.id.detail_edit_button);
    }
}

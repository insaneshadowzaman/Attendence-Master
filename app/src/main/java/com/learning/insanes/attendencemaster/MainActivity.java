package com.learning.insanes.attendencemaster;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toolbar;

import com.idescout.sql.SqlScoutServer;
import com.learning.insanes.attendencemaster.Utils.CoursesCursorRecyclerViewAdapter;
import com.learning.insanes.attendencemaster.Utils.CoursesDatabaseHelper;

public class MainActivity extends Activity {

    RecyclerView recyclerView;
    Button addCourseButton;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        SqlScoutServer.create(this, getPackageName());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setActionBar((Toolbar) findViewById(R.id.toolbar));

    }

    @Override
    protected void onResume() {
        super.onResume();
        CoursesDatabaseHelper dbHelper = new CoursesDatabaseHelper(this);
        db = dbHelper.getReadableDatabase();
        // DO NOT CLOSE THIS CURSOR
        Cursor cursor = db.query(CoursesDatabaseHelper.TABLE_NAME, new String[] {CoursesDatabaseHelper.COURSES_TABLE_COLUMNS[0],
                CoursesDatabaseHelper.COURSES_TABLE_COLUMNS[1], CoursesDatabaseHelper.COURSES_TABLE_COLUMNS[18], CoursesDatabaseHelper.COURSES_TABLE_COLUMNS[19]
        }, null, null, null, null, CoursesDatabaseHelper.COURSES_TABLE_COLUMNS[2]);

        addCourseButton = findViewById(R.id.button_add_course);
        addCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddCourseActivity.class);
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.recyclerview_mainactivity);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        CoursesCursorRecyclerViewAdapter adapter = new CoursesCursorRecyclerViewAdapter(cursor);
        recyclerView.setAdapter(adapter);

    }
}

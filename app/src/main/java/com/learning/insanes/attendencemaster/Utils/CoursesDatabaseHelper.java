package com.learning.insanes.attendencemaster.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CoursesDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "courses";
    private static final Integer DB_VERSION = 1;

    public static final String TABLE_NAME = "COURSES";
    public static final String[] COURSES_TABLE_COLUMNS = {
            // Table name = COURSES
            "_id",              // 0 INTEGER
            "CODE",             // 1 STRING
            "NAME",             // 2 STRING
            "TEACHER_NAME",     // 3 STRING

            "SUNDAY_HOUR",      // 4 INTEGER
            "SUNDAY_MINUTE",    // 5 INTEGER
            "MONDAY_HOUR",      // 6 INTEGER
            "MONDAY_MINUTE",    // 7 INTEGER
            "TUESDAY_HOUR",     // 8 INTEGER
            "TUESDAY_MINUTE",   // 9 INTEGER
            "WEDNESDAY_HOUR",   // 10 INTEGER
            "WEDNESDAY_MINUTE", // 11 INTEGER
            "THURSDAY_HOUR",    // 12 INTEGER
            "THURSDAY_MINUTE",  // 13 INTEGER
            "FRIDAY_HOUR",      // 14 INTEGER
            "FRIDAY_MINUTE",    // 15 INTEGER
            "SATURDAY_HOUR",    // 16 INTEGER
            "SATURDAY_MINUTE",  // 17 INTEGER

            "ATTENDED",         // 18 INTEGER
            "TOTAL"             // 19 INTEGER
    };

    public CoursesDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "CODE TEXT, " +
                "NAME TEXT, " +
                "TEACHER_NAME TEXT, " +

                "SUNDAY_HOUR INTEGER, " +
                "SUNDAY_MINUTE INTEGER, " +
                "MONDAY_HOUR INTEGER, " +
                "MONDAY_MINUTE INTEGER, " +
                "TUESDAY_HOUR INTEGER, " +
                "TUESDAY_MINUTE INTEGER, " +
                "WEDNESDAY_HOUR INTEGER, " +
                "WEDNESDAY_MINUTE INTEGER, " +
                "THURSDAY_HOUR INTEGER, " +
                "THURSDAY_MINUTE INTEGER, " +
                "FRIDAY_HOUR INTEGER, " +
                "FRIDAY_MINUTE INTEGER, " +
                "SATURDAY_HOUR INTEGER, " +
                "SATURDAY_MINUTE INTEGER, " +
                "ATTENDED INTEGER, " +
                "TOTAL INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}

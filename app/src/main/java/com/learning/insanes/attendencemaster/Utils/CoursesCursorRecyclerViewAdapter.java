package com.learning.insanes.attendencemaster.Utils;

import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.learning.insanes.attendencemaster.CourseDetailActivity;
import com.learning.insanes.attendencemaster.MainActivity;
import com.learning.insanes.attendencemaster.R;

import java.util.zip.Inflater;

public class CoursesCursorRecyclerViewAdapter
        extends RecyclerView.Adapter<CoursesCursorRecyclerViewAdapter.CourseViewHolder> {

    private Cursor cursor;
    private static final Integer COURSE_ID = 0;
    private static final Integer COURSE_CODE = 1;
    private static final Integer COURSE_ATTENDED = 2;
    private static final Integer COURSE_TOTAL = 3;

    public CoursesCursorRecyclerViewAdapter(Cursor cursor) {
        this.cursor = cursor;
    }

    @Override
    public CourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View myView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.courses_short_view, parent, false);
        return new CourseViewHolder(myView);
    }

    @Override
    public void onBindViewHolder(CourseViewHolder view, int position) {
        if(cursor.moveToPosition(position)) {
            Log.d("RECYCLERVIEW", DatabaseUtils.dumpCursorToString(cursor));
            final Integer id = cursor.getInt(0);
            String code = cursor.getString(COURSE_CODE);
            Integer attended = cursor.getInt(COURSE_ATTENDED);
            Integer total = cursor.getInt(COURSE_TOTAL);
            view.setCode(code);
            view.setPercent(attended, total);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), CourseDetailActivity.class);
                    intent.putExtra(CourseDetailActivity.COURSE_ID, id);
                    view.getContext().startActivity(intent);
                }
            });

        } else {
            view.setCode("Not found");
            view.setPercent(0, 100);
        }
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    class CourseViewHolder extends RecyclerView.ViewHolder {
        private TextView code;
        private TextView percent;
        CourseViewHolder(View itemView) {
            super(itemView);
            code = itemView.findViewById(R.id.course_view_course_code);
            percent = itemView.findViewById(R.id.course_view_attendence_percent);
        }
        void setCode(String string) {
            code.setText(string);
        }
        void setPercent(Integer attended, Integer total) {
            if(total == 0) {
                percent.setText("0%");
                return;
            }
            Double d = Double.valueOf(attended)/Double.valueOf(total);
            d *= 100d;
            String text = String.valueOf(d) + "%";
            percent.setText(text);
        }
        void setOnClickListener(View.OnClickListener listener) {
            code.setOnClickListener(listener);
            percent.setOnClickListener(listener);
        }
    }
}

package com.csampog.termmanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.csampog.termmanager.R;
import com.csampog.termmanager.dataAccess.repositories.CourseRepository;
import com.csampog.termmanager.messaging.interfaces.CourseSelectedListener;
import com.csampog.termmanager.model.Course;

import java.text.SimpleDateFormat;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CourseAdapter extends EntityAdapter<Course, CourseAdapter.ViewHolder> {

    private CourseSelectedListener courseSelectedListener;

    public CourseAdapter(@NonNull Context context, int resource, @NonNull List<Course> courses) {
        super(context, resource, courses);
    }

    @Override
    protected void bindEntity(Course course, ViewHolder viewHolder) {
        SimpleDateFormat df = new SimpleDateFormat("MMM d, yyyy");
        viewHolder.titleTextView.setText(course.getTitle());
        viewHolder.startTextView.setText(df.format(course.getStartDate()));
        viewHolder.endTextView.setText(df.format(course.getAnticipatedEndDate()));
        viewHolder.statusTextView.setText(course.getStatus());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CourseAdapter.this.courseSelectedListener != null) {
                    courseSelectedListener.courseSelected(course);
                }
            }
        });


        viewHolder.menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(viewHolder.itemView.getContext(), v);
                popupMenu.inflate(R.menu.course_list_item_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.course_action_remove) {
                            course.setTermId(0);
                            CourseRepository.getInstance(v.getContext()).insertOrUpdate(course);
                        } else {
                            CourseRepository.getInstance(v.getContext()).delete(course);
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.course_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return entities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView titleTextView;
        public TextView startTextView;
        public TextView endTextView;
        public TextView statusTextView;
        public TextView menuButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.course_list_item_title);
            startTextView = itemView.findViewById(R.id.course_list_item_start);
            endTextView = itemView.findViewById(R.id.course_list_item_end);
            statusTextView = itemView.findViewById(R.id.course_list_item_status);
            menuButton = itemView.findViewById(R.id.course_list_item_option);
        }
    }


    public void setCourseSelectedListener(CourseSelectedListener listener) {

        courseSelectedListener = listener;
    }
}

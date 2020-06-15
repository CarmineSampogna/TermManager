package com.csampog.termmanager;

import android.content.Intent;
import android.os.Bundle;

import com.csampog.termmanager.adapters.CourseAdapter;
import com.csampog.termmanager.messaging.CoursesFilter;
import com.csampog.termmanager.messaging.interfaces.CourseSelectedListener;
import com.csampog.termmanager.model.Course;
import com.csampog.termmanager.viewmodels.AllCoursesViewModel;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AllCoursesActivity extends AppCompatActivity {


    private AllCoursesViewModel viewModel;
    private List<Course> courses;
    private CourseAdapter adapter;
    private CoursesFilter coursesFilter = CoursesFilter.ALL;
    private RecyclerView coursesRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_courses);

        Intent intent = getIntent();

        CollapsingToolbarLayout toolbarLayout = findViewById(R.id.coursesToolbar);
        toolbarLayout.setTitle(getString(R.string.courses_title));

        coursesRecyclerView = findViewById(R.id.all_courses_recycler);
        coursesRecyclerView.setLayoutManager(new GridLayoutManager(this, getResources().getInteger(R.integer.grid_layout_columns)));

        courses = new ArrayList<>();
        adapter = new CourseAdapter(this, R.layout.course_list_item, courses);
        adapter.setCourseSelectedListener(new CourseSelectedListener() {
            @Override
            public void courseSelected(Course course) {
                Intent intent = new Intent(AllCoursesActivity.this, CourseDetailsActivity.class);
                intent.putExtra(CourseDetailsActivity.COURSE_ID_KEY, course.getCourseId());
                startActivity(intent);
            }
        });

        coursesRecyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(getViewModelStore(), new ViewModelProvider.AndroidViewModelFactory(getApplication()))
                .get(AllCoursesViewModel.class);


        final Observer<List<Course>> courseObserver = courses -> {
            AllCoursesActivity.this.courses.clear();
            AllCoursesActivity.this.courses.addAll(courses);
            if (AllCoursesActivity.this.adapter == null) {
                adapter = new CourseAdapter(AllCoursesActivity.this, R.layout.course_list_item, courses);
                coursesRecyclerView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
        };

        viewModel.refreshCourses(coursesFilter);
        viewModel.courses.observe(this, courseObserver);
    }

}
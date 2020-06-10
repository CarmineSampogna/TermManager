package com.csampog.termmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.csampog.termmanager.adapters.CourseAdapter;
import com.csampog.termmanager.messaging.CoursesFilter;
import com.csampog.termmanager.model.Course;
import com.csampog.termmanager.viewmodels.AllCoursesViewModel;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AddCourseToTermActivity extends AppCompatActivity {

    public static final String TERM_ID_PARAM = "termId";

    private AllCoursesViewModel viewModel;
    private List<Course> courses;
    private CourseAdapter adapter;
    private CoursesFilter coursesFilter = CoursesFilter.TERMLESS;
    private RecyclerView coursesRecyclerView;
    private FloatingActionButton addNewButton;
    private int termId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course_to_term);

        parseTermId();

        initToolbar();

        addNewButton = findViewById(R.id.courseTerm_add_new);
        addNewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddCourseToTermActivity.this, AddNewCourseActivity.class);
                intent.putExtra(AddCourseToTermActivity.TERM_ID_PARAM, termId);
                startActivity(intent);
            }
        });

        initRecyclerView();

        initAdapter();

        viewModel = new ViewModelProvider(getViewModelStore(), new ViewModelProvider.AndroidViewModelFactory(getApplication()))
                .get(AllCoursesViewModel.class);

        final Observer<List<Course>> courseObserver = courses -> {
            AddCourseToTermActivity.this.courses.clear();
            AddCourseToTermActivity.this.courses.addAll(courses);
            if (AddCourseToTermActivity.this.adapter == null) {
                adapter = new CourseAdapter(AddCourseToTermActivity.this, R.layout.course_list_item, courses);
                coursesRecyclerView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
        };

        viewModel.refreshCourses(coursesFilter);
        viewModel.courses.observe(this, courseObserver);
    }

    private void initAdapter() {
        courses = new ArrayList<>();
        adapter = new CourseAdapter(this, R.layout.course_list_item, courses);
        adapter.setCourseSelectedListener(course -> {

            course.setTermId(termId);
            viewModel.updateCourse(course);
            finish();
        });
        coursesRecyclerView.setAdapter(adapter);
    }

    private void initRecyclerView() {
        coursesRecyclerView = findViewById(R.id.courseTerm_recycler);
        coursesRecyclerView.setLayoutManager(new GridLayoutManager(this, getResources().getInteger(R.integer.grid_layout_columns)));
    }

    private void initToolbar() {
        CollapsingToolbarLayout toolbarLayout = findViewById(R.id.courseTerm_toolbar);
        toolbarLayout.setTitle(getString(R.string.courseTerm_title));
    }

    private void parseTermId() {
        Intent intent = getIntent();
        if (intent.hasExtra(TERM_ID_PARAM)) {
            termId = intent.getIntExtra(TERM_ID_PARAM, 0);
        }

        if (termId == 0) {
            finish();
        }
    }
}
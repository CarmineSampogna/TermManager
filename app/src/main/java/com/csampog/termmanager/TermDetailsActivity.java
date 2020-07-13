package com.csampog.termmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.csampog.termmanager.adapters.CourseAdapter;
import com.csampog.termmanager.messaging.interfaces.CourseSelectedListener;
import com.csampog.termmanager.model.Course;
import com.csampog.termmanager.viewmodels.TermDetailsViewModel;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class TermDetailsActivity extends AppCompatActivity {

    public static final String TERM_ID_KEY = "term_id";

    private TermDetailsViewModel viewModel;
    private List<Course> termCourses;
    private boolean coursesAvailable = false;

    private CollapsingToolbarLayout toolbarLayout;
    private Toolbar toolbar;
    private TextView startDateText;
    private TextView endDateText;
    private RecyclerView termCoursesRecyclerView;
    private CourseAdapter courseAdapter;
    private TextInputEditText titleEditText;
    private MaterialButton addCourseButton;
    private FloatingActionButton editButton;
    private int termId;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.term_details_menu, menu);
        return true;
    }

    private void navToEdit() {
        Intent i = new Intent(this, EditTermActivity.class);
        i.putExtra(EditTermActivity.TERM_ID_PARAM, termId);
        startActivity(i);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.term_delete_button) {

            if (viewModel.hasCoursesAssigned()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.term_courses_error);
                builder.setNeutralButton(R.string.ok_button_text, (dialog, which) -> dialog.dismiss());
                builder.show();
            } else {
                viewModel.deleteTerm();
                termId = 0;
                finish();
            }
        } else {
            finish();
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_details);

        initViews();
        initViewModel();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(getViewModelStore(), new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(TermDetailsViewModel.class);
        Intent intent = getIntent();

        termId = 0;

        if (intent.hasExtra(TERM_ID_KEY)) {
            termId = intent.getIntExtra(TERM_ID_KEY, 0);
            if (termId > 0) {
                viewModel.refreshTermDetails(termId);
            }
        }

        if (termId == 0) {
            finish();
        }

        final Observer<List<Course>> termCourseObserver = courses -> {
            termCourses.clear();
            termCourses.addAll(courses);

            if (courseAdapter == null) {
                courseAdapter = new CourseAdapter(TermDetailsActivity.this, R.layout.course_list_item, termCourses);
                termCoursesRecyclerView.setAdapter(courseAdapter);
            } else {

                courseAdapter.notifyDataSetChanged();
            }
        };

        final Observer<String> titleObserver = s -> {

            toolbarLayout.setTitle(s);
            titleEditText.setText(s);
        };

        final Observer<String> startDateObserver = s -> startDateText.setText(s);

        final Observer<String> endDateObserver = s -> endDateText.setText(s);

        final Observer<Boolean> coursesAvailableObserver = b -> coursesAvailable = b;

        viewModel.termCourses.observe(this, termCourseObserver);
        viewModel.title.observe(this, titleObserver);
        viewModel.formattedStartDate.observe(this, startDateObserver);
        viewModel.formattedEndDate.observe(this, endDateObserver);
        viewModel.hasCoursesAvailableToAdd.observe(this, coursesAvailableObserver);
        viewModel.updateHasCoursesAvailable();
    }

    private void initViews() {
        toolbarLayout = findViewById(R.id.term_details_toolbar_layout);
        toolbar = findViewById(R.id.term_details_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        editButton = findViewById(R.id.term_details_edit_button);
        editButton.setOnClickListener(v -> navToEdit());
        titleEditText = findViewById(R.id.term_title_text);
        startDateText = findViewById(R.id.term_details_start);
        endDateText = findViewById(R.id.term_details_end);
        addCourseButton = findViewById(R.id.add_course_button);
        addCourseButton.setOnClickListener(v -> {

            viewModel.updateHasCoursesAvailable();
            Intent intent = null;
            if (!this.coursesAvailable) {

                intent = new Intent(TermDetailsActivity.this, AddNewCourseActivity.class);
                intent.putExtra(AddNewCourseActivity.TERM_ID_PARAM, viewModel.getTermId());
            } else {

                intent = new Intent(TermDetailsActivity.this, AddCourseToTermActivity.class);
                intent.putExtra(AddCourseToTermActivity.TERM_ID_PARAM, viewModel.getTermId());
            }
            startActivity(intent);
        });

        initRecyclerView();
    }

    private void initRecyclerView() {

        termCourses = new ArrayList<>();
        termCoursesRecyclerView = findViewById(R.id.term_courses_recyclerView);
        termCoursesRecyclerView.setLayoutManager(new GridLayoutManager(this, getResources().getInteger(R.integer.grid_layout_columns)));
        courseAdapter = new CourseAdapter(this, R.layout.course_list_item, termCourses);
        courseAdapter.setCourseSelectedListener(new CourseSelectedListener() {
            @Override
            public void courseSelected(Course course) {
                Intent intent = new Intent(TermDetailsActivity.this, CourseDetailsActivity.class);
                intent.putExtra(CourseDetailsActivity.COURSE_ID_KEY, course.getCourseId());
                startActivity(intent);
            }
        });
        termCoursesRecyclerView.setAdapter(courseAdapter);
    }
}
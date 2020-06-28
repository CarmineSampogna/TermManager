package com.csampog.termmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.csampog.termmanager.adapters.CourseDetailsPagerAdapter;
import com.csampog.termmanager.model.Assessment;
import com.csampog.termmanager.model.Note;
import com.csampog.termmanager.viewmodels.CourseDetailsViewModel;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

public class CourseDetailsActivity extends AppCompatActivity {

    public static final String COURSE_ID_KEY = "courseId";

    private CourseDetailsViewModel viewModel;
    private List<Assessment> courseAssessments;
    private List<Note> courseNotes;

    private CollapsingToolbarLayout toolbarLayout;
    private Toolbar courseDetailsToolbar;
    private TextView startDateText;
    private TextView endDateText;
    private TextInputLayout statusInputLayout;
    private TextInputEditText statusTextInput;
    private TextView statusTextView;
    private TextInputEditText mentorNameTextView;
    private TextInputEditText mentorEmailTextView;
    private TextInputEditText mentorPhoneTextView;
    private TextInputEditText titleEditText;
    private TabLayout courseTabLayout;
    private ViewPager2 courseViewPager;

    private int courseId = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);

        initViewModel();
        initViews();
        initObservers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.course_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.course_details_edit) {
           // item.setVisible(false);
           // statusInputLayout.setVisibility(View.VISIBLE);
            //statusTextView.setVisibility(View.INVISIBLE);
            Intent intent = new Intent(this, EditCourseActivity.class);
            intent.putExtra(EditCourseActivity.COURSE_ID_PARAM, courseId);
            startActivity(intent);
        }
        return true;
    }

    private void initViewPager() {

        courseViewPager.setAdapter(new CourseDetailsPagerAdapter(courseId, viewModel, this));
        TabLayoutMediator mediator = new TabLayoutMediator(courseTabLayout, courseViewPager, (tab, position) -> {

            switch (position) {
                case 0:
                    tab.setText(getResources().getString(R.string.assessments_title));
                    break;
                case 1:
                    tab.setText(R.string.notes_title);
                    break;
            }
        });

        mediator.attach();
    }

    private void initObservers() {

        final Observer<String> titleObserver = s -> {

            toolbarLayout.setTitle(s);
            //titleEditText.setText(s);
        };

        final Observer<String> startDateObserver = s -> startDateText.setText(s);
        final Observer<String> endDateObserver = s -> endDateText.setText(s);
        final Observer<String> statusObserver = s -> {
            //statusTextInput.setText(s);
            statusTextView.setText(s);
        };
        final Observer<String> mentorNameObserver = s -> mentorNameTextView.setText(s);
        final Observer<String> mentorPhoneObserver = s -> mentorPhoneTextView.setText(s);
        final Observer<String> mentorEmailObserver = s -> mentorEmailTextView.setText(s);

        viewModel.title.observe(this, titleObserver);
        viewModel.formattedStartDate.observe(this, startDateObserver);
        viewModel.formattedEndDate.observe(this, endDateObserver);
        viewModel.status.observe(this, statusObserver);
        viewModel.mentorName.observe(this, mentorNameObserver);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(getViewModelStore(), new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(CourseDetailsViewModel.class);
        Intent intent = getIntent();

        courseId = 0;
        if (intent.hasExtra(COURSE_ID_KEY)) {
            courseId = intent.getIntExtra(COURSE_ID_KEY, 0);
            if (courseId > 0) {
                viewModel.refreshCourseDetails(courseId);
            }
        } else {
            //Show error about loading course details
        }

    }

    private void initViews() {
        toolbarLayout = findViewById(R.id.course_details_toolbar_layout);
        courseDetailsToolbar = findViewById(R.id.course_details_toolbar);
        setSupportActionBar(courseDetailsToolbar);
        titleEditText = findViewById(R.id.course_title_text);
        startDateText = findViewById(R.id.course_details_start);
        endDateText = findViewById(R.id.course_details_end);
        //statusInputLayout = findViewById(R.id.course_details_status_editText_layout);
        //statusTextInput = findViewById(R.id.course_details_status_editText);
        statusTextView = findViewById(R.id.course_details_status_textView);
        mentorNameTextView = findViewById(R.id.course_details_mentorName_text);
        mentorPhoneTextView = findViewById(R.id.course_details_mentorPhone_text);
        mentorEmailTextView = findViewById(R.id.course_details_mentorEmail_text);
        courseTabLayout = findViewById(R.id.courseTabLayout);
        courseViewPager = findViewById(R.id.courseViewPager);
        initViewPager();
    }
}
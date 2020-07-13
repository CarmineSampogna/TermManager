package com.csampog.termmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.csampog.termmanager.adapters.CourseDetailsPagerAdapter;
import com.csampog.termmanager.model.Assessment;
import com.csampog.termmanager.model.Note;
import com.csampog.termmanager.viewmodels.CourseDetailsViewModel;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class CourseDetailsActivity extends AppCompatActivity {

    public static final String COURSE_ID_KEY = "courseId";

    private CourseDetailsViewModel viewModel;
    private List<Assessment> courseAssessments;
    private List<Note> courseNotes;

    private CollapsingToolbarLayout toolbarLayout;
    private Toolbar courseDetailsToolbar;
    private TextView titleTextView;
    private TextView startDateText;
    private TextView endDateText;
    private TextInputLayout statusInputLayout;
    private TextInputEditText statusTextInput;
    private TextView statusTextView;
    private TextView alertsEnabledTextView;
    private LinearLayout noMentorInfoLayout;
    private LinearLayout mentorInfoLayout;
    private TextInputLayout mentorNameLayout;
    private TextView mentorNameTextView;
    private TextInputLayout mentorEmailLayout;
    private TextView mentorEmailTextView;
    private TextInputLayout mentorPhoneLayout;
    private TextView mentorPhoneTextView;
    private TextInputEditText titleEditText;
    private TabLayout courseTabLayout;
    private ViewPager2 courseViewPager;
    private FloatingActionButton editButton;

    private int courseId = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);


        Toolbar toolbar = findViewById(R.id.course_details_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        if (item.getItemId() == R.id.course_delete_button) {
            viewModel.deleteCourse();
        }
        finish();
        return true;
    }

    private void initViewPager() {

        courseViewPager.setAdapter(new CourseDetailsPagerAdapter(courseId, viewModel, this));
        TabLayoutMediator mediator = new TabLayoutMediator(courseTabLayout, courseViewPager, (tab, position) -> {

            switch (position) {
                case 0:
                    tab.setText(getResources().getString(R.string.assessments_title));
                    tab.setIcon(R.drawable.ic_baseline_assignment_turned_in_24);
                    break;
                case 1:
                    tab.setText(R.string.notes_title);
                    tab.setIcon(R.drawable.ic_baseline_notes_24);
                    break;
            }
        });

        mediator.attach();
    }

    private void initObservers() {

        final Observer<String> titleObserver = s -> {

            toolbarLayout.setTitle(s);
            titleTextView.setText(s);
        };
        final Observer<String> startDateObserver = s -> startDateText.setText(s);
        final Observer<String> endDateObserver = s -> endDateText.setText(s);
        final Observer<String> statusObserver = s -> {

            statusTextView.setText(s);
        };

        final Observer<Boolean> hasMentorInfoObserver = hasInfo -> {
            if (hasInfo == null || hasInfo) {
                noMentorInfoLayout.setVisibility(View.GONE);
                mentorInfoLayout.setVisibility(View.VISIBLE);
            } else {
                noMentorInfoLayout.setVisibility(View.VISIBLE);
                mentorInfoLayout.setVisibility(View.GONE);
            }
        };

        final Observer<Boolean> alertsEnabledObserver = alertsEnabled -> {
            boolean enabled = alertsEnabled != null && alertsEnabled;
            int textRes = 0;
            int iconRes = 0;

            textRes = enabled ? R.string.alerts_on : R.string.alerts_off;
            iconRes = enabled ? R.drawable.ic_baseline_notifications_24 : R.drawable.ic_baseline_notifications_off_24;

            alertsEnabledTextView.setText(getString(textRes));
            alertsEnabledTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(iconRes, 0, 0, 0);
        };

        final Observer<String> mentorNameObserver = s -> {

            mentorNameTextView.setText(s);
            int visibility = s != null && !s.isEmpty() ?
                    View.VISIBLE : View.GONE;

            mentorNameTextView.setVisibility(visibility);
        };

        final Observer<String> mentorPhoneObserver = s -> {
            mentorPhoneTextView.setText(s);

            int visibility = s != null && !s.isEmpty() ?
                    View.VISIBLE : View.GONE;

            mentorPhoneTextView.setVisibility(visibility);
        };

        final Observer<String> mentorEmailObserver = s -> {
            mentorEmailTextView.setText(s);

            int visibility = s != null && !s.isEmpty() ?
                    View.VISIBLE : View.GONE;

            mentorEmailTextView.setVisibility(visibility);
        };

        viewModel.title.observe(this, titleObserver);
        viewModel.formattedStartDate.observe(this, startDateObserver);
        viewModel.formattedEndDate.observe(this, endDateObserver);
        viewModel.status.observe(this, statusObserver);
        viewModel.alertsEnabled.observe(this, alertsEnabledObserver);
        viewModel.mentorName.observe(this, mentorNameObserver);
        viewModel.mentorEmail.observe(this, mentorEmailObserver);
        viewModel.mentorPhone.observe(this, mentorPhoneObserver);
        viewModel.hasMentorInfo.observe(this, hasMentorInfoObserver);
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
        }
    }

    private void initViews() {
        toolbarLayout = findViewById(R.id.course_details_toolbar_layout);
        courseDetailsToolbar = findViewById(R.id.course_details_toolbar);
        setSupportActionBar(courseDetailsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        editButton = findViewById(R.id.course_details_edit_button);
        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditCourseActivity.class);
            intent.putExtra(EditCourseActivity.COURSE_ID_PARAM, courseId);
            startActivity(intent);
        });
        titleEditText = findViewById(R.id.course_title_text);
        titleTextView = findViewById(R.id.course_details_title_textView);
        startDateText = findViewById(R.id.course_details_start);
        endDateText = findViewById(R.id.course_details_end);
        statusTextView = findViewById(R.id.course_details_status_textView);
        alertsEnabledTextView = findViewById(R.id.course_details_alertsEnabled_textView);
        noMentorInfoLayout = findViewById(R.id.noMentorInfo_layout);
        mentorInfoLayout = findViewById(R.id.mentorInfo_layout);
        mentorNameTextView = findViewById(R.id.course_details_mentorName_text);
        mentorPhoneTextView = findViewById(R.id.course_details_mentorPhone_text);
        mentorEmailTextView = findViewById(R.id.course_details_mentorEmail_text);
        courseTabLayout = findViewById(R.id.courseTabLayout);
        courseViewPager = findViewById(R.id.courseViewPager);
        initViewPager();
    }
}
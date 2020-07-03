package com.csampog.termmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
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
import com.google.android.material.textview.MaterialTextView;

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

            statusTextView.setText(s);
        };

        final Observer<Boolean> hasMentorInfoObserver = hasInfo ->{
          if(hasInfo){
              noMentorInfoLayout.setVisibility(View.GONE);
              mentorInfoLayout.setVisibility(View.VISIBLE);
          }else{
              noMentorInfoLayout.setVisibility(View.VISIBLE);
              mentorInfoLayout.setVisibility(View.GONE);
          }
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
        noMentorInfoLayout = findViewById(R.id.noMentorInfo_layout);
        mentorInfoLayout = findViewById(R.id.mentorInfo_layout);
        //mentorNameLayout = findViewById(R.id.course_mentorName_layout);
        mentorNameTextView = findViewById(R.id.course_details_mentorName_text);
        //mentorPhoneLayout = findViewById(R.id.course_mentorPhone_layout);
        mentorPhoneTextView = findViewById(R.id.course_details_mentorPhone_text);
        //mentorEmailLayout = findViewById(R.id.course_mentorEmail_layout);
        mentorEmailTextView = findViewById(R.id.course_details_mentorEmail_text);
        courseTabLayout = findViewById(R.id.courseTabLayout);
        courseViewPager = findViewById(R.id.courseViewPager);
        initViewPager();
    }
}
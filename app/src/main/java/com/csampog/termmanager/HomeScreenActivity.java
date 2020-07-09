package com.csampog.termmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.csampog.termmanager.dataAccess.repositories.CourseRepository;
import com.csampog.termmanager.model.Assessment;
import com.csampog.termmanager.model.Course;
import com.csampog.termmanager.model.Term;
import com.csampog.termmanager.viewmodels.HomeScreenViewModel;

import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class HomeScreenActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private HomeScreenViewModel viewModel;

    private Term latestTerm;
    private View latestTermView;
    private FrameLayout latestTermFrame;
    private LinearLayout noLatestTermLayout;
    private Button viewAllTermsButton;
    private Button addTermButton;

    private Course currentCourse;
    private View currentCourseView;
    private FrameLayout currentCourseFrame;
    private LinearLayout noCurrentCourseLayout;
    private Button viewAllCoursesButton;
    private Button addCourseButton;

    private Assessment nextAssessment;
    private View nextAssessmentView;
    private FrameLayout nextAssessmentFrame;
    private LinearLayout noAssessmentLayout;
    private Button viewAllAssessmentsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WorkManager wm = WorkManager.getInstance(getApplication());
        PeriodicWorkRequest saveRequest =
                new PeriodicWorkRequest.Builder(NotificationWorkManager.class, 15, TimeUnit.MINUTES)
                        .setInitialDelay(1, TimeUnit.MINUTES)
                        .build();
        wm.enqueue(saveRequest);

        setContentView(R.layout.activity_home_screen);
        initViews();
        initViewModel();
        initObservers();
    }

    private void initViews() {
        initToolbar();
        initTermSection();
        initCourseSection();
        initAssessmentSection();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(getViewModelStore(), new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(HomeScreenViewModel.class);
        viewModel.init();
    }

    private void initObservers() {
        initTermObserver();
        initCourseObserver();
        initAssessmentObserver();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.home_toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
    }

    private void initTermSection() {
        // latestTermFrame = findViewById(R.id.latest_term_frame);
        noLatestTermLayout = findViewById(R.id.no_latest_term_layout);
        viewAllTermsButton = findViewById(R.id.view_all_terms);
        viewAllTermsButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreenActivity.this, AllTermsActivity.class);
            startActivity(intent);
        });
        addTermButton = findViewById(R.id.home_add_term_button);
        addTermButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreenActivity.this, AddTermActivity.class);
            startActivity(intent);
        });
    }

    private void initCourseSection() {
        //currentCourseFrame = findViewById(R.id.latest_course_frame);
        noCurrentCourseLayout = findViewById(R.id.no_latest_course_layout);
        viewAllCoursesButton = findViewById(R.id.view_all_courses);
        viewAllCoursesButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreenActivity.this, AllCoursesActivity.class);
            startActivity(intent);
        });
        addCourseButton = findViewById(R.id.home_add_course_button);
        addCourseButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreenActivity.this, AddNewCourseActivity.class);
            startActivity(intent);
        });
    }

    private void initAssessmentSection() {
        nextAssessmentFrame = findViewById(R.id.next_assessment_frame);
        noAssessmentLayout = findViewById(R.id.no_assessment_layout);
        viewAllAssessmentsButton = findViewById(R.id.view_all_assessments);
        viewAllAssessmentsButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreenActivity.this, AllAssessmentsActivity.class);
            startActivity(intent);
        });
    }

    private void initTermObserver() {

        final Observer<Optional<Term>> latestTermObserver = t -> {

            latestTerm = t.isPresent() ? t.get() : null;

            if (latestTermView == null) {
                latestTermView = findViewById(R.id.latest_term_view);
                latestTermView.setOnClickListener(v -> {

                    Intent intent = new Intent(this, TermDetailsActivity.class);
                    intent.putExtra(TermDetailsActivity.TERM_ID_KEY, latestTerm.getTermId());
                    startActivity(intent);
                });
            }
            TextView titleView = latestTermView.findViewById(R.id.term_list_item_title);

            TextView start = latestTermView.findViewById(R.id.term_list_item_start);
            TextView end = latestTermView.findViewById(R.id.term_list_item_end);

            titleView.setText(latestTerm != null ? latestTerm.getTitle() : "");
            SimpleDateFormat df = new SimpleDateFormat("MMM d, yyyy");
            start.setText(latestTerm != null ? df.format(latestTerm.getStart()) : "");
            end.setText(latestTerm != null ? df.format(latestTerm.getEnd()) : "");

            boolean latestTermVisible = latestTerm != null;

            noLatestTermLayout.setVisibility(latestTermVisible ? View.GONE : View.VISIBLE);
            latestTermView.setVisibility(latestTermVisible ? View.VISIBLE : View.GONE);
        };
        viewModel.latestTerm.observe(this, latestTermObserver);
    }

    private void initCourseObserver() {
        final Observer<Optional<Course>> currentCourseObserver = c -> {

            currentCourse = c.isPresent() ? c.get() : null;

                if (currentCourseView == null) {

                    currentCourseView = findViewById(R.id.latest_course_view);
                    //currentCourseView = getLayoutInflater().inflate(R.layout.course_list_item, currentCourseFrame);
                    currentCourseView.setOnClickListener(v -> {

                        Intent intent = new Intent(HomeScreenActivity.this, CourseDetailsActivity.class);
                        intent.putExtra(CourseDetailsActivity.COURSE_ID_KEY, currentCourse.getCourseId());
                        startActivity(intent);
                    });
                }

            TextView courseTitleText = currentCourseView.findViewById(R.id.course_list_item_title);
            courseTitleText.setText(currentCourse != null ? currentCourse.getTitle() : "");

            TextView statusText = currentCourseView.findViewById(R.id.course_list_item_status);
            statusText.setText(currentCourse != null ? currentCourse.getStatus() : "");

            SimpleDateFormat df = new SimpleDateFormat("MMM d, yyyy");
            TextView startText = currentCourseView.findViewById(R.id.course_list_item_start);
            startText.setText(currentCourse != null ? df.format(currentCourse.getStartDate()) : "");

            TextView endText = currentCourseView.findViewById(R.id.course_list_item_end);
            endText.setText(currentCourse != null ? df.format(currentCourse.getAnticipatedEndDate()) : "");

            if (currentCourse != null) {
                TextView optionsText = currentCourseView.findViewById(R.id.course_list_item_option);
                optionsText.setOnClickListener(v -> {

                    PopupMenu popupMenu = new PopupMenu(HomeScreenActivity.this, v);
                    popupMenu.inflate(R.menu.course_list_item_menu);
                    if (currentCourse.getTermId() == 0) {

                        MenuItem removeItem = popupMenu.getMenu().findItem(R.id.course_action_remove);
                        removeItem.setVisible(false);
                    }

                    popupMenu.setOnMenuItemClickListener(item -> {
                        if (item.getItemId() == R.id.course_action_remove) {
                            currentCourse.setTermId(0);
                            CourseRepository.getInstance(v.getContext()).insertOrUpdate(currentCourse);
                        } else {
                            CourseRepository.getInstance(v.getContext()).delete(currentCourse);
                        }
                        return false;
                    });
                    popupMenu.show();
                });
            }

            boolean visible = currentCourse != null;

            currentCourseView.setVisibility(visible ? View.VISIBLE : View.GONE);
            noCurrentCourseLayout.setVisibility(visible ? View.GONE : View.VISIBLE);
        };

        viewModel.latestCourse.observe(this, currentCourseObserver);
    }

    private void initAssessmentObserver() {
        final Observer<Optional<Assessment>> nextAssessmentObserver = a -> {

            nextAssessment = a.isPresent() ? a.get() : null;


            if (nextAssessmentView == null) {

                nextAssessmentView = findViewById(R.id.next_assessment);
                //nextAssessmentView = getLayoutInflater().inflate(R.layout.assessment_list_item, nextAssessmentFrame);
            }
            TextView titleTextView = nextAssessmentView.findViewById(R.id.assessment_list_item_title);
            titleTextView.setText(nextAssessment != null ? nextAssessment.getTitle() : "");

            SimpleDateFormat df = new SimpleDateFormat("MMM d, yyyy");

            TextView goalDateTextView = nextAssessmentView.findViewById(R.id.assessment_list_item_start);
            goalDateTextView.setText(nextAssessment != null ? df.format(nextAssessment.getGoalDate()) : "");

            TextView typeTextView = nextAssessmentView.findViewById(R.id.assessment_list_item_type);
            typeTextView.setText(nextAssessment != null ? nextAssessment.getTestType() : "");

            boolean visible = nextAssessment != null;

            nextAssessmentView.setVisibility(visible ? View.VISIBLE : View.GONE);
            noAssessmentLayout.setVisibility(visible ? View.GONE : View.VISIBLE);
        };


        viewModel.nextAssessment.observe(this, nextAssessmentObserver);
    }
}
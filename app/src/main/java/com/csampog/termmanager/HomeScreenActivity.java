package com.csampog.termmanager;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.csampog.termmanager.model.Assessment;
import com.csampog.termmanager.model.Course;
import com.csampog.termmanager.model.Term;
import com.csampog.termmanager.viewmodels.HomeScreenViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
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

    private Course currentCourse;
    private View currentCourseView;
    private FrameLayout currentCourseFrame;
    private LinearLayout noCurrentCourseLayout;
    private Button viewAllCoursesButton;

    private Assessment nextAssessment;
    private View nextAssessmentView;
    private FrameLayout nextAssessmentFrame;
    private LinearLayout noAssessmentLayout;
    private Button viewAllAssessmentsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        JobScheduler mScheduler;
        mScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
//        int jobId = 1;
//        JobInfo existingJob = mScheduler.getPendingJob(jobId);
//        if(existingJob == null){
//            ComponentName serviceName = new ComponentName(getPackageName(),
//                    NotificationJobService.class.getName());
//            JobInfo.Builder builder = new JobInfo.Builder(jobId, serviceName);
//            builder.
//        }
//        WorkManager wm = WorkManager.getInstance(this);
//        PeriodicWorkRequest saveRequest =
//                new PeriodicWorkRequest.Builder(NotificationWorkManager.class, 10, TimeUnit.MINUTES)
//                        .setInitialDelay(1, TimeUnit.MINUTES)
//                        .build();
//        wm.enqueue(saveRequest);

        setContentView(R.layout.activity_home_screen);
        Toolbar toolbar = findViewById(R.id.home_toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);


        latestTermFrame = findViewById(R.id.latest_term_frame);
        noLatestTermLayout = findViewById(R.id.no_latest_term_layout);
        viewAllTermsButton = findViewById(R.id.view_all_terms);

        currentCourseFrame = findViewById(R.id.latest_course_frame);
        noCurrentCourseLayout = findViewById(R.id.no_latest_course_layout);
        viewAllCoursesButton = findViewById(R.id.view_all_courses);
        viewAllCoursesButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreenActivity.this, AllCoursesActivity.class);
            startActivity(intent);
        });


        nextAssessmentFrame = findViewById(R.id.next_assessment_frame);

        noAssessmentLayout = findViewById(R.id.no_assessment_layout);


        viewAllAssessmentsButton = findViewById(R.id.view_all_assessments);
        viewAllAssessmentsButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreenActivity.this, AllAssessmentsActivity.class);
            startActivity(intent);
        });

        viewAllTermsButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreenActivity.this, AllTermsActivity.class);
            startActivity(intent);
        });

        viewModel = new ViewModelProvider(getViewModelStore(), new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(HomeScreenViewModel.class);
        viewModel.init();

        initTermObserver();

        initCourseObserver();

        final Observer<Optional<Assessment>> nextAssessmentObserver = a -> {

            nextAssessment = a.isPresent() ? a.get() : null;
            if (nextAssessment != null) {

                if(nextAssessmentView == null){

                    nextAssessmentView = findViewById(R.id.next_assessment);
                    nextAssessmentView = getLayoutInflater().inflate(R.layout.assessment_list_item, nextAssessmentFrame);
                }
                TextView titleTextView = nextAssessmentView.findViewById(R.id.assessment_list_item_title);
                titleTextView.setText(nextAssessment.getTitle());

                SimpleDateFormat df = new SimpleDateFormat("MMM d, yyyy");

                TextView goalDateTextView = nextAssessmentView.findViewById(R.id.assessment_list_item_start);
                goalDateTextView.setText(df.format(nextAssessment.getGoalDate()));

                TextView typeTextView = nextAssessmentView.findViewById(R.id.assessment_list_item_type);
                typeTextView.setText(nextAssessment.getTestType());

                nextAssessmentView.setVisibility(View.VISIBLE);
                noAssessmentLayout.setVisibility(View.INVISIBLE);
            } else {
                noAssessmentLayout.setVisibility(View.VISIBLE);
                if(nextAssessmentView != null) {
                    nextAssessmentView.setVisibility(View.INVISIBLE);
                }
            }
        };

        viewModel.nextAssessment.observe(this, nextAssessmentObserver);
    }

    private void initCourseObserver() {
        final Observer<Optional<Course>> currentCourseObserver = c -> {

            currentCourse = c.isPresent() ? c.get() : null;
            if (currentCourse != null) {

                if (currentCourseView == null) {

                    currentCourseView = findViewById(R.id.latest_course_view);
                    currentCourseView = getLayoutInflater().inflate(R.layout.course_list_item, currentCourseFrame);
                    currentCourseView.setOnClickListener(v -> {

                        Intent intent = new Intent(HomeScreenActivity.this, CourseDetailsActivity.class);
                        intent.putExtra(CourseDetailsActivity.COURSE_ID_KEY, currentCourse.getCourseId());
                        startActivity(intent);
                    });
                }

                TextView courseTitleText = currentCourseView.findViewById(R.id.course_list_item_title);
                courseTitleText.setText(currentCourse.getTitle());

                TextView statusText = currentCourseView.findViewById(R.id.course_list_item_status);
                statusText.setText(currentCourse.getStatus());

                SimpleDateFormat df = new SimpleDateFormat("MMM d, yyyy");
                TextView startText = currentCourseView.findViewById(R.id.course_list_item_start);
                startText.setText(df.format(currentCourse.getStartDate()));

                TextView endText = currentCourseView.findViewById(R.id.course_list_item_end);
                endText.setText(df.format(currentCourse.getAnticipatedEndDate()));


                currentCourseView.setVisibility(View.VISIBLE);
                noCurrentCourseLayout.setVisibility(View.INVISIBLE);
            } else {

                noCurrentCourseLayout.setVisibility(View.VISIBLE);
                if(currentCourseView != null) {
                    currentCourseView.setVisibility(View.INVISIBLE);
                }
            }
        };

        viewModel.latestCourse.observe(this, currentCourseObserver);
    }


    private void initTermObserver() {

        final Observer<Optional<Term>> latestTermObserver = t -> {

            latestTerm = t.isPresent() ? t.get() : null;
            if (latestTerm != null) {
                if (latestTermView == null) {
                    latestTermView = findViewById(R.id.latest_term_view);
                    latestTermView = getLayoutInflater().inflate(R.layout.term_list_item, latestTermFrame);
                    latestTermView.setOnClickListener(v -> {

                        Intent intent = new Intent(this, TermDetailsActivity.class);
                        intent.putExtra(TermDetailsActivity.TERM_ID_KEY, latestTerm.getTermId());
                        startActivity(intent);
                    });
                }
                TextView titleView = latestTermView.findViewById(R.id.term_list_item_title);
                titleView.setText(latestTerm.getTitle());
                TextView start = latestTermView.findViewById(R.id.term_list_item_start);
                TextView end = latestTermView.findViewById(R.id.term_list_item_end);


                SimpleDateFormat df = new SimpleDateFormat("MMM d, yyyy");
                start.setText(df.format(latestTerm.getStart()));
                end.setText(df.format(latestTerm.getEnd()));

                noLatestTermLayout.setVisibility(View.INVISIBLE);
                latestTermView.setVisibility(View.VISIBLE);
            } else {
                if (latestTermView != null) {
                    latestTermView.setVisibility(View.INVISIBLE);
                }
                noLatestTermLayout.setVisibility(View.VISIBLE);
            }
        };
        viewModel.latestTerm.observe(this, latestTermObserver);
    }
}
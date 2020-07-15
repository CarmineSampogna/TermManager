package com.csampog.termmanager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.csampog.termmanager.model.Course;
import com.csampog.termmanager.viewmodels.EditCourseViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.temporal.ChronoUnit;
import java.util.OptionalInt;

public class EditCourseActivity extends AppCompatActivity {

    public static final String TERM_ID_PARAM = "termId";
    public static final String COURSE_ID_PARAM = "courseId";

    private MaterialButton startText;
    private MaterialButton endText;

    private FloatingActionButton saveButton;
    private EditText titleText;
    private EditText mentorNameText;
    private EditText mentorEmailText;
    private EditText mentorPhoneText;
    private RadioGroup statusRadioGroup;
    private Switch courseAlertsSwitch;


    private OptionalInt termId = OptionalInt.empty();
    private EditCourseViewModel viewModel;

    private OptionalInt courseId = OptionalInt.empty();


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        Intent intent = getIntent();
        if (intent.hasExtra(TERM_ID_PARAM)) {

            int paramValue = intent.getIntExtra(getString(R.string.param_termId), 0);
            if (paramValue > 0) {

                termId = OptionalInt.of(paramValue);
            }
        }

        if (intent.hasExtra(COURSE_ID_PARAM)) {
            courseId = OptionalInt.of(intent.getIntExtra(COURSE_ID_PARAM, 0));
        }

        Toolbar toolbar = findViewById(R.id.add_course_toolbar);
        int activityTitleRes = courseId.isPresent() ? R.string.edit_course_title : R.string.add_course_title;
        toolbar.setTitle(activityTitleRes);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        statusRadioGroup = findViewById(R.id.add_course_status_group);
        statusRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String status = "";
            switch (checkedId) {
                case R.id.add_course_status_completed_option:
                    status = Course.COMPLETED;
                    break;
                case R.id.add_course_status_dropped_option:
                    status = Course.DROPPED;
                    break;
                case R.id.add_course_status_inProgress_option:
                    status = Course.IN_PROGRESS;
                    break;
                case R.id.add_course_status_planToTake_option:
                    status = Course.PLAN_TO_TAKE;
                    break;
            }
            viewModel.statusInput = status;
        });

        courseAlertsSwitch = findViewById(R.id.course_alerts_switch);
        courseAlertsSwitch.setOnCheckedChangeListener((v, b) -> viewModel.startAlertEnabledInput = b);

        mentorNameText = findViewById(R.id.course_mentorName_text);
        mentorNameText.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PERSON_NAME);
        mentorNameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.mentorNameInput = s.toString();
            }
        });

        mentorEmailText = findViewById(R.id.course_mentorEmail_text);
        mentorEmailText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.mentorEmailInput = s.toString();
            }
        });

        mentorPhoneText = findViewById(R.id.course_mentorPhone_text);
        mentorPhoneText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.mentorPhoneInput = s.toString();
            }
        });

        saveButton = findViewById(R.id.add_course_save);
        saveButton.setOnClickListener(v -> {
            try {


                StringBuilder errorBuilder = new StringBuilder();
                boolean canSave = true;

                if (titleText.getText() == null ||
                        titleText.getText().toString().length() < 3) {
                    canSave = false;
                    errorBuilder.append(getString(R.string.title_error) + "\n");
                }

                if (viewModel.startDate == null) {
                    canSave = false;
                    errorBuilder.append(getString(R.string.course_start_error) + "\n");
                }

                if (viewModel.endDate == null) {
                    canSave = false;
                    errorBuilder.append(getString(R.string.course_end_error) + "\n");
                }

                if (canSave) {

                    viewModel.saveCourse();
                    finish();
                } else {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setTitle(R.string.assessment_save_error_title);
                    alertDialogBuilder.setMessage(errorBuilder.toString());
                    alertDialogBuilder.setPositiveButton(R.string.ok_button_text, (dialog, which) -> dialog.dismiss());
                    alertDialogBuilder.show();
                }


            } catch (Exception ex) {
                Log.e(AddTermActivity.class.getName(), ex.getMessage());
            }
        });
        saveButton.setVisibility(View.VISIBLE);
        initTitleText();

        initDateButtons();
        initViewModel();
    }

    private void initTitleText() {
        titleText = findViewById(R.id.course_title_text);
        titleText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.titleInput = s.toString();
            }
        });
    }

    private void initDateButtons() {

        startText = findViewById(R.id.course_start_text);
        endText = findViewById(R.id.course_end_text);

        startText.setOnClickListener(new EditCourseActivity.DateClickListener());
        endText.setOnClickListener(new EditCourseActivity.DateClickListener());
    }

    private void initViewModel() {

        viewModel = new ViewModelProvider(getViewModelStore(), new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(EditCourseViewModel.class);

        if (termId.isPresent()) {
            viewModel.termId = termId.getAsInt();
        }

        if(courseId.isPresent()){
            viewModel.setCourseId(courseId.getAsInt());
        }

        final Observer<String> titleObserver = s -> titleText.setText(s);

        final Observer<String> startDateObserver = s -> startText.setText(s);

        final Observer<String> endDateObserver = s -> endText.setText(s);

        final Observer<String> mentorNameObserver = s -> mentorNameText.setText(s);

        final Observer<String> mentorEmailObserver = s -> mentorEmailText.setText(s);

        final Observer<String> mentorPhoneObserver = s -> mentorPhoneText.setText(s);

        final Observer<String> statusObserver = s -> {
            int checkedId = 0;

            if(s.contentEquals(Course.PLAN_TO_TAKE)){
                checkedId = R.id.add_course_status_planToTake_option;
            }else if( s.contentEquals(Course.IN_PROGRESS)){
                checkedId = R.id.add_course_status_inProgress_option;
            }else if(s.contentEquals(Course.COMPLETED)){
                checkedId = R.id.add_course_status_completed_option;
            }else{
                checkedId = R.id.add_course_status_dropped_option;
            }
            statusRadioGroup.check(checkedId);
        };

        final Observer<Boolean> alertsEnabledObserver = alertsEnabled -> courseAlertsSwitch.setChecked(alertsEnabled != null ? alertsEnabled : false);

        final Observer<Boolean> canSaveObserver = aBoolean -> {
            if (aBoolean) {
                saveButton.show();
            } else {
                saveButton.hide();
            }
        };

        viewModel.title.observe(this, titleObserver);
        viewModel.mentorName.observe(this, mentorNameObserver);
        viewModel.mentorEmail.observe(this, mentorEmailObserver);
        viewModel.mentorPhone.observe(this, mentorPhoneObserver);
        viewModel.formattedStartDate.observe(this, startDateObserver);
        viewModel.formattedEndDate.observe(this, endDateObserver);
        viewModel.status.observe(this, statusObserver);
        viewModel.startAlertEnabled.observe(this, alertsEnabledObserver);
        //viewModel.canSave.observe(this, canSaveObserver);

    }

    private class DateClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            final MaterialButton fView = (MaterialButton) v;
            DatePickerDialog datePickerDialog = new DatePickerDialog(EditCourseActivity.this);

            boolean isStartDate = fView.getId() == R.id.course_start_text;
            if (isStartDate) {

                if (viewModel.endDate != null) {
                    datePickerDialog.getDatePicker().setMaxDate(viewModel.endDate.toInstant().minus(1, ChronoUnit.DAYS).toEpochMilli());
                }
            } else {

                if (viewModel.startDate != null) {
                    datePickerDialog.getDatePicker().setMinDate(viewModel.startDate.toInstant().plus(1, ChronoUnit.DAYS).toEpochMilli());
                }
            }

            datePickerDialog.setOnDateSetListener((view, year, month, dayOfMonth) -> viewModel.setDate(year, month, dayOfMonth, isStartDate));

            int titleId = isStartDate ? R.string.course_start_picker_title : R.string.course_end_picker_title;

            datePickerDialog.setTitle(getString(titleId));
            datePickerDialog.show();
        }
    }
}
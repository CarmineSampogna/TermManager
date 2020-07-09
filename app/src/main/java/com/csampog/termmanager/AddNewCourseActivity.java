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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.csampog.termmanager.model.Course;
import com.csampog.termmanager.viewmodels.AddCourseViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.OptionalInt;

public class AddNewCourseActivity extends AppCompatActivity {

    public static final String TERM_ID_PARAM = "termId";

    private MaterialButton startText;
    private MaterialButton endText;
    private DatePickerDialog datePickerDialog;
    private FloatingActionButton saveButton;
    private EditText titleText;
    private EditText mentorNameText;
    private EditText mentorEmailText;
    private EditText mentorPhoneText;
    private RadioGroup statusRadioGroup;
    private Switch alertSwitch;


    private OptionalInt termId = OptionalInt.empty();
    private AddCourseViewModel viewModel;

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


        initToolbar();

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
            viewModel.setStatus(status);
        });


        saveButton = findViewById(R.id.add_course_save);
        saveButton.setOnClickListener(v -> {
            try {

                StringBuilder errorBuilder = new StringBuilder();
                boolean canSave = true;

                if (titleText.getText() == null || titleText.getText().length() < 3) {
                    canSave = false;
                    errorBuilder.append("Title must be at least 3 characters");
                }

                if (viewModel.formattedStartDate.getValue() == null) {
                    canSave = false;
                    errorBuilder.append("Start date must be set");
                }

                if (canSave) {

                    viewModel.createCourse();
                    finish();
                } else {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setTitle(R.string.course_save_error_title);
                    alertDialogBuilder.setMessage(errorBuilder.toString());
                    alertDialogBuilder.setPositiveButton(R.string.ok_button_text, (dialog, which) -> dialog.dismiss());
                    alertDialogBuilder.show();
                }

            } catch (Exception ex) {
                Log.e(AddTermActivity.class.getName(), ex.getMessage());
            }
        });
        initTitleText();

        alertSwitch = findViewById(R.id.course_alerts_switch);
        alertSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                viewModel.alertsEnabled.setValue(isChecked);
            }
        });
        initMentorFields();

        initDateButtons();
        initViewModel();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.add_course_toolbar);
        int activityTitleRes = R.string.add_course_title;
        toolbar.setTitle(activityTitleRes);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initMentorFields() {
        mentorNameText = findViewById(R.id.course_mentorName_text);
        mentorNameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.mentorName.setValue(s.toString());
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
                viewModel.mentorEmail.setValue(s.toString());
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
                viewModel.mentorPhone.setValue(s.toString());
            }
        });
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
                viewModel.setTitle(s.toString());
            }
        });
    }

    private void initDateButtons() {

        startText = findViewById(R.id.course_start_text);
        endText = findViewById(R.id.course_end_text);

        startText.setOnClickListener(new AddNewCourseActivity.DateClickListener());
        endText.setOnClickListener(new AddNewCourseActivity.DateClickListener());
    }

    private void initViewModel() {

        viewModel = new ViewModelProvider(getViewModelStore(), new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(AddCourseViewModel.class);
        viewModel.setStatus(Course.PLAN_TO_TAKE);

        if (termId.isPresent() && termId.getAsInt() > 0) {
            viewModel.setTermId(termId.getAsInt());
        }

        final Observer<String> startDateObserver = s -> startText.setText(s);

        final Observer<String> endDateObserver = s -> endText.setText(s);

        final Observer<Boolean> canSaveObserver = aBoolean -> {
            if (aBoolean) {
                saveButton.show();
            } else {
                saveButton.hide();
            }
        };

        viewModel.formattedStartDate.observe(this, startDateObserver);
        viewModel.formattedEndDate.observe(this, endDateObserver);
        viewModel.canSave.observe(this, canSaveObserver);

    }

    private class DateClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            final MaterialButton fView = (MaterialButton) v;

            if (datePickerDialog == null) {

                datePickerDialog = new DatePickerDialog(AddNewCourseActivity.this);
            }

            boolean isStartDate = fView.getId() == R.id.course_start_text;

            datePickerDialog.setOnDateSetListener((view, year, month, dayOfMonth) -> viewModel.setDate(year, month, dayOfMonth, isStartDate));

            int titleId = isStartDate ? R.string.course_start_picker_title : R.string.course_end_picker_title;

            datePickerDialog.setTitle(getString(titleId));
            datePickerDialog.show();
        }
    }
}
package com.csampog.termmanager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.csampog.termmanager.viewmodels.EditAssessmentViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.Date;

public class EditAssessmentActivity extends AppCompatActivity {

    public static final String ASSESSMENT_ID_PARAM = "assessment";
    public static final String COURSE_ID_PARAM = "courseId";
    public static final String EDIT_MODE_PARAM = "editMode";

    private EditAssessmentViewModel viewModel;
    private int assessmentId = 0;
    private int courseId = 0;
    private RadioGroup testTypeGroup;
    private MaterialButton goalDateButton;
    private DatePickerDialog datePickerDialog;
    private FloatingActionButton saveButton;
    private EditText titleEditText;
    private RadioButton performanceButton;
    private RadioButton objectiveButton;
    private Switch alertSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_assessment);

        Intent intent = getIntent();
        assessmentId = intent.getIntExtra(ASSESSMENT_ID_PARAM, 0);
        courseId = intent.getIntExtra(COURSE_ID_PARAM, 0);

        initToolbar();
        initViews();
        initViewModel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_assessment_menu, menu);
        return true;
    }


    private void initToolbar() {

        Toolbar toolbar = findViewById(R.id.edit_assessment_toolbar);
        toolbar.setTitle(getString(R.string.edit_assessment_text));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {

        initTitleTextView();
        initAlertSwitch();
        initSaveButton();
        initDateButton();
        initTestTypeGroup();
    }

    private void initAlertSwitch() {
        alertSwitch = findViewById(R.id.edit_assessment_alerts_switch);
        alertSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> viewModel.setAlertsEnabled(isChecked));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.edit_assessment_delete_button) {
            viewModel.deleteAssessment();
        }

        finish();
        return true;
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(getViewModelStore(), new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(EditAssessmentViewModel.class);

        viewModel.setAssessmentId(assessmentId);
        viewModel.setCourseId(courseId);

        final Observer<String> titleObserver = s -> titleEditText.setText(s);
        final Observer<Boolean> alertsObserver = b -> alertSwitch.setChecked(b);

        final Observer<Boolean> canSaveObserver = aBoolean -> {
            saveButton.setEnabled(aBoolean);
        };
        final Observer<String> formattedGoalDateObserver = formattedDate -> goalDateButton.setText(formattedDate);

        //viewModel.canSave.observe(this, canSaveObserver);
        viewModel.formattedGoalDate.observe(this, formattedGoalDateObserver);
        viewModel.alertsEnabled.observe(this, alertsObserver);
        viewModel.title.observe(this, titleObserver);
        viewModel.assessmentType.observe(this, s -> {
            if (s.contentEquals(getString(R.string.assessment_type_objective))) {
                objectiveButton.setChecked(true);
            } else {

                performanceButton.setChecked(true);
            }
        });
    }

    private void initSaveButton() {
        saveButton = findViewById(R.id.edit_assessment_save_button);
        saveButton.setOnClickListener(v -> {

            StringBuilder errorBuilder = new StringBuilder();
            boolean canSave = true;

            if (titleEditText.getText() == null ||
                    titleEditText.getText().toString().trim().length() < 3) {
                canSave = false;
                errorBuilder.append(getString(R.string.title_error) + "\n");
            }

            if (goalDateButton.getText() == getString(R.string.goal_date_text)) {
                canSave = false;
                errorBuilder.append("Goal date must be set.");
            }

            if (canSave) {

                viewModel.saveAssessment();
                finish();
            } else {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle(R.string.assessment_save_error_title);
                alertDialogBuilder.setMessage(errorBuilder.toString());
                alertDialogBuilder.setPositiveButton(R.string.ok_button_text, (dialog, which) -> dialog.dismiss());
                alertDialogBuilder.show();
            }
        });
    }

    private void initTitleTextView() {
        titleEditText = findViewById(R.id.edit_assessment_title_editText);
        titleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.setTitle(s.toString());
                viewModel.updateCanSave();
            }
        });
    }

    private void initTestTypeGroup() {
        testTypeGroup = findViewById(R.id.edit_assessment_type_group);
        testTypeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String selection = "";
            switch (checkedId) {
                case R.id.edit_assessment_type_objective_button:
                    selection = getString(R.string.assessment_type_objective);
                    break;
                case R.id.edit_assessment_type_performance_button:
                    selection = getString(R.string.assessment_type_performance);
                    break;
                default:
                    selection = "";
            }
            viewModel.setAssessmentType(selection);
        });

        objectiveButton = findViewById(R.id.edit_assessment_type_objective_button);
        performanceButton = findViewById(R.id.edit_assessment_type_performance_button);
    }

    private void initDateButton() {
        goalDateButton = findViewById(R.id.edit_assessment_goal_date_button);
        goalDateButton.setOnClickListener(new EditAssessmentActivity.DateClickListener());
    }

    private class DateClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            final MaterialButton fView = (MaterialButton) v;

            if (datePickerDialog == null) {

                datePickerDialog = new DatePickerDialog(EditAssessmentActivity.this);
            }

            Date date = viewModel.getGoalDate();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            datePickerDialog.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.setOnDateSetListener((view, year, month, dayOfMonth) -> viewModel.setDate(year, month, dayOfMonth));
            datePickerDialog.setTitle(getString(R.string.goal_date_picker_header));
            datePickerDialog.show();
        }
    }
}

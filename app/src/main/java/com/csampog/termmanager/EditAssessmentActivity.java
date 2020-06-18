package com.csampog.termmanager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.csampog.termmanager.model.Assessment;
import com.csampog.termmanager.viewmodels.AddAssessmentViewModel;
import com.csampog.termmanager.viewmodels.EditAssessmentViewModel;
import com.google.android.material.appbar.CollapsingToolbarLayout;
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

    private void initToolbar() {
        CollapsingToolbarLayout toolbarLayout = findViewById(R.id.add_assessment_toolbar);
        toolbarLayout.setTitle(getString(R.string.edit_assessment_text));
    }

    private void initViews() {

        initTitleTextView();
        initSaveButton();
        initDateButton();
        initTestTypeGroup();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(getViewModelStore(), new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(EditAssessmentViewModel.class);

        viewModel.setAssessmentId(assessmentId);
        viewModel.setCourseId(courseId);

        final Observer<String> titleObserver = s -> titleEditText.setText(s);

        final Observer<Boolean> canSaveObserver = aBoolean -> {
            saveButton.setEnabled(aBoolean);
        };
        final Observer<String> formattedGoalDateObserver = formattedDate -> goalDateButton.setText(formattedDate);

        viewModel.canSave.observe(this, canSaveObserver);
        viewModel.formattedGoalDate.observe(this, formattedGoalDateObserver);

        viewModel.title.observe(this, titleObserver);
        viewModel.assessmentType.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s == getString(R.string.assessment_type_objective)) {
                    objectiveButton.setChecked(true);
                } else {

                    performanceButton.setChecked(true);
                }
            }
        });
    }

    private void initSaveButton() {
        saveButton = findViewById(R.id.edit_assessment_save_button);
        saveButton.setOnClickListener(v -> {
            viewModel.saveAssessment();
            finish();
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

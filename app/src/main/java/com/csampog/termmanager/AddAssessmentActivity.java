package com.csampog.termmanager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.csampog.termmanager.viewmodels.AddAssessmentViewModel;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AddAssessmentActivity extends AppCompatActivity {

    public static final String COURSE_ID_PARAM = "courseId";
    public static final String ASSESSMENT_ID_PARAM = "assessment";
    public static final String EDIT_MODE_PARAM = "editMode";

    private AddAssessmentViewModel viewModel;
    private int courseId = 0;
    private int assessmentId = 0;
    private RadioGroup testTypeGroup;
    private MaterialButton goalDateButton;
    private DatePickerDialog datePickerDialog;
    private FloatingActionButton saveButton;
    private EditText titleEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_assessment);

        Intent intent = getIntent();
        courseId = intent.getIntExtra(COURSE_ID_PARAM, 0);

        assessmentId = intent.getIntExtra(ASSESSMENT_ID_PARAM, 0);

        initToolbar();

        initViews();

        initViewModel();
    }

    private void initToolbar() {
        CollapsingToolbarLayout toolbarLayout = findViewById(R.id.add_assessment_toolbar);
        toolbarLayout.setTitle(getString(R.string.add_assessment_title));
    }

    private void initViews() {

        initTitleTextView();
        initSaveButton();
        initDateButton();
        initTestTypeGroup();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(getViewModelStore(), new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(AddAssessmentViewModel.class);

        final Observer<String> titleObserver = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                    titleEditText.setText(s);
            }
        };

        final Observer<Boolean> canSaveObserver = aBoolean -> {
            saveButton.setEnabled(aBoolean);
        };
        final Observer<String> formattedGoalDateObserver = formattedDate -> goalDateButton.setText(formattedDate);

        viewModel.setCourseId(courseId);

      //  viewModel.canSave.observe(this, canSaveObserver);
        viewModel.formattedGoalDate.observe(this, formattedGoalDateObserver);
    }

    private void initSaveButton() {
        saveButton = findViewById(R.id.add_assessment_save_button);
        saveButton.setOnClickListener(v -> {

            StringBuilder errorBuilder = new StringBuilder();
            boolean canSave = true;

            if(titleEditText.getText() == null ||
            titleEditText.getText().toString().length() < 3){
                canSave = false;
                errorBuilder.append(getString(R.string.title_error) + "\n");
            }

            if(goalDateButton.getText() == getString(R.string.goal_date_text)){
                canSave = false;
                errorBuilder.append("Goal date must be set.");
            }

            if(canSave) {

                viewModel.saveAssessment();
                finish();
            }else{

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle(R.string.caourse_save_error_title);
                alertDialogBuilder.setMessage(errorBuilder.toString());
                alertDialogBuilder.setPositiveButton(R.string.ok_button_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialogBuilder.show();
            }
        });
    }

    private void initTitleTextView() {
        titleEditText = findViewById(R.id.assessment_title_editText);
        titleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.titleInput.setValue(s.toString());
                viewModel.updateCanSave();
            }
        });
    }

    private void initTestTypeGroup() {
        testTypeGroup = findViewById(R.id.assessment_type_group);
        testTypeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String selection = "";
            switch (checkedId) {
                case R.id.assessment_type_objective_button:
                    selection = getString(R.string.assessment_type_objective);
                    break;
                case R.id.assessment_type_performance_button:
                    selection = getString(R.string.assessment_type_performance);
                    break;
                default:
                    selection = "";
            }
            viewModel.setAssessmentType(selection);
        });
    }

    private void initDateButton() {
        goalDateButton = findViewById(R.id.goal_date_button);
        goalDateButton.setOnClickListener(new DateClickListener());
    }

    private class DateClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            final MaterialButton fView = (MaterialButton) v;

            if (datePickerDialog == null) {

                datePickerDialog = new DatePickerDialog(AddAssessmentActivity.this);
            }

            boolean isStartDate = fView.getId() == R.id.term_start_text;

            datePickerDialog.setOnDateSetListener((view, year, month, dayOfMonth) -> viewModel.setDate(year, month, dayOfMonth));

            int titleId = isStartDate ? R.string.term_start_picker_title : R.string.term_end_picker_title;

            datePickerDialog.setTitle(getString(titleId));
            datePickerDialog.show();
        }
    }
}
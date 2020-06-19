package com.csampog.termmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.csampog.termmanager.dataAccess.repositories.CourseRepository;
import com.csampog.termmanager.model.Course;
import com.csampog.termmanager.viewmodels.AddCourseViewModel;
import com.csampog.termmanager.viewmodels.EditCourseViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.OptionalInt;

public class EditCourseActivity extends AppCompatActivity {

    public static final String TERM_ID_PARAM = "termId";
    public static final String COURSE_ID_PARAM = "courseId";

    private MaterialButton startText;
    private MaterialButton endText;
    private DatePickerDialog datePickerDialog;
    private FloatingActionButton saveButton;
    private EditText titleText;
    private RadioGroup statusRadioGroup;


    private OptionalInt termId = OptionalInt.empty();
    private EditCourseViewModel viewModel;

    private OptionalInt courseId = OptionalInt.empty();



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

        statusRadioGroup = findViewById(R.id.add_course_status_group);
        statusRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
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
            }
        });

        saveButton = findViewById(R.id.add_course_save);
        saveButton.setOnClickListener(v -> {
            try {
                viewModel.saveCourse();
                if (termId.isPresent()) {

                    Intent termIntent = new Intent(EditCourseActivity.this, TermDetailsActivity.class);
                    termIntent.putExtra(TermDetailsActivity.TERM_ID_KEY, termId.getAsInt());
                    startActivity(termIntent);
                }

                finish();
            } catch (Exception ex) {
                Log.e(AddTermActivity.class.getName(), ex.getMessage());
            }
        });
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

        final Observer<String> statusObserver = s -> {

            if (s == getString(R.string.course_completed)) {
                statusRadioGroup.check(R.id.add_course_status_completed_option);
            }else if(s == getString(R.string.course_dropped)){
                statusRadioGroup.check(R.id.add_course_status_dropped_option);
            }else if (s == getString(R.string.course_in_progress)){
                statusRadioGroup.check(R.id.add_course_status_inProgress_option);
            }else{
                statusRadioGroup.check(R.id.add_course_status_planToTake_option);
            }
        };


        final Observer<Boolean> canSaveObserver = aBoolean -> {
            if (aBoolean) {
                saveButton.show();
            } else {
                saveButton.hide();
            }
        };

        CourseRepository courseRepository = CourseRepository.getInstance(getBaseContext());
        courseRepository.AddSampleData();

        viewModel.title.observe(this, titleObserver);
        viewModel.formattedStartDate.observe(this, startDateObserver);
        viewModel.formattedEndDate.observe(this, endDateObserver);
        viewModel.status.observe(this, statusObserver);
        viewModel.canSave.observe(this, canSaveObserver);

    }

    private class DateClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            final MaterialButton fView = (MaterialButton) v;

            if (datePickerDialog == null) {

                datePickerDialog = new DatePickerDialog(EditCourseActivity.this);
            }

            boolean isStartDate = fView.getId() == R.id.course_start_text;

            datePickerDialog.setOnDateSetListener((view, year, month, dayOfMonth) -> viewModel.setDate(year, month, dayOfMonth, isStartDate));

            int titleId = isStartDate ? R.string.course_start_picker_title : R.string.course_end_picker_title;

            datePickerDialog.setTitle(getString(titleId));
            datePickerDialog.show();
        }
    }
}
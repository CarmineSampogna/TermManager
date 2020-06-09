package com.csampog.termmanager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.csampog.termmanager.dataAccess.repositories.CourseRepository;
import com.csampog.termmanager.viewmodels.AddCourseViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.OptionalInt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class AddCourseActivity extends AppCompatActivity {

    private MaterialButton startText;
    private MaterialButton endText;
    private DatePickerDialog datePickerDialog;
    private FloatingActionButton saveButton;
    private EditText titleText;

    private OptionalInt termId = null;
    private AddCourseViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        Toolbar toolbar = findViewById(R.id.add_course_toolbar);
        toolbar.setTitle(R.string.add_course_title);

        Intent intent = getIntent();
        if (intent.hasExtra(getString(R.string.param_termId))) {

            int paramValue = intent.getIntExtra(getString(R.string.param_termId), 0);
            if (paramValue > 0) {

                termId = OptionalInt.of(paramValue);
            }
        }

        saveButton = findViewById(R.id.add_course_save);
        saveButton.setOnClickListener(v -> {
            try {
                viewModel.createCourse();
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
                viewModel.setTitle(s.toString());
            }
        });
    }

    private void initDateButtons() {

        startText = findViewById(R.id.course_start_text);
        endText = findViewById(R.id.course_end_text);

        startText.setOnClickListener(new AddCourseActivity.DateClickListener());
        endText.setOnClickListener(new AddCourseActivity.DateClickListener());
    }

    private void initViewModel() {

        viewModel = new ViewModelProvider(getViewModelStore(), new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(AddCourseViewModel.class);

        if (termId.isPresent()) {
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

        CourseRepository courseRepository = CourseRepository.getInstance(getBaseContext());
        courseRepository.AddSampleData();

        viewModel.formattedStartDate.observe(this, startDateObserver);
        viewModel.formattedEndDate.observe(this, endDateObserver);
        viewModel.canSave.observe(this, canSaveObserver);
    }

    private class DateClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            final MaterialButton fView = (MaterialButton) v;

            if (datePickerDialog == null) {

                datePickerDialog = new DatePickerDialog(AddCourseActivity.this);
            }

            boolean isStartDate = fView.getId() == R.id.term_start_text;

            datePickerDialog.setOnDateSetListener((view, year, month, dayOfMonth) -> viewModel.setDate(year, month, dayOfMonth, isStartDate));

            int titleId = isStartDate ? R.string.course_start_picker_title : R.string.course_end_picker_title;

            datePickerDialog.setTitle(getString(titleId));
            datePickerDialog.show();
        }
    }
}
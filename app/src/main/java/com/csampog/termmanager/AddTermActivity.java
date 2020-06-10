package com.csampog.termmanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.csampog.termmanager.dataAccess.repositories.CourseRepository;
import com.csampog.termmanager.viewmodels.AddTermViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class AddTermActivity extends AppCompatActivity {

    private MaterialButton startText;
    private MaterialButton endText;
    private DatePickerDialog datePickerDialog;
    private FloatingActionButton saveButton;
    private EditText titleText;

    private AddTermViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_term);

        Toolbar toolbar = findViewById(R.id.add_term_toolbar);
        toolbar.setTitle(getResources().getString(R.string.add_term_label));

        saveButton = findViewById(R.id.add_term_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    viewModel.createTerm();
                    finish();
                }catch(Exception ex){
                    Log.e(AddTermActivity.class.getName(), ex.getMessage());
                }
            }
        });
        initTitleText();

        initDateButtons();
        initViewModel();
    }

    private void initTitleText() {
        titleText = findViewById(R.id.term_title_text);
        titleText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                    viewModel.setTitle(s.toString());
            }
        });
    }

    private void initDateButtons() {

        startText = findViewById(R.id.term_start_text);
        endText = findViewById(R.id.term_end_text);

        startText.setOnClickListener(new DateClickListener());
        endText.setOnClickListener(new DateClickListener());
    }

    private void initViewModel() {

        viewModel = new ViewModelProvider(getViewModelStore(), new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(AddTermViewModel.class);

        final Observer<String> startDateObserver = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                startText.setText(s);
            }
        };

        final Observer<String> endDateObserver = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                endText.setText(s);
            }
        };

        final Observer<Boolean> canSaveObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    saveButton.show();
                } else {
                    saveButton.hide();
                }
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

                datePickerDialog = new DatePickerDialog(AddTermActivity.this);
            }

            boolean isStartDate = fView.getId() == R.id.term_start_text;

            datePickerDialog.setOnDateSetListener((view, year, month, dayOfMonth) -> viewModel.setDate(year, month, dayOfMonth, isStartDate));

            int titleId = isStartDate ? R.string.term_start_picker_title : R.string.term_end_picker_title;

            datePickerDialog.setTitle(getString(titleId));
            datePickerDialog.show();
        }
    }
}
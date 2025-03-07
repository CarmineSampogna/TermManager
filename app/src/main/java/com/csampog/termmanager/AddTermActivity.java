package com.csampog.termmanager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.csampog.termmanager.viewmodels.AddTermViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.temporal.ChronoUnit;

public class AddTermActivity extends AppCompatActivity {

    private MaterialButton startText;
    private MaterialButton endText;

    private FloatingActionButton saveButton;
    private EditText titleText;

    private AddTermViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_term);

        initToolbar();
        initSaveButton();
        initTitleText();
        initDateButtons();
        initViewModel();
    }

    private void initSaveButton() {
        saveButton = findViewById(R.id.add_term_save);
        saveButton.setOnClickListener(v -> {
            try {
                StringBuilder errorBuilder = new StringBuilder();
                boolean canSave = true;

                if (titleText.getText() == null || titleText.getText().toString().trim().length() < 3) {
                    canSave = false;
                    errorBuilder.append("Title must be at least 3 characters.\n");
                }

                if (viewModel.formattedStartDate.getValue() == null) {
                    canSave = false;
                    errorBuilder.append("Start date must be set.\n");
                }

                if (viewModel.formattedEndDate.getValue() == null) {
                    canSave = false;
                    errorBuilder.append("End date must be set.\n");
                }

                if (canSave) {
                    viewModel.createTerm();
                    finish();
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setTitle(R.string.term_save_error);
                    alertDialogBuilder.setMessage(errorBuilder.toString());
                    alertDialogBuilder.setPositiveButton(R.string.ok_button_text, (dialog, which) -> dialog.dismiss());
                    alertDialogBuilder.show();
                }
            } catch (Exception ex) {
                Log.e(AddTermActivity.class.getName(), ex.getMessage());
            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.add_term_toolbar);
        toolbar.setTitle(getResources().getString(R.string.add_term_label));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initTitleText() {
        titleText = findViewById(R.id.term_title_text);
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

        startText = findViewById(R.id.term_start_text);
        endText = findViewById(R.id.term_end_text);

        startText.setOnClickListener(new DateClickListener());
        endText.setOnClickListener(new DateClickListener());
    }

    private void initViewModel() {

        viewModel = new ViewModelProvider(getViewModelStore(), new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(AddTermViewModel.class);

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
        //viewModel.canSave.observe(this, canSaveObserver);
    }

    private class DateClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            final MaterialButton fView = (MaterialButton) v;

            DatePickerDialog datePickerDialog = new DatePickerDialog(AddTermActivity.this);

            boolean isStartDate = fView.getId() == R.id.term_start_text;
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

            int titleId = isStartDate ? R.string.term_start_picker_title : R.string.term_end_picker_title;

            datePickerDialog.setTitle(getString(titleId));
            datePickerDialog.show();
        }
    }
}
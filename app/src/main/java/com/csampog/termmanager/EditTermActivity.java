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
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.csampog.termmanager.viewmodels.EditTermViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class EditTermActivity extends AppCompatActivity {

    public static final String TERM_ID_PARAM = "termId";

    Toolbar toolbar;
    private EditTermViewModel viewModel;
    private EditText titleTextView;
    private Button startDateText;
    private Button endDateText;
    private FloatingActionButton saveButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        int termId = i.getIntExtra(TERM_ID_PARAM, 0);
        if (termId == 0) {
            return;
        }

        setContentView(R.layout.activity_add_term);

        initToolbar();

        viewModel = new ViewModelProvider(getViewModelStore(), new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(EditTermViewModel.class);
        viewModel.setTermId(termId);

        initTitleTextView();

        initSaveButton();

        initDateButtons();

        final Observer<String> titleObserver = s -> {
            titleTextView.setText(s);
            toolbar.setTitle(s);
        };
        final Observer<String> startDateObserver = s -> startDateText.setText(s);
        final Observer<String> endDateObserver = s -> endDateText.setText(s);

        viewModel.title.observe(this, titleObserver);
        viewModel.formattedStartDate.observe(this, startDateObserver);
        viewModel.formattedEndDate.observe(this, endDateObserver);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return true;
    }

    private void initSaveButton() {
        saveButton = findViewById(R.id.add_term_save);
        saveButton.setVisibility(View.VISIBLE);
        saveButton.setOnClickListener(v -> {
            try {

                StringBuilder errorBuilder = new StringBuilder();
                boolean canSave = true;

                if (titleTextView.getText() == null || titleTextView.getText().toString().trim().length() < 3) {
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
                    viewModel.saveTerm();
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

    private void initTitleTextView() {
        titleTextView = findViewById(R.id.term_title_text);
        titleTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.titleInput = s == null ? null : s.toString();
            }
        });
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.add_term_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initDateButtons() {

        startDateText = findViewById(R.id.term_start_text);
        endDateText = findViewById(R.id.term_end_text);

        startDateText.setOnClickListener(new EditTermActivity.DateClickListener());
        endDateText.setOnClickListener(new EditTermActivity.DateClickListener());
    }

    private class DateClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            final MaterialButton fView = (MaterialButton) v;

            DatePickerDialog datePickerDialog = new DatePickerDialog(EditTermActivity.this);

            boolean isStartDate = fView.getId() == R.id.term_start_text;

            Instant targetInstant;

            if (isStartDate) {

                targetInstant = viewModel.startDate.toInstant();
                if (viewModel.endDate != null) {
                    datePickerDialog.getDatePicker().setMaxDate(viewModel.endDate.toInstant().minus(1, ChronoUnit.DAYS).toEpochMilli());
                }
            } else {
                targetInstant = viewModel.endDate.toInstant();
                if (viewModel.startDate != null) {
                    datePickerDialog.getDatePicker().setMinDate(viewModel.startDate.toInstant().plus(1, ChronoUnit.DAYS).toEpochMilli());
                }
            }

            ZonedDateTime targetTime = targetInstant.atZone(ZoneId.systemDefault());
            int tMonth = targetTime.getMonthValue() - 1;
            datePickerDialog.getDatePicker().updateDate(targetTime.getYear(), tMonth, targetTime.getDayOfMonth());

            datePickerDialog.setOnDateSetListener((view, year, month, dayOfMonth) -> viewModel.setDate(year, month, dayOfMonth, isStartDate));

            int titleId = isStartDate ? R.string.term_start_picker_title : R.string.term_end_picker_title;

            datePickerDialog.setTitle(getString(titleId));
            datePickerDialog.show();
        }
    }
}
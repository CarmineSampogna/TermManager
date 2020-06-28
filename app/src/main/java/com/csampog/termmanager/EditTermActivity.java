package com.csampog.termmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.csampog.termmanager.viewmodels.EditTermViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class EditTermActivity extends AppCompatActivity {

    public  static final String TERM_ID_PARAM = "termId";

    private EditTermViewModel viewModel;
    private EditText titleTextView;
    private Button startDateText;
    private Button endDateText;
    private FloatingActionButton saveButton;
    private DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_term);

        viewModel = new ViewModelProvider(getViewModelStore(), new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(EditTermViewModel.class);


        Intent i = getIntent();
        int termId = i.getIntExtra(TERM_ID_PARAM, 0);

        viewModel.setTermId(termId);

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

        saveButton = findViewById(R.id.add_term_save);
        saveButton.setVisibility(View.VISIBLE);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    viewModel.saveTerm();
                    finish();
                }catch(Exception ex){
                    Log.e(AddTermActivity.class.getName(), ex.getMessage());
                }
            }
        });

       initDateButtons();

        final Observer<String> titleObserver = s -> titleTextView.setText(s);
        final Observer<String> startDateObserver = s -> startDateText.setText(s);
        final Observer<String> endDateObserver = s -> endDateText.setText(s);

        viewModel.title.observe(this, titleObserver);
        viewModel.formattedStartDate.observe(this, startDateObserver);
        viewModel.formattedEndDate.observe(this, endDateObserver);
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

            if (datePickerDialog == null) {

                datePickerDialog = new DatePickerDialog(EditTermActivity.this);
            }

            boolean isStartDate = fView.getId() == R.id.term_start_text;

            datePickerDialog.setOnDateSetListener((view, year, month, dayOfMonth) -> viewModel.setDate(year, month, dayOfMonth, isStartDate));

            int titleId = isStartDate ? R.string.term_start_picker_title : R.string.term_end_picker_title;

            datePickerDialog.setTitle(getString(titleId));
            datePickerDialog.show();
        }
    }
}
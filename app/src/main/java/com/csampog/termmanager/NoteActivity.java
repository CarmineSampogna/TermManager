package com.csampog.termmanager;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.csampog.termmanager.viewmodels.NoteViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class NoteActivity extends AppCompatActivity {

    public static final String IS_NEW_NOTE_PARAM = "isNewNote";
    public static final String IS_READ_ONLY_PARAM = "isReadOnly";
    public static final String COURSE_ID_PARAM = "courseId";
    public static final String NOTE_ID_PARAM = "noteId";

    private boolean isReadOnly;
    private boolean isNewNote;
    private int noteId;
    private int courseId;

    private TextInputEditText noteTitleText;

    private EditText noteTextInput;
    private Button saveButton;
    private Button cancelButton;
    private Button deleteButton;

    private NoteViewModel viewModel;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!isNewNote) {

            getMenuInflater().inflate(R.menu.note_details_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int selectedId = item.getItemId();
        if (selectedId == R.id.note_delete_button) {
            viewModel.deleteNote();
            finish();
        } else if (selectedId == R.id.note_details_share) {

            Intent sendIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"));
            sendIntent.putExtra("sms_body", noteTitleText.getText().toString() + ". " + noteTextInput.getText().toString());

            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
                sendIntent.setType("vnd.android-dir/mms-sms");
            startActivity(sendIntent);
        } else {
            finish();
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        Intent intent = getIntent();

        isNewNote = intent.getBooleanExtra(IS_NEW_NOTE_PARAM, true);

        isReadOnly = intent.getBooleanExtra(IS_READ_ONLY_PARAM, false);

        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(NoteViewModel.class);

        noteTitleText = findViewById(R.id.note_title_input);
        noteTitleText.addTextChangedListener(new TextWatcher() {
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

        noteTextInput = findViewById(R.id.note_text_input);
        noteTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.textInput = s.toString();
            }
        });

        saveButton = findViewById(R.id.note_save_button);
        saveButton.setOnClickListener(v -> {
            StringBuilder errorBuilder = new StringBuilder();
            boolean canSave = true;

            if (noteTitleText.getText() == null || noteTitleText.getText().toString().trim().length() < 3) {
                canSave = false;
                errorBuilder.append("Title must be at least 3 characters.\n");
            }

            if (noteTextInput.getText() == null || noteTextInput.getText().toString().trim().length() < 3) {
                canSave = false;
                errorBuilder.append("Note text must be at least 3 characters.\n");
            }

            if (canSave) {
                viewModel.saveNote();
                finish();
            } else {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle(R.string.note_save_error);
                alertDialogBuilder.setMessage(errorBuilder.toString());
                alertDialogBuilder.setPositiveButton(R.string.ok_button_text, (dialog, which) -> dialog.dismiss());
                alertDialogBuilder.show();
            }
        });

        cancelButton = findViewById(R.id.note_cancel_button);
        cancelButton.setOnClickListener(v -> finish());

        deleteButton = findViewById(R.id.note_delete_button);

        if (isNewNote) {
            courseId = intent.getIntExtra(COURSE_ID_PARAM, 0);
            viewModel.setCourseId(courseId);
        } else {

            noteId = intent.getIntExtra(NOTE_ID_PARAM, 0);
            viewModel.setNoteId(noteId);
        }

        initToolbar();

        if (!isNewNote) {
            final Observer<String> titleObserver = s -> noteTitleText.setText(s);
            final Observer<String> noteObserver = s -> noteTextInput.setText(s);

            viewModel.text.observe(this, noteObserver);
            viewModel.title.observe(this, titleObserver);
        }
    }

    private void initToolbar() {

        Toolbar toolbar = findViewById(R.id.note_toolbar);

        int titleRes = 0;

        if (isNewNote) {
            titleRes = R.string.new_note_title;
        } else if (!isReadOnly) {
            titleRes = R.string.edit_note_title;
        } else {
            titleRes = R.string.note_details_title;
        }

        toolbar.setTitle(titleRes);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
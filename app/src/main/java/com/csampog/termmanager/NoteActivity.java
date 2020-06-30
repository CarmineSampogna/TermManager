package com.csampog.termmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.csampog.termmanager.viewmodels.NoteViewModel;
import com.google.android.material.appbar.CollapsingToolbarLayout;
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
        if(!isNewNote){

            getMenuInflater().inflate(R.menu.note_details_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int selectedId = item.getItemId();
        if(selectedId == R.id.assessment_delete_menu_item){
            viewModel.deleteNote();
            finish();
        }else if(selectedId == R.id.note_details_share){

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.setData(Uri.parse("smsto:"));
            intent.putExtra("sms_body", noteTextInput.getText().toString());
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
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
            viewModel.saveNote();
            finish();
        });

        cancelButton = findViewById(R.id.note_cancel_button);
        cancelButton.setOnClickListener(v -> finish());

        deleteButton = findViewById(R.id.note_delete_button);

//        if(!isNewNote) {
//            deleteButton.setOnClickListener(v -> {
//                viewModel.deleteNote();
//                finish();
//            });
//        }else{
//            deleteButton.setVisibility(View.INVISIBLE);
//        }

        if(isNewNote){
            courseId = intent.getIntExtra(COURSE_ID_PARAM, 0);
            viewModel.setCourseId(courseId);
        }else{

            noteId = intent.getIntExtra(NOTE_ID_PARAM,0);
            viewModel.setNoteId(noteId);
        }
        
        initToolbar();

        if(!isNewNote) {
            final Observer<String> titleObserver = s -> noteTitleText.setText(s);
            final Observer<String> noteObserver = s -> noteTextInput.setText(s);

            viewModel.text.observe(this, noteObserver);
            viewModel.title.observe(this, titleObserver);
        }
    }

    private void initToolbar() {

        Toolbar toolbar = findViewById(R.id.note_toolbar);

        TextView tb = findViewById(R.id.note_title);

        if(isNewNote){
            tb.setText(R.string.new_note_title);
        }else if(!isReadOnly){
            tb.setText(R.string.edit_note_title);
        }else{
            tb.setText(R.string.note_details_title);
        }

        toolbar.setTitle("TESTING");
        setSupportActionBar(toolbar);
    }
}
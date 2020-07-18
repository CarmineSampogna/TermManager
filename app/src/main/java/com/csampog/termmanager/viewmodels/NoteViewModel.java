package com.csampog.termmanager.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.csampog.termmanager.dataAccess.repositories.NoteRepository;
import com.csampog.termmanager.model.Note;

import java.util.OptionalInt;

public class NoteViewModel extends AndroidViewModel {

    public LiveData<String> title;
    public String titleInput;

    public LiveData<String> text;
    public String textInput;

    public MutableLiveData<Boolean> canSave;

    private OptionalInt courseId = OptionalInt.empty();
    private OptionalInt noteId = OptionalInt.empty();

    private LiveData<Note> note;
    private NoteRepository noteRepository;

    public NoteViewModel(@NonNull Application application) {
        super(application);
        noteRepository = NoteRepository.getInstance(application);
        canSave = new MutableLiveData<>(false);
    }

    public void setCourseId(int courseId){
        this.courseId = OptionalInt.of(courseId);
    }

    public void setNoteId(int noteId){

        this.noteId = OptionalInt.of(noteId);
        this.note = noteRepository.getNoteById(this.noteId.getAsInt());

        title = Transformations.map(note, n-> {
            if(n != null) {
                textInput = n.getText();
                courseId = OptionalInt.of(n.getCourseId());
                return n.getTitle();
            }
            return "";
        });

        text = Transformations.map(note, n -> {
            if(n != null){
                titleInput = n.getTitle();
                return n.getText();
            }
            return "";
        });
    }

    public void updateCanSave() {

        boolean validTitle = titleInput != null && titleInput.trim().length() > 2;
        boolean validText = textInput != null && textInput.trim().length() > 2;

        canSave.setValue(validTitle && validText);
    }

    public void saveNote() {
        Note entity = new Note(titleInput.trim(), textInput.trim(), courseId.getAsInt());
        if(noteId.isPresent()){
            entity.setNoteId(noteId.getAsInt());
        }
        noteRepository.insertOrUpdate(entity);
    }

    public void deleteNote(){
        if(noteId.isPresent()){
            Note n = new Note();
            n.setNoteId(noteId.getAsInt());
            noteRepository.delete(n);
        }
    }
}

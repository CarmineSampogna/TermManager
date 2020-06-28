package com.csampog.termmanager.dataAccess.repositories;

import android.content.Context;

import com.csampog.termmanager.model.Note;

import java.util.List;

import androidx.lifecycle.LiveData;

public class NoteRepository extends RepositoryBase {

    private static NoteRepository instance;

    public static NoteRepository getInstance(Context context) {
        if (instance == null) {
            instance = new NoteRepository(context);
        }

        return instance;
    }

    private NoteRepository(Context context) {
        super(context);
    }

    public LiveData<Note> getNoteById(final int noteId) {

        return dbContext.noteDao().getNoteById(noteId);
    }

    public LiveData<List<Note>> getNotesForCourse(int courseId) {
        if(courseId < 1) throw new IllegalArgumentException();

        return dbContext.noteDao().getNotesForCourse(courseId);
    }

    public void insertOrUpdate(Note note) {
        if(note == null) throw new NullPointerException(Note.class.getName());

        final Note fNote = note;
        executor.execute(new Runnable() {
            @Override
            public void run() {
                dbContext.noteDao().insertOrUpdate(fNote);
            }
        });
    }

    public void insertOrUpdateAll(List<Note> notes) {
        if(notes == null) throw new NullPointerException(Note.class.getName());

        final List<Note> fNotes = notes;
        executor.execute(new Runnable() {
            @Override
            public void run() {
                dbContext.noteDao().insertOrUpdateAll(fNotes);
            }
        });
    }

    public void delete(Note note) {
        if(note == null) return;

        final Note fNote = note;

        executor.execute(new Runnable() {
            @Override
            public void run() {
                dbContext.noteDao().delete(fNote);
            }
        });
    }
}

package com.csampog.termmanager.dataAccess.repositories;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.csampog.termmanager.model.Note;

import java.util.List;

public class NoteRepository extends RepositoryBase {

    public NoteRepository(Context context) {
        super(context);
    }

    public Note getNoteById(final int noteId) {
        final Note[] ret = {null};

        executor.execute(new Runnable() {
            @Override
            public void run() {
                ret[0] = dbContext.noteDao().getNoteById(noteId);
            }
        });

        return ret[0];
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

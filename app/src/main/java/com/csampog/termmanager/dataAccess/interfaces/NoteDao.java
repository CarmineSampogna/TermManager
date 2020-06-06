package com.csampog.termmanager.dataAccess.interfaces;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.csampog.termmanager.model.Note;
import com.csampog.termmanager.model.Term;

import java.util.List;

@Dao
public interface NoteDao extends EntityDao<Note> {

    @Query("SELECT * FROM Notes WHERE noteId = :noteId")
    Note getNoteById(int noteId);

    @Query("SELECT * FROM Notes WHERE courseId = :courseId")
    LiveData<List<Note>> getNotesForCourse(int courseId);
}

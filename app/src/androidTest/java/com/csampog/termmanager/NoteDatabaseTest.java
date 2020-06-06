package com.csampog.termmanager;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;

import com.csampog.termmanager.dataAccess.interfaces.NoteDao;
import com.csampog.termmanager.dataAccess.TermManagerDbContext;
import com.csampog.termmanager.model.Course;
import com.csampog.termmanager.model.Note;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.Instant;
import java.util.Date;

@RunWith(JUnit4.class)
public class NoteDatabaseTest {
    private final String TAG = "JUnit";
    private TermManagerDbContext dbContext;
    private NoteDao noteDao;

    @Before
    public void createDb(){
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        dbContext = Room.inMemoryDatabaseBuilder(context, TermManagerDbContext.class)
                .build();
        noteDao = dbContext.noteDao();
        Log.i(TAG, "createDb");
    }

    @After
    public void closeDb(){
        dbContext.close();
        Log.i(TAG, "closeDb");
    }

    @Test
    public void insertAndRetrieve(){
        Course course = new Course(1, "Test Title", Date.from(Instant.now()), Date.from(Instant.now()), Course.PLAN_TO_TAKE, "Test name", "Test phone", "Test email");
        dbContext.courseDao().insertOrUpdate(course);

        Note note = new Note(1, "Test", 1);
        noteDao.insertOrUpdate(note);

        Note dbNote = noteDao.getNoteById(1);
        Assert.assertNotNull(dbNote);
    }

    @Test
    public void delete(){
        Course course = new Course(1, "Test Title", Date.from(Instant.now()), Date.from(Instant.now()), Course.PLAN_TO_TAKE, "Test name", "Test phone", "Test email");
        dbContext.courseDao().insertOrUpdate(course);

        Note note = new Note(1, "Test", 1);
        noteDao.insertOrUpdate(note);

        Note dbNote = noteDao.getNoteById(1);
        Assert.assertNotNull(dbNote);

        noteDao.delete(dbNote);
        dbNote = noteDao.getNoteById(1);
        Assert.assertNull(dbNote);
    }

    @Test
    public void modify(){
        String expectedNote = "Modified note";

        Course course = new Course(1, "Test Title", Date.from(Instant.now()), Date.from(Instant.now()), Course.PLAN_TO_TAKE, "Test name", "Test phone", "Test email");
        dbContext.courseDao().insertOrUpdate(course);

        Note note = new Note(1, "Test", 1);
        noteDao.insertOrUpdate(note);

        Note dbNote = noteDao.getNoteById(1);
        Assert.assertNotNull(dbNote);

        dbNote.setText(expectedNote);
        noteDao.insertOrUpdate(dbNote);

        Note modifiedNote = noteDao.getNoteById(1);
        Assert.assertEquals(expectedNote, modifiedNote.getText());
    }
}

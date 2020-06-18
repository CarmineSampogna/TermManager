package com.csampog.termmanager.dataAccess;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.csampog.termmanager.dataAccess.converters.DateConverter;
import com.csampog.termmanager.dataAccess.interfaces.AssessmentDao;
import com.csampog.termmanager.dataAccess.interfaces.CourseDao;
import com.csampog.termmanager.dataAccess.interfaces.NoteDao;
import com.csampog.termmanager.dataAccess.interfaces.TermDao;
import com.csampog.termmanager.model.Assessment;
import com.csampog.termmanager.model.Course;
import com.csampog.termmanager.model.Note;
import com.csampog.termmanager.model.Term;

@Database(entities = {Assessment.class, Course.class, Note.class, Term.class}, version = 3)
@TypeConverters({DateConverter.class})
public abstract class TermManagerDbContext extends RoomDatabase {
    public static final String DATABASE_NAME = "TermManager.db";
    private static volatile TermManagerDbContext instance;
    private static final Object LOCK = new Object();

    public abstract TermDao termDao();
    public abstract CourseDao courseDao();
    public abstract AssessmentDao assessmentDao();
    public abstract NoteDao noteDao();

    public static TermManagerDbContext getInstance(Context context) {
        if(instance == null){
            synchronized (LOCK){
                if(instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(), TermManagerDbContext.class, DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }
}

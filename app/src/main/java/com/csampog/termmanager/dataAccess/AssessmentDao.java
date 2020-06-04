package com.csampog.termmanager.dataAccess;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.csampog.termmanager.model.Assessment;

import java.util.List;

@Dao
public interface AssessmentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(Assessment assessment);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(List<Assessment> assessments);

    @Delete
    void deleteAssessment(Assessment assessment);

    @Query("SELECT * FROM Assessments")
    LiveData<List<Assessment>> getAllAssessments();

    @Query("SELECT * FROM Assessments WHERE assessmentId = :assessmentId")
    Assessment getAssessmentById(int assessmentId);

    @Query("SELECT * FROM Assessments WHERE courseId = :courseId")
    LiveData<List<Assessment>> getAssessmentsForCourse(int courseId);
}

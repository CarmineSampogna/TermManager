package com.csampog.termmanager.dataAccess.interfaces;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import com.csampog.termmanager.model.Assessment;
import java.util.List;

@Dao
public interface AssessmentDao extends EntityDao<Assessment>{

    @Query("SELECT * FROM Assessments")
    LiveData<List<Assessment>> getAllAssessments();

    @Query("SELECT * FROM Assessments WHERE assessmentId = :assessmentId")
    Assessment getAssessmentById(int assessmentId);

    @Query("SELECT * FROM Assessments WHERE courseId = :courseId")
    LiveData<List<Assessment>> getAssessmentsForCourse(int courseId);
}

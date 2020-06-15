package com.csampog.termmanager.dataAccess.repositories;

import android.content.Context;

import com.csampog.termmanager.model.Assessment;

import java.util.List;

import androidx.lifecycle.LiveData;

public class AssessmentRepository extends RepositoryBase {

    private static AssessmentRepository instance;

    public static AssessmentRepository getInstance(Context context) {
        if (instance == null) {
            instance = new AssessmentRepository(context);
        }
        return instance;
    }

    private AssessmentRepository(Context context) {
        super(context);
    }

    public LiveData<List<Assessment>> getAllAssessments() {
        return dbContext.assessmentDao().getAllAssessments();
    }


    public Assessment getAssessmentById(final int assessmentId) {
        final Assessment[] ret = {null};
        executor.execute(new Runnable() {
            @Override
            public void run() {
               ret[0] = dbContext.assessmentDao().getAssessmentById(assessmentId);
            }
        });

        return ret[0];
    }


    public LiveData<List<Assessment>> getAssessmentsForCourse(int courseId) {
        return dbContext.assessmentDao().getAssessmentsForCourse(courseId);
    }


    public void insertOrUpdate(final Assessment assessment) {
        if(assessment == null) throw new NullPointerException(Assessment.class.getName());

        final Assessment fAssessment = assessment;
        executor.execute(new Runnable() {
            @Override
            public void run() {
                dbContext.assessmentDao().insertOrUpdate(assessment);
            }
        });
    }

    public void insertOrUpdateAll(List<Assessment> assessments) {
        if(assessments == null) throw new NullPointerException(Assessment.class.getName());

        final List<Assessment> fAssessments = assessments;
        executor.execute(new Runnable() {
            @Override
            public void run() {
                dbContext.assessmentDao().insertOrUpdateAll(fAssessments);
            }
        });
    }

    public void delete(final Assessment assessment) {
        if(assessment == null) return;
        executor.execute(new Runnable() {

            @Override
            public void run() {
                dbContext.assessmentDao().delete(assessment);
            }
        });
    }
}

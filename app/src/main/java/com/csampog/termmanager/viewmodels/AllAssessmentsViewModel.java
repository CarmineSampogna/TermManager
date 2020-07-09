package com.csampog.termmanager.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.csampog.termmanager.dataAccess.repositories.AssessmentRepository;
import com.csampog.termmanager.model.Assessment;

import java.util.List;

public class AllAssessmentsViewModel extends AndroidViewModel {

    public LiveData<List<Assessment>> assessments;


    public AllAssessmentsViewModel(@NonNull Application application) {
        super(application);

        AssessmentRepository repository = AssessmentRepository.getInstance(application.getApplicationContext());
        assessments = repository.getAllAssessments();
    }
}

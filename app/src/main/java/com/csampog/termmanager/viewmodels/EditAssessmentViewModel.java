package com.csampog.termmanager.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.csampog.termmanager.dataAccess.repositories.AssessmentRepository;
import com.csampog.termmanager.model.Assessment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditAssessmentViewModel extends AndroidViewModel {

    public LiveData<String> title;
    private String titleInput;

    public MutableLiveData<String> formattedGoalDate;
    private Date dateInput;

    public MutableLiveData<Boolean> canSave;

    public LiveData<String> assessmentType;
    private String assessmentTypeInput;

    public LiveData<Boolean> alertsEnabled;
    private boolean alertsEnabledInput;

    private int assessmentId;
    private int courseId;
    private AssessmentRepository assessmentRepository;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");
    private LiveData<Assessment> assessment;


    public EditAssessmentViewModel(@NonNull Application application) {
        super(application);
        assessmentRepository = AssessmentRepository.getInstance(application);
        canSave = new MutableLiveData<>();
        formattedGoalDate = new MutableLiveData<>();

    }

    public Date getGoalDate() {
        return dateInput;
    }

    public void setAlertsEnabled(boolean alertsEnabled) {
        this.alertsEnabledInput = alertsEnabled;
    }

    public void setTitle(String titleInput) {

        this.titleInput = titleInput;
    }

    public void setCourseId(int courseId) {

        this.courseId = courseId;
        updateCanSave();
    }

    public void setAssessmentId(int assessmentId) {

        this.assessmentId = assessmentId;
        assessment = assessmentRepository.getAssessmentById(this.assessmentId);
        title = Transformations.map(assessment, a -> {
            formattedGoalDate.setValue(a != null ? dateFormat.format(a.getGoalDate()) : "");
            dateInput = a != null ? a.getGoalDate() : null;
            return a != null ? a.getTitle() : "";
        });

        alertsEnabled = Transformations.map(assessment, a -> {
            alertsEnabledInput = a != null ? a.getAlertsEnabled() : false;
            return a != null ? a.getAlertsEnabled() : false;
        });

        assessmentType = Transformations.map(assessment, a -> {
            String testType = a != null ? a.getTestType() : "";
            assessmentTypeInput = testType;
            return testType;
        });
    }

    public void setDate(int year, int month, int dayOfMonth) {

        MutableLiveData<String> target = formattedGoalDate;
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);

        dateInput = Date.from(calendar.toInstant());

        String formattedDate = dateFormat.format(dateInput);

        target.setValue(formattedDate);
        updateCanSave();
    }

    public void setAssessmentType(String assessmentType) {

        this.assessmentTypeInput = assessmentType;
        updateCanSave();
    }

    public void updateCanSave() {

        boolean isCourseIdValid = courseId > 0;

        boolean isTitleValid = titleInput != null &&
                titleInput.trim().length() > 2;

        boolean isDateValid = dateInput != null;

        boolean isAssessmentTypeValid = assessmentTypeInput != null;

        canSave.setValue(isCourseIdValid &&
                isTitleValid &&
                isDateValid &&
                isAssessmentTypeValid);
    }

    public void saveAssessment() {

        Assessment target = new Assessment();
        target.setTitle(titleInput.trim());
        target.setGoalDate(dateInput);
        target.setTestType(assessmentTypeInput);
        target.setCourseId(courseId);
        target.setAssessmentId(assessmentId);
        target.setAlertsEnabled(alertsEnabledInput);
        assessmentRepository.insertOrUpdate(target);
    }

    public void deleteAssessment() {

        Assessment target = new Assessment();
        target.setAssessmentId(assessmentId);
        assessmentRepository.delete(target);
    }
}

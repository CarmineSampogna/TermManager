package com.csampog.termmanager.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.csampog.termmanager.dataAccess.repositories.AssessmentRepository;
import com.csampog.termmanager.model.Assessment;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

public class AddAssessmentViewModel extends AndroidViewModel {

    public MutableLiveData<String> title;
    public MutableLiveData<String> titleInput;
    public MutableLiveData<String> formattedGoalDate;
    public MutableLiveData<Boolean> canSave;
    public MutableLiveData<Boolean> isReadOnly;
    public MutableLiveData<Boolean> alertsEnabled;

    private int courseId;
    private AssessmentRepository assessmentRepository;
    private Date goalDate;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");
    private String assessmentType;

    public AddAssessmentViewModel(@NonNull Application application) {
        super(application);
        title = new MutableLiveData<>();
        formattedGoalDate = new MutableLiveData<>();
        titleInput = new MutableLiveData<>();
        canSave = new MutableLiveData<>();
        isReadOnly = new MutableLiveData<>();
        alertsEnabled = new MutableLiveData<>();
        assessmentRepository = AssessmentRepository.getInstance(application);
    }

    public void setCourseId(int courseId) {

        this.courseId = courseId;
        updateCanSave();
    }

    public void setDate(int year, int month, int dayOfMonth) {

        MutableLiveData<String> target = formattedGoalDate;
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);

        Date selectedDate = Date.from(calendar.toInstant());

        String formattedDate = dateFormat.format(selectedDate);
        goalDate = selectedDate;

        target.setValue(formattedDate);
        updateCanSave();
    }

    public void setAssessmentType(String assessmentType) {

        this.assessmentType = assessmentType;
        updateCanSave();
    }

    public void updateCanSave() {

        boolean isCourseIdValid = courseId > 0;

        boolean isTitleValid = titleInput.getValue() != null &&
                titleInput.getValue().length() > 2;

        boolean isDateValid = goalDate != null &&
                goalDate.after(Date.from(Instant.now()));

        boolean isAssessmentTypeValid = assessmentType != null;

        canSave.setValue(isCourseIdValid &&
                isTitleValid &&
                isDateValid &&
                isAssessmentTypeValid);
    }

    public void saveAssessment() {

        Assessment target =  new Assessment();
        target.setTitle(titleInput.getValue());
        target.setGoalDate(goalDate);
        target.setTestType(assessmentType);
        target.setCourseId(courseId);
        target.setAlertsEnabled(alertsEnabled.getValue());
        assessmentRepository.insertOrUpdate(target);
    }
}

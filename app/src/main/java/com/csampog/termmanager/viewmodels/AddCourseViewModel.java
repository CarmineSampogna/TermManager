package com.csampog.termmanager.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.csampog.termmanager.model.Course;

import java.util.Calendar;
import java.util.Date;
import java.util.OptionalInt;

public class AddCourseViewModel extends CourseViewModelBase {

    public MutableLiveData<String> title;
    public MutableLiveData<String> formattedStartDate;
    public MutableLiveData<String> formattedEndDate;
    public MutableLiveData<String> mentorName;
    public MutableLiveData<String> mentorPhone;
    public MutableLiveData<String> mentorEmail;
    public MutableLiveData<Boolean> canSave;
    public MutableLiveData<String> status;
    public MutableLiveData<Boolean> startAlertEnabled;
    public MutableLiveData<Boolean> endAlertEnabled;

    public Date startDate;
    public Date endDate;


    private OptionalInt termId = null;

    public AddCourseViewModel(@NonNull Application application) {
        super(application);
        termId = OptionalInt.empty();
        title = new MutableLiveData<>();
        formattedStartDate = new MutableLiveData<>();
        formattedEndDate = new MutableLiveData<>();
        status = new MutableLiveData<>();
        mentorName = new MutableLiveData<>();
        mentorPhone = new MutableLiveData<>();
        mentorEmail = new MutableLiveData<>();
        canSave = new MutableLiveData<>();
        startAlertEnabled = new MutableLiveData<>();
        endAlertEnabled = new MutableLiveData<>();
        canSave.setValue(false);
        startAlertEnabled.setValue(true);
        endAlertEnabled.setValue(true);
    }

    public void setTermId(int termId) {

        this.termId = OptionalInt.of(termId);
    }

    public void setTitle(String title) {

        this.title.setValue(title);
        updateCanSave();
    }

    public void setStatus(String status) {
        this.status.setValue(status);
        updateCanSave();
    }

    public void setDate(int year, int month, int dayOfMonth, boolean isStartDate) {

        MutableLiveData<String> target = isStartDate ? formattedStartDate : formattedEndDate;
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);

        Date selectedDate = Date.from(calendar.toInstant());

        if (isStartDate) {
            startDate = selectedDate;
        } else {
            endDate = selectedDate;
        }

        String formattedDate = dateFormat.format(selectedDate);
        if (isStartDate) {
            startDate = selectedDate;
        } else {
            endDate = selectedDate;
        }

        target.setValue(formattedDate);
        updateCanSave();
    }

    public void updateCanSave() {

        boolean validTitle = title.getValue() != null &&
                title.getValue().trim().length() > 2;
        boolean validDates = endDate != null &&
                startDate != null &&
                endDate.after(startDate);
        boolean validStatus = status.getValue() != null;

        canSave.setValue(validTitle && validDates && validStatus);
    }

    public void createCourse() {

        if (canSave.getValue()) {
            canSave.setValue(false);


            String mentorNameValue = mentorName.getValue() == null ? null : mentorName.getValue().trim();
            String mentorPhoneValue = mentorPhone.getValue() == null ? null : mentorPhone.getValue().trim();
            String mentorEmailValue = mentorEmail.getValue() == null ? null : mentorEmail.getValue().trim();

            try {
                Course course = new Course(title.getValue().trim(),
                        startDate,
                        endDate,
                        status.getValue(),
                        mentorNameValue,
                        mentorPhoneValue,
                        mentorEmailValue,
                        startAlertEnabled.getValue(),
                        endAlertEnabled.getValue(),
                        startAlertEnabled.getValue(),
                        endAlertEnabled.getValue());

                if (termId.isPresent()) {
                    course.setTermId(termId.getAsInt());
                }

                courseRepository.insertOrUpdate(course);
            } catch (Exception ex) {
                Log.e(AddTermViewModel.class.getName(), ex.getMessage());
                throw ex;
            }
        }
    }
}

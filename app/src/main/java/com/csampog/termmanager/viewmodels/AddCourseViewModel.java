package com.csampog.termmanager.viewmodels;

import android.app.Application;
import android.util.Log;

import com.csampog.termmanager.NotificationWorkManager;
import com.csampog.termmanager.dataAccess.converters.DateConverter;
import com.csampog.termmanager.model.Course;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.OptionalInt;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class AddCourseViewModel extends CourseViewModelBase {

    public MutableLiveData<String> title;
    public MutableLiveData<String> formattedStartDate;
    public MutableLiveData<String> formattedEndDate;
    public MutableLiveData<String> mentorName;
    public MutableLiveData<String> mentorPhone;
    public MutableLiveData<String> mentorEmail;
    public MutableLiveData<Boolean> canSave;
    public MutableLiveData<String> status;
    public MutableLiveData<Boolean> alertsEnabled;


    private OptionalInt termId = null;

    public AddCourseViewModel(@NonNull Application application) {
        super(application);
        title = new MutableLiveData<>();
        formattedStartDate = new MutableLiveData<>();
        formattedEndDate = new MutableLiveData<>();
        status = new MutableLiveData<>();
        mentorName = new MutableLiveData<>();
        mentorPhone = new MutableLiveData<>();
        mentorEmail = new MutableLiveData<>();
        canSave = new MutableLiveData<>();
        alertsEnabled = new MutableLiveData<>();
        canSave.setValue(false);
        alertsEnabled.setValue(true);
    }

    public void setTermId(int termId) {

        this.termId = OptionalInt.of(termId);
    }

    public void setTitle(String title) {

        this.title.setValue(title);
    }

    public void setDate(int year, int month, int dayOfMonth, boolean isStartDate) {

        MutableLiveData<String> target = isStartDate ? formattedStartDate : formattedEndDate;
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);

        Date selectedDate = Date.from(calendar.toInstant());

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
                title.getValue().length() > 1;
        boolean validDates = endDate != null &&
                startDate != null &&
                endDate.after(startDate);
        boolean validStatus = status.getValue() != null;

        canSave.setValue(validTitle && validDates && validStatus);
    }

    public void createCourse() {

        if (canSave.getValue()) {
            canSave.setValue(false);


            String mentorNameValue = mentorName == null ? null : mentorName.getValue();
            String mentorPhoneValue = mentorPhone == null ? null : mentorPhone.getValue();
            String mentorEmailValue = mentorEmail == null ? null : mentorEmail.getValue();

            try {
                Course course = new Course(title.getValue(),
                        startDate,
                        endDate,
                        status.getValue(),
                        mentorNameValue,
                        mentorPhoneValue,
                        mentorEmailValue,
                        alertsEnabled.getValue(),
                        alertsEnabled.getValue(),
                        alertsEnabled.getValue());

                if (termId.isPresent()) {
                    course.setTermId(termId.getAsInt());
                }

                courseRepository.insertOrUpdate(course);
                int size = courseRepository.courses.getValue().size();

                long ts = startDate.toInstant().minus(1, ChronoUnit.DAYS).toEpochMilli();
                DateConverter.fromTimestamp(ts);

                    WorkManager wm = WorkManager.getInstance(getApplication());
                    PeriodicWorkRequest saveRequest =
                            new PeriodicWorkRequest.Builder(NotificationWorkManager.class, 15, TimeUnit.MINUTES)
                                    .setInitialDelay(1, TimeUnit.MINUTES)
                                    .build();
                    wm.enqueue(saveRequest);


            } catch (Exception ex) {
                Log.e(AddTermViewModel.class.getName(), ex.getMessage());
                throw ex;
            }
        }
    }
}

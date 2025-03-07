package com.csampog.termmanager.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.csampog.termmanager.dataAccess.repositories.CourseRepository;
import com.csampog.termmanager.model.Course;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditCourseViewModel extends AndroidViewModel {

    public LiveData<String> title;
    public String titleInput;

    public MutableLiveData<String> formattedStartDate;
    private Date startDateInput;

    public MutableLiveData<String> formattedEndDate;
    private Date endDateInput;

    public LiveData<String> status;
    public String statusInput;

    public LiveData<String> mentorName;
    public String mentorNameInput;

    public LiveData<String> mentorPhone;
    public String mentorPhoneInput;

    public LiveData<String> mentorEmail;
    public String mentorEmailInput;

    public MutableLiveData<Boolean> canSave;

    public LiveData<Boolean> startAlertEnabled;
    public Boolean startAlertEnabledInput;

    public LiveData<Boolean> endAlertEnabled;
    public Boolean endAlertEnabledInput;

    private boolean startAlertPending;
    private boolean endAlertPending;

    private LiveData<Course> course;
    private int courseId;

    public int termId;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");
    private CourseRepository courseRepository;
    public Date startDate;
    public Date endDate;

    public EditCourseViewModel(@NonNull Application application) {
        super(application);

        formattedStartDate = new MutableLiveData<>();
        formattedEndDate = new MutableLiveData<>();
        status = new MutableLiveData<>();
        canSave = new MutableLiveData<>();
        courseRepository = CourseRepository.getInstance(application);
        startAlertEnabledInput = true;
        endAlertEnabledInput = true;
    }

    public void setCourseId(int courseId){

        this.courseId = courseId;
        course = courseRepository.getCourseById(this.courseId);
        title = Transformations.map(course, c -> {
            formattedStartDate.setValue(dateFormat.format(c.getStartDate()));
            startDateInput = c.getStartDate();
            formattedEndDate.setValue(dateFormat.format(c.getAnticipatedEndDate()));
            endDateInput = c.getAnticipatedEndDate();
            termId = c.getTermId();
            titleInput = c.getTitle();
            startDate = c.getStartDate();
            endDate = c.getAnticipatedEndDate();
            mentorNameInput = c.getMentorName();
            mentorPhoneInput = c.getMentorPhone();
            mentorEmailInput = c.getMentorEmail();
            return c.getTitle();
        });

        status = Transformations.map(course, c -> {
            statusInput = c.getStatus();
            return c.getStatus();
        });

        mentorName = Transformations.map(course, c -> {
            mentorNameInput = c.getMentorName();
            return c.getMentorName();
        });
        mentorEmail = Transformations.map(course, c -> {
            mentorEmailInput = c.getMentorEmail();
            return c.getMentorEmail();
        });
        mentorPhone = Transformations.map(course, c -> {
            mentorPhoneInput = c.getMentorPhone();
            return c.getMentorPhone();
        });

        startAlertEnabled = Transformations.map(course, c -> {
            startAlertPending = c.getStartAlertPending();
            endAlertPending = c.getEndAlertPending();
            startAlertEnabledInput = c.getStartAlertEnabled();
            return c.getStartAlertEnabled();
        });

        endAlertEnabled = Transformations.map(course, c -> {
            endAlertPending = c.getEndAlertPending();
            endAlertEnabledInput = c.getEndAlertEnabled();
            return c.getEndAlertEnabled();
        });
    }

    public void saveCourse() {
        updateCanSave();
        if (canSave.getValue()) {
            canSave.setValue(false);

            try {
                Course modifiedCourse = new Course(courseId,
                        titleInput.trim(),
                        startDateInput,
                        endDateInput,
                        statusInput,
                        mentorNameInput != null ? mentorNameInput.trim() : null,
                        mentorPhoneInput != null ? mentorPhoneInput.trim() : null,
                        mentorEmailInput != null ? mentorEmailInput.trim() : null,
                        startAlertEnabledInput,
                        endAlertEnabledInput,
                        startAlertEnabledInput,
                        endAlertEnabledInput);

                modifiedCourse.setTermId(termId);
                courseRepository.insertOrUpdate(modifiedCourse);
            } catch (Exception ex) {
                Log.e(AddTermViewModel.class.getName(), ex.getMessage());
                throw ex;
            }
        }
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
            startDateInput = selectedDate;
        } else {
            endDateInput = selectedDate;
        }

        target.setValue(formattedDate);
        updateCanSave();
    }

    protected void updateCanSave() {

        boolean validTitle = titleInput != null && !titleInput.isEmpty() && titleInput.length() > 1;
        boolean validDates = endDateInput != null &&
                startDateInput != null &&
                endDateInput.after(startDateInput);
        boolean validStatus = statusInput != null;

        canSave.setValue(validTitle && validDates && validStatus);
    }
}

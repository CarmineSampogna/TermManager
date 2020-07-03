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

    private LiveData<Course> course;
    private int courseId;

    public int termId;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");
    private CourseRepository courseRepository;

    public EditCourseViewModel(@NonNull Application application) {
        super(application);

        formattedStartDate = new MutableLiveData<>();
        formattedEndDate = new MutableLiveData<>();
        status = new MutableLiveData<>();
        mentorName = new MutableLiveData<>();
        mentorEmail = new MutableLiveData<>();
        mentorPhone = new MutableLiveData<>();
        canSave = new MutableLiveData<>();
        courseRepository = CourseRepository.getInstance(application);
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

        status = Transformations.map(course, c -> c.getStatus());
    }

    public void saveCourse() {
        updateCanSave();
        if (canSave.getValue()) {
            canSave.setValue(false);

            try {
                Course modifiedCourse = new Course(courseId,
                        titleInput,
                        startDateInput,
                        endDateInput,
                        statusInput,
                        mentorNameInput,
                        mentorPhoneInput,
                        mentorEmailInput);

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

        boolean validTitle = titleInput != null && titleInput.length() > 1;
        boolean validDates = endDateInput != null &&
                startDateInput != null &&
                endDateInput.after(startDateInput);
        boolean validStatus = statusInput != null;

        canSave.setValue(validTitle && validDates && validStatus);
    }
}

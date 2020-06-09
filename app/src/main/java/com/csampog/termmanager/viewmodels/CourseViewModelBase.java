package com.csampog.termmanager.viewmodels;

import android.app.Application;

import com.csampog.termmanager.dataAccess.repositories.CourseRepository;

import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class CourseViewModelBase extends AndroidViewModel {

    protected Date startDate = null;
    protected Date endDate = null;
    protected CourseRepository courseRepository;
    protected SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");

    public CourseViewModelBase(@NonNull Application application) {
        super(application);

        courseRepository = CourseRepository.getInstance(application.getBaseContext());
    }
}

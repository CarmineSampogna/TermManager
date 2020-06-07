package com.csampog.termmanager.viewmodels;

import android.app.Application;

import com.csampog.termmanager.dataAccess.repositories.TermRepository;

import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public abstract class TermViewModelBase extends AndroidViewModel {

    protected Date startDate = null;
    protected Date endDate = null;
    protected TermRepository termRepository;
    protected SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");

    public TermViewModelBase(@NonNull Application application) {
        super(application);

        termRepository = TermRepository.getInstance(application.getBaseContext());
    }
}

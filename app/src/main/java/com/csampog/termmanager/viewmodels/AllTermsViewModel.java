package com.csampog.termmanager.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.csampog.termmanager.dataAccess.repositories.TermRepository;
import com.csampog.termmanager.model.Term;

import java.util.List;

public class AllTermsViewModel extends AndroidViewModel {

    private TermRepository repository;
    public LiveData<List<Term>> allTerms;

    public AllTermsViewModel(@NonNull Application application) {
        super(application);
        repository = TermRepository.getInstance(application.getBaseContext());
        allTerms = repository.terms;
        repository.AddSampleData();
    }
}

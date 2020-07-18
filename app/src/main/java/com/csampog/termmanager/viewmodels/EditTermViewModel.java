package com.csampog.termmanager.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.csampog.termmanager.model.Term;

import java.util.Calendar;
import java.util.Date;

public class EditTermViewModel extends TermViewModelBase {

    public LiveData<String> title;
    public String titleInput;

    public MutableLiveData<String> formattedStartDate;

    public MutableLiveData<String> formattedEndDate;

    public MutableLiveData<Boolean> canSave;
    public Date startDate;
    public Date endDate;

    private LiveData<Term> term;
    private int termId;

    public EditTermViewModel(@NonNull Application application) {

        super(application);
        formattedStartDate = new MutableLiveData<>();
        formattedEndDate = new MutableLiveData<>();
        canSave = new MutableLiveData<>();
    }

    public void setTermId(int termId){
        this.termId = termId;
        term = termRepository.getTermById(this.termId);
        title = Transformations.map(term, t -> {
            startDate = t.getStart();
            formattedStartDate.setValue(dateFormat.format(startDate));

            endDate = t.getEnd();
            formattedEndDate.setValue(dateFormat.format(t.getEnd()));
            return t.getTitle();
        });
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

    public void saveTerm(){

        Term term = new Term(termId, titleInput.trim(), startDate, endDate);
        termRepository.insertOrUpdate(term);
    }

    protected void updateCanSave() {

        boolean validTitle = title != null && title.getValue() != null && title.getValue().trim().length() > 2;
        boolean validDates = endDate != null &&
                startDate != null &&
                endDate.after(startDate);

        canSave.setValue(validTitle && validDates);
    }

}

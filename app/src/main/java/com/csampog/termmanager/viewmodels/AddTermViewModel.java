package com.csampog.termmanager.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.csampog.termmanager.model.Term;

import java.util.Calendar;
import java.util.Date;

public class AddTermViewModel extends TermViewModelBase {

    public MutableLiveData<String> title;
    public MutableLiveData<String> formattedStartDate;
    public MutableLiveData<String> formattedEndDate;
    public MutableLiveData<Boolean> canSave;
    public Date startDate;
    public Date endDate;

    public AddTermViewModel(@NonNull Application application) {
        super(application);

        title = new MutableLiveData<>();
        formattedStartDate = new MutableLiveData<>();
        formattedEndDate = new MutableLiveData<>();
        canSave = new MutableLiveData<>();
        canSave.setValue(false);
    }

    public void createTerm() {

        if(canSave.getValue()){
            canSave.setValue(false);
            try {
                Term newTerm = new Term(title.getValue(), startDate, endDate);
                termRepository.insertOrUpdate(newTerm);
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
            startDate = selectedDate;
        } else {
            endDate = selectedDate;
        }

        target.setValue(formattedDate);
        updateCanSave();
    }

    public void setTitle(String title) {

        this.title.setValue(title);
        updateCanSave();
    }

    protected void updateCanSave() {

        boolean validTitle = title != null && title.getValue() != null && title.getValue().length() > 2;
        boolean validDates = endDate != null &&
                startDate != null &&
                endDate.after(startDate);

        canSave.setValue(validTitle && validDates);
    }
}

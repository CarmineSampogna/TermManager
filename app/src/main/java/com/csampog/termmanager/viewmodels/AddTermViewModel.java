package com.csampog.termmanager.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.csampog.termmanager.dataAccess.repositories.TermRepository;
import com.csampog.termmanager.model.Term;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddTermViewModel extends AndroidViewModel {

    public MutableLiveData<String> formattedStartDate;
    public MutableLiveData<String> formattedEndDate;
    public MutableLiveData<Boolean> canSave;

    private String title = "";
    private Date startDate = null;
    private Date endDate = null;
    private TermRepository termRepository;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");

    public AddTermViewModel(@NonNull Application application) {
        super(application);
        formattedStartDate = new MutableLiveData<>();
        formattedEndDate = new MutableLiveData<>();
        canSave = new MutableLiveData<>();
        termRepository = TermRepository.getInstance(application.getBaseContext());
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

    public void createTerm() {

        if(canSave.getValue()){
            canSave.setValue(false);
            try {
                Term newTerm = new Term(title, startDate, endDate);
                termRepository.insertOrUpdate(newTerm);
            }catch (Exception ex){
                Log.e(AddTermViewModel.class.getName(), ex.getMessage());
                throw ex;
            }
        }
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
        updateCanSave();
    }

    private void updateCanSave() {

        boolean validTitle = title != null && title.length() > 1;
        boolean validDates = endDate != null &&
                startDate != null &&
                endDate.after(startDate);

        canSave.setValue(validTitle && validDates);
    }
}

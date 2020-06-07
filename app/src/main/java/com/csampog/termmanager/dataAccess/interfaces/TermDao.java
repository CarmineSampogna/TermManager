package com.csampog.termmanager.dataAccess.interfaces;

import com.csampog.termmanager.model.Term;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

@Dao
public interface TermDao extends EntityDao<Term> {

    @Query("SELECT * FROM Terms")
    LiveData<List<Term>> getAllTerms();

    @Query("SELECT * FROM Terms WHERE termId = :termId")
    LiveData<Term> getTermById(int termId);

    @Query("SELECT COUNT(*) FROM Terms")
    int getCount();
}

package com.csampog.termmanager.dataAccess.interfaces;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.csampog.termmanager.model.Term;

import java.util.List;

@Dao
public interface TermDao extends EntityDao<Term> {

    @Query("SELECT * FROM Terms")
    LiveData<List<Term>> getAllTerms();

    @Query("SELECT * FROM Terms WHERE termId = :termId")
    Term getTermById(int termId);

    @Query("SELECT COUNT(*) FROM Terms")
    int getCount();
}

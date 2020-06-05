package com.csampog.termmanager.dataAccess;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.csampog.termmanager.model.Term;

import java.util.List;

@Dao
public interface TermDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdateTerm(Term term);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Term> term);

    @Delete
    void deleteTerm(Term term);

    @Query("SELECT * FROM Terms")
    LiveData<List<Term>> getAllTerms();

    @Query("SELECT * FROM Terms WHERE termId = :termId")
    Term getTermById(int termId);

    @Query("SELECT COUNT(*) FROM Terms")
    int getCount();
}

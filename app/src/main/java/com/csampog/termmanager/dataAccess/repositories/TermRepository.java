package com.csampog.termmanager.dataAccess.repositories;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.csampog.termmanager.dataAccess.utilities.SampleData;
import com.csampog.termmanager.model.Term;

import java.util.List;

public class TermRepository extends RepositoryBase {

    private static TermRepository instance;

    public static TermRepository getInstance(Context context) {
        if (instance == null) {
            instance = new TermRepository(context);
        }
        return instance;
    }

    private TermRepository(Context context) {
        super(context);
        terms = getAllTerms();
    }

    public LiveData<List<Term>> terms;

    public void AddSampleData() {

        insertOrUpdateAll(SampleData.getTerms(4));
    }

    public void insertOrUpdate(Term term) {
        if (term == null) throw new NullPointerException(Term.class.getName());

        final Term fTerm = term;
        executor.execute(new Runnable() {
            @Override
            public void run() {
                dbContext.termDao().insertOrUpdate(fTerm);
            }
        });
    }

    public void insertOrUpdateAll(List<Term> terms) {
        if (terms == null) throw new NullPointerException(Term.class.getName());

        final List<Term> fTerms = terms;
        executor.execute(new Runnable() {
            @Override
            public void run() {
                dbContext.termDao().insertOrUpdateAll(fTerms);
            }
        });
    }

    public void delete(Term term) {
        final Term dTerm = term;
        if (term == null) throw new NullPointerException(Term.class.getName());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                dbContext.termDao().delete(dTerm);
            }
        });
    }

    private LiveData<List<Term>> getAllTerms() {

        return dbContext.termDao().getAllTerms();
    }

    public Term getTermById(final int termId) {

        final Term[] ret = {null};

        executor.execute(new Runnable() {
            @Override
            public void run() {
                ret[0] = dbContext.termDao().getTermById(termId);
            }
        });

        return ret[0];
    }
}

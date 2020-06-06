package com.csampog.termmanager.dataAccess.repositories;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.csampog.termmanager.dataAccess.TermManagerDbContext;
import com.csampog.termmanager.dataAccess.interfaces.TermDao;
import com.csampog.termmanager.model.Term;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TermRepository extends RepositoryBase {

    public TermRepository(Context context){
        super(context);
    }

    public void insertOrUpdate(Term term) {
        if(term == null) throw new NullPointerException(Term.class.getName());

        final Term fTerm = term;
        executor.execute(new Runnable() {
            @Override
            public void run() {
                dbContext.termDao().insertOrUpdate(fTerm);
            }
        });
    }

    public void insertOrUpdateAll(List<Term> terms) {
        if(terms == null) throw new NullPointerException(Term.class.getName());

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
        if(term == null) throw new NullPointerException(Term.class.getName());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                dbContext.termDao().delete(dTerm);
            }
        });
    }

    public LiveData<List<Term>> getAllTerms() {
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

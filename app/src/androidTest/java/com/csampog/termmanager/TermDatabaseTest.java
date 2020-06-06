package com.csampog.termmanager;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;

import com.csampog.termmanager.dataAccess.interfaces.TermDao;
import com.csampog.termmanager.dataAccess.TermManagerDbContext;
import com.csampog.termmanager.model.Term;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@RunWith(JUnit4.class)
public class TermDatabaseTest {

    private final String TAG = "JUnit";
    private TermManagerDbContext dbContext;
    private TermDao termDao;

    @Before
    public void createDb(){
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        dbContext = Room.inMemoryDatabaseBuilder(context, TermManagerDbContext.class)
                .build();
        termDao = dbContext.termDao();
        Log.i(TAG, "createDb");
    }


    @After
    public void closeDb(){
        dbContext.close();
        Log.i(TAG, "closeDb");
    }

    @Test
    public void createManyTerms(){
        List<Term> terms = new ArrayList<>();
        for(int i = 0; i < 3; i++){
            Term term = new Term(i + 1, String.valueOf(i), Date.from(Instant.now()), Date.from(Instant.now()));
            terms.add(term);
        }
        termDao.insertOrUpdateAll(terms);
        Assert.assertEquals(3, termDao.getCount());
    }

    @Test
    public void createAndModifyTerm(){
        Term term = new Term(10, "Test Term", Date.from(Instant.now()), Date.from(Instant.now()));
        termDao.insertOrUpdate(term);

        Term dbTerm = termDao.getTermById(10);
        Assert.assertNotNull(dbTerm);

        dbTerm.setTitle("Modified");
        termDao.insertOrUpdate(dbTerm);

        dbTerm = termDao.getTermById(10);
        Assert.assertNotNull(dbTerm);
        Assert.assertEquals("Modified", dbTerm.getTitle());
    }
    
    @Test
    public void deleteTerm(){
        
        Term term = new Term(10, "Test Term", Date.from(Instant.now()), Date.from(Instant.now()));
        termDao.insertOrUpdate(term);

        Assert.assertTrue(termDao.getCount() > 0);
        
        Term t = termDao.getTermById(10);

        termDao.delete(t);

        Assert.assertEquals(0, termDao.getCount());
    }
}

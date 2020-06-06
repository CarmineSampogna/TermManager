package com.csampog.termmanager.dataAccess.repositories;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.csampog.termmanager.dataAccess.TermManagerDbContext;
import com.csampog.termmanager.dataAccess.utilities.SampleData;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public abstract class RepositoryBase {

    protected TermManagerDbContext dbContext;
    protected Executor executor = Executors.newSingleThreadExecutor();

    public RepositoryBase(Context context){
       // dbContext = TermManagerDbContext.getInstance(context);
        dbContext = Room.inMemoryDatabaseBuilder(context, TermManagerDbContext.class).build();
        dbContext.termDao().insertOrUpdateAll(SampleData.getTerms(4));
    }
}

package com.csampog.termmanager.dataAccess.repositories;

import android.content.Context;

import com.csampog.termmanager.dataAccess.TermManagerDbContext;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public abstract class RepositoryBase {

    protected TermManagerDbContext dbContext;
    protected Executor executor = Executors.newSingleThreadExecutor();

    public RepositoryBase(Context context){
        dbContext = TermManagerDbContext.getInstance(context);
        //dbContext = Room.inMemoryDatabaseBuilder(context, TermManagerDbContext.class).build();
    }
}

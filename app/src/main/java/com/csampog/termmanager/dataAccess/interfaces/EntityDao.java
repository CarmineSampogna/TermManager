package com.csampog.termmanager.dataAccess.interfaces;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import java.util.List;

@Dao
public interface EntityDao<TEntity> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(TEntity entity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdateAll(List<TEntity> entity);

    @Delete
    void delete(TEntity entity);
}

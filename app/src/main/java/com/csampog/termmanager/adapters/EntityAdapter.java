package com.csampog.termmanager.adapters;

import android.content.Context;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public abstract class EntityAdapter<TEntity, TViewHolder extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<TViewHolder> {

    protected List<TEntity> entities;
    protected int viewLayoutId;

    public EntityAdapter(@NonNull Context context, int resource, @NonNull List<TEntity> entities) {
        super();
        viewLayoutId = resource;
        this.entities = entities;
    }

    @Override
    public void onBindViewHolder(@NonNull TViewHolder holder, int position) {
        TEntity entity = entities.get(position);
        bindEntity(entity, holder);
    }

    protected abstract void bindEntity(TEntity entity, TViewHolder viewHolder);
}

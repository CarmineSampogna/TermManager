package com.csampog.termmanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.csampog.termmanager.model.Term;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public abstract class EntityAdapter<TEntity> extends ArrayAdapter<TEntity> {

    protected List<TEntity> entities;
    protected int viewLayoutId;

    public EntityAdapter(@NonNull Context context, int resource, @NonNull TEntity[] objects) {
        super(context, resource, objects);
        viewLayoutId = resource;
        entities = Arrays.asList(objects);
    }

    @NonNull
    @Override
    public  View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        TEntity entity = this.entities.get(position);
        LayoutInflater inflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(viewLayoutId, parent, false);
        bindEntity(entity, view);
        return view;
    }

    protected abstract void bindEntity(TEntity entity, View view);
}

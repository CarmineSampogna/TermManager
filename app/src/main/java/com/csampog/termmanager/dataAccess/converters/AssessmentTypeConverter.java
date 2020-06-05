package com.csampog.termmanager.dataAccess.converters;

import androidx.room.TypeConverter;

import com.csampog.termmanager.model.Assessment;

public class AssessmentTypeConverter {

    @TypeConverter
    public static Assessment.AssessmentType fromString(String string){
        return Assessment.AssessmentType.valueOf(string.toUpperCase());
    }

    @TypeConverter
    public static String toString(Assessment.AssessmentType assessmentType){
        return assessmentType.toString();
    }
}

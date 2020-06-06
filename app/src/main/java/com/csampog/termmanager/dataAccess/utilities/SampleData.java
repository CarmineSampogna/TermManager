package com.csampog.termmanager.dataAccess.utilities;

import com.csampog.termmanager.model.Term;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SampleData {

    public static List<Term> getTerms(int count) {
        List<Term> ret = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            int num = i + 1;
            String title = "Term " + num;
            Date start = Date.from(Instant.now().plus(i * 6, ChronoUnit.MONTHS));
            Date end = Date.from(start.toInstant().plus(6, ChronoUnit.MONTHS));
            ret.add(new Term(i, title, start, end));
        }

        return ret;
    }
}

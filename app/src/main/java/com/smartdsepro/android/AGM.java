package com.smartdsepro.android;

/**
 * Created by mashnoor on 1/12/16.
 */
public class AGM {
    String companyName;
    String yearEnd;
    String dividend;
    String dateOfAgm;
    String recordDate;
    String venue;
    String time;

    public String getCompanyName() {
        return companyName;
    }

    public String getYearEnd() {
        return yearEnd;
    }

    public String getDividend() {
        return dividend;
    }

    public String getDateOfAgm() {
        return dateOfAgm;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public String getVenue() {
        return venue;
    }

    public String getTime() {
        return time;
    }

    public AGM(String companyName, String yearEnd, String dividend, String dateOfAgm, String recordDate, String venue, String time) {
        this.companyName = companyName;
        this.yearEnd = yearEnd;
        this.dividend = dividend;
        this.dateOfAgm = dateOfAgm;
        this.recordDate = recordDate;
        this.venue = venue;
        this.time = time;
    }


}

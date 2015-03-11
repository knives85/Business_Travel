package org.devfault.businesstravel.model;

import java.util.Date;

/**
 * Created by noi on 31/01/15.
 */
public class WorkDay {

    private Date workDate;
    private double mExpensesTot;
    private double mIncomeTot;

    public Date getWorkDate() {
        return workDate;
    }

    public void setWorkDate(Date workDate) {
        this.workDate = workDate;
    }

    public double getExpensesTot() {
        return mExpensesTot;
    }

    public void setExpensesTot(double mExpensesTot) {
        this.mExpensesTot = mExpensesTot;
    }

    public double getIncomeTot() {
        return mIncomeTot;
    }

    public void setIncomeTot(double mIncomeTot) {
        this.mIncomeTot = mIncomeTot;
    }
}

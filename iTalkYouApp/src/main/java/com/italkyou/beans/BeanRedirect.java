package com.italkyou.beans;

import java.io.Serializable;

/**
 * Created by rsantillanc on 19/08/2015.
 */
public class BeanRedirect implements Serializable {

    //Static
    private static final long serialVersionUID = 1L;

    //Attributes
    private int redirectId;
    private long phoneNumber;
    private int zipcode;
    private int userId;
    private String fromDate;
    private String toDate;
    private int flagPermanent;
    private int flagActive;


    //Contructors

    public BeanRedirect() {
    }

    public BeanRedirect(
            long phoneNumber,
            int zipcode,
            int userId,
            String fromDate,
            String toDate,
            int flagPermanent,
            int flagActive
    ) {
        this.phoneNumber = phoneNumber;
        this.zipcode = zipcode;
        this.userId = userId;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.flagPermanent = flagPermanent;
        this.flagActive = flagActive;
    }


    //Custom methods
    @Override
    public String toString(){
        String console;

        console =  "\n\n";
        console +=  "+-------------------- BeanRedirect --------------------\n";
        console += "| redirectId: " +redirectId+ "\n";
        console += "| phoneNumber: " +phoneNumber+ "\n";
        console += "| zipcode: " +zipcode+ "\n";
        console += "| userId: " +userId+ "\n";
        console += "| fromDate: " +fromDate+ "\n";
        console += "| toDate: " +toDate+ "\n";
        console += "| flagActive: " +flagActive+ "\n";
        console += "| flagPermanent: " +flagPermanent+ "\n";
        console += "+-------------------- BeanRedirect --------------------\n";

        return console;
    }

    //Set and get methods

    public int getRedirectId() {
        return redirectId;
    }

    public void setRedirectId(int redirectId) {
        this.redirectId = redirectId;
    }

    public int getZipcode() {
        return zipcode;
    }

    public void setZipcode(int zipcode) {
        this.zipcode = zipcode;
    }

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public int getFlagPermanent() {
        return flagPermanent;
    }

    public void setFlagPermanent(int flagPermanent) {
        this.flagPermanent = flagPermanent;
    }

    public int getFlagActive() {
        return flagActive;
    }

    public void setFlagActive(int flagActive) {
        this.flagActive = flagActive;
    }
}

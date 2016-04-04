package com.italkyou.beans.popup;

import com.italkyou.utils.Const;

import java.io.Serializable;

/**
 * Created by RenzoD on 05/06/2015.
 */
public class MenuActionsContact implements Serializable {

    private String title;
    private int image;
    private int contactID;
    private String lookUpKey;
    private String numbers;
    private String[] phonesToCall;

    public MenuActionsContact(int image, String title, int contactID, String lookUpKey, String numbers) {
        this.lookUpKey = lookUpKey;
        this.contactID = contactID;
        this.image = image;
        this.title = title;
        this.numbers = numbers;
        phonesToCall = numbers.split(String.valueOf(Const.CHAR_COMMA));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getContactID() {
        return contactID;
    }

    public void setContactID(int contactID) {
        this.contactID = contactID;
    }

    public String getLookUpKey() {
        return lookUpKey;
    }

    public void setLookUpKey(String lookUpKey) {
        this.lookUpKey = lookUpKey;
    }

    public String[] getNumbers() {
        return phonesToCall;
    }

    public int count() {
        return phonesToCall.length;
    }
}

package com.italkyou.beans.popup;

import java.io.Serializable;

/**
 * Created by RenzoD on 05/06/2015.
 */
public class MenuActionsChat implements Serializable{

    private String title;
    private String name;
    private String annex;
    private int imageAction;
    private int imageStatus;
    private boolean fagStatus;


    public MenuActionsChat(String title, int image) {
        this.title = title;
        this.imageAction = image;
    }

    public MenuActionsChat(String name, String annex, int image, int imageStatus,boolean isOnline) {
        this.name = name;
        this.annex = annex;
        this.imageAction = image;
        this.imageStatus = imageStatus;
        this.fagStatus = isOnline;
    }

    public boolean isFagStatus() {
        return fagStatus;
    }

    public void setFagStatus(boolean fagStatus) {
        this.fagStatus = fagStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAnnex() {
        return annex;
    }

    public void setAnnex(String annex) {
        this.annex = annex;
    }

    public int getImageStatus() {
        return imageStatus;
    }

    public void setImageStatus(int imageStatus) {
        this.imageStatus = imageStatus;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImageAction() {
        return imageAction;
    }

    public void setImageAction(int imageAction) {
        this.imageAction = imageAction;
    }
}

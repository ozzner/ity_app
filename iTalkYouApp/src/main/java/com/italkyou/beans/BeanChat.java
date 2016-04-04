package com.italkyou.beans;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.io.Serializable;
import java.util.Date;

@ParseClassName("BeanChat")
public class BeanChat extends ParseObject implements Serializable {

    /**
     * rsantillanc@gmail.com
     */
    private static final long serialVersionUID = 1L;
    private String objectId;
    private Date createdAt;
    private Date updatedAt;


    public BeanChat() {

    }

    @Override
    public String getObjectId() {
        return objectId;
    }

    @Override
    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}

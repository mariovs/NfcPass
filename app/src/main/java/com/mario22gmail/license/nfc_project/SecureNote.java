package com.mario22gmail.license.nfc_project;

import java.io.Serializable;

/**
 * Created by Mario Vasile on 5/23/2016.
 */
public class SecureNote implements Serializable{

    public SecureNote(String subject, String message)
    {
        this.subject = subject;
        this.message = message;
    }

    private String subject;
    private String message;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

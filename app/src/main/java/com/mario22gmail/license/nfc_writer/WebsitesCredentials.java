package com.mario22gmail.license.nfc_writer;

import java.io.Serializable;

/**
 * Created by Mario Vasile on 3/28/2016.
 */
public class WebsitesCredentials implements Serializable{
    private String url;
    private String userName;
    private String password;

    public WebsitesCredentials()
    {
    }

    public WebsitesCredentials(String url, String userName, String password)
    {
        this.url = url;
        this.userName = userName;
        this.password = password;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {

        return url;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
}

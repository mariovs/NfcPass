package com.mario22gmail.license.nfc_project;

/**
 * Created by Mario Vasile on 5/1/2016.
 */
public class AuthResponse {


    private boolean isValid;
    private String ErrorMessage;

    public boolean isValid() {
        return isValid;
    }

    public void setIsValid(boolean isValid) {
        this.isValid = isValid;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        ErrorMessage = errorMessage;
    }
}

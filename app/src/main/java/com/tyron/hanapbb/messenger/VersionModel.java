package com.tyron.hanapbb.messenger;

import androidx.annotation.NonNull;

public class VersionModel {

    private double version;
    private double min_version;
    private String message;
    private boolean required = true;
    private String update_link;

    public VersionModel(){

    }

    public VersionModel(double version, double min_version, String message, boolean required, String update_link){
        this.version = version;
        this.min_version = version;
        this.message = message;
        this.required = required;
        this.update_link = update_link;
    }

    public String getUpdate_link(){
        return update_link;
    }

    public boolean isRequired() {
        return required;
    }

    public String getMessage() {
        return message;
    }

    public double getMin_version() {
        return min_version;
    }

    @NonNull
    public double getVersion() {
        return version;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setVersion(double version) {
        this.version = version;
    }

    public void setMin_version(double min_version) {
        this.min_version = min_version;
    }

    public void setRequired(boolean required){
        this.required = required;
    }
}

package com.tyron.hanapbb.ui.models;

public class UserModel {
    private String photoUrl;
    private String name;
    private String username;
    private int age;
    private String gender;
    private String preferredGender;
    private String uid;

    public UserModel(){

    }
    public UserModel(String photoUrl, String name, String username, int age, String gender, String preferredGender, String uid){
        this.photoUrl = photoUrl;
        this.name = name;
        this.username = username;
        this.age = age;
        this.gender = gender;
        this.preferredGender = preferredGender;
        this.uid = uid;
    }

    public String getUid(){
        return uid;
    }
    public void setUid(String uid){
        this.uid = uid;
    }
    public int getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getName() {
        return name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getPreferredGender() {
        return preferredGender;
    }

    public String getUsername() {
        return username;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setPreferredGender(String preferredGender) {
        this.preferredGender = preferredGender;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

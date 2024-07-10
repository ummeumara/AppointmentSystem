package com.example.appointmentsystem;

public class Doctor {
    private String id;
    private String name;
    private String specialty;
    private String profileImage;

    // Default constructor required for calls to DataSnapshot.getValue(Doctor.class)
    public Doctor() {
    }

    public Doctor(String id, String name, String specialty, String profileImage) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
        this.profileImage = profileImage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
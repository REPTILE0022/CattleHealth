package com.example.cattlehealth;

public class Appointment {

    private String date;
    private String location;
    private String animalKind;
    private String age;
    private String contactInfo;
    private String name;

    // Default constructor required for calls to DataSnapshot.getValue(Appointment.class)
    public Appointment() {
    }
    public Appointment(String date, String location, String animalKind, String age, String contactInfo, String name) {
        this.date = date;
        this.location = location;
        this.animalKind = animalKind;
        this.age = age;
        this.contactInfo = contactInfo;
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public String getAnimalKind() {
        return animalKind;
    }

    public String getAge() {
        return age;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public String getName() {
        return name;
    }
}

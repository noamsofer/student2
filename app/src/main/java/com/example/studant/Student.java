package com.example.studant;

public class Student {
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String grade;
    private final String major;
    private final boolean notifications;
    private final boolean termsAccepted;

    public Student(String firstName, String lastName, String email, String grade,
                   String major, boolean notifications, boolean termsAccepted) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.grade = grade;
        this.major = major;
        this.notifications = notifications;
        this.termsAccepted = termsAccepted;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getGrade() {
        return grade;
    }

    public String getMajor() {
        return major;
    }

    public boolean isNotifications() {
        return notifications;
    }

    public boolean isTermsAccepted() {
        return termsAccepted;
    }
}

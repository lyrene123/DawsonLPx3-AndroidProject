package com.dawsonlpx3.data;


import java.util.HashMap;
import java.util.Map;

//https://www.firebase.com/docs/android/guide/saving-data.html
public class TeacherDetails {

    private String email;
    private String first_name;
    private String full_name;
    private String last_name;
    private String local;
    private String office;
    private Map<String, Map<String, String>> departments;
    private Map<String, Map<String, String>> positions;
    private Map<String, Map<String, String>> sectors;

    public TeacherDetails(){
        this("","","","","","",
                new HashMap<String, Map<String, String>>(), new HashMap<String, Map<String, String>>(),
                new HashMap<String, Map<String, String>>());
    }

    public TeacherDetails(String email, String first_name, String full_name, String last_name,
                          String local, String office, Map<String, Map<String, String>> departments,
                          Map<String, Map<String, String>> positions,
                          Map<String, Map<String, String>> sectors) {
        this.email = email;
        this.first_name = first_name;
        this.full_name = full_name;
        this.last_name = last_name;
        this.local = local;
        this.office = office;
        this.departments = departments;
        this.positions = positions;
        this.sectors = sectors;
    }

    public String getEmail() {
        return email;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getFull_name() {
        return full_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getLocal() {
        return local;
    }

    public String getOffice() {
        return office;
    }

    public Map<String, Map<String, String>> getDepartments() {
        return departments;
    }

    public Map<String, Map<String, String>> getPositions() {
        return positions;
    }

    public Map<String, Map<String, String>> getSectors() {
        return sectors;
    }
}

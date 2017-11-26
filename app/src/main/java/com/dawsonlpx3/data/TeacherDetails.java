package com.dawsonlpx3.data;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * Encapsulates the properties and characteristics to represent a Teacher
 * in the DawsonLPx3 application and represents a teacher record from the firebase.
 *
 * @author Lyrene Labor
 * @author Peter Bellefleur
 * @author Phil Langlois
 * @author Pengkim Sy
 */
public class TeacherDetails implements Serializable{

    private String email;
    private String first_name;
    private String full_name;
    private String last_name;
    private String local;
    private String office;
    private String website;
    private String bio;
    private String image;
    private List<Object> departments;
    private List<Object> positions;
    private List<Object> sectors;

    /**
     * Default constructor to initialize the properties
     */
    public TeacherDetails(){
        this("","","","","","","","","",
                new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>());
    }

    /**
     * Constructor to initialize the properties with the input values
     * The idea of using maps came from
     * //https://www.firebase.com/docs/android/guide/saving-data.html
     *
     * @param email Teacher's email
     * @param first_name Teacher's first name
     * @param full_name Teacher's full name
     * @param last_name Teacher's last name
     * @param local Teacher's local number extension
     * @param office Teacher's office number
     * @param departments Teacher's departments
     * @param positions Teacher's positions
     * @param sectors Teacher's sectors
     */
    public TeacherDetails(String email, String first_name, String full_name, String last_name,
                          String local, String office, String website, String image, String bio,
                          List<Object> departments,
                          List<Object> positions,
                          List<Object> sectors) {

        this.email = email;
        this.first_name = first_name;
        this.full_name = full_name;
        this.last_name = last_name;
        this.local = local;
        this.office = office;
        this.departments = departments;
        this.positions = positions;
        this.sectors = sectors;
        this.image = image;
        this.bio = bio;
        this.website = website;
    }

    /**
     * Returns the website
     *
     * @return String
     */
    public String getWebsite() {
        return website;
    }

    /**
     * Sets the website string
     *
     * @param website
     */
    public void setWebsite(String website) {
        this.website = website;
    }

    /**
     * Returns the bio of the teacher
     *
     * @return String
     */
    public String getBio() {
        return bio;
    }

    /**
     * Sets the Bio of the teacher
     *
     * @param bio
     */
    public void setBio(String bio) {
        this.bio = bio;
    }

    /**
     * Returns the image of the teacher
     *
     * @return String image url
     */
    public String getImage() {
        return image;
    }

    /**
     * Sets the image of the teacher
     *
     * @param image
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * Returns the teacher's email
     *
     * @return String email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the teacher's first name
     *
     * @return String first name
     */
    public String getFirst_name() {
        return first_name;
    }

    /**
     * Returns the teacher's full name
     *
     * @return String full name
     */
    public String getFull_name() {
        return full_name;
    }

    /**
     * Returns the teacher's last name
     *
     * @return String last name
     */
    public String getLast_name() {
        return last_name;
    }

    /**
     * Returns the local of the teacher
     *
     * @return String local
     */
    public String getLocal() {
        return local;
    }

    /**
     * Returns the teacher's office
     *
     * @return String office number
     */
    public String getOffice() {
        return office;
    }

    /**
     * Returns the list of teacher's departments
     *
     * @return Map object
     */
    public List<Object> getDepartments() {
        return departments;
    }

    /**
     * Returns the list of teacher's positions
     *
     * @return Map object
     */
    public List<Object> getPositions() {
        return positions;
    }

    /**
     * Returns the list of teacher's sectors
     *
     * @return Map object
     */
    public List<Object> getSectors() {
        return sectors;
    }
}

package com.example.alhanoufaldawood.conlang.Customer;

public class Translator {

    private String name;
    private String rate;
    private String language;
    private String field;
    private String bio;
    private String education;
    private String providedLanguage;
    private String edit;
    private String type;
    private String image;
    private String AvrRate ;
    private String numOfRate ;



    public Translator() {

    }


    public Translator(String name, String rate, String language, String field, String bio, String education, String providedLanguage,String edit,String type, String image,String numOfRate,String AvrRate) {

        this.name = name;
        this.rate = rate;
        this.language = language;
        this.field = field;
        this.bio = bio;
        this.education = education;
        this.providedLanguage = providedLanguage;
        this.edit =edit;
        this.type = type;
        this.image = image;
         this.AvrRate=AvrRate ;
         this.numOfRate=numOfRate ;


    }



    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getProvidedLanguage() {
        return providedLanguage;
    }

    public String getedit() {
        return edit;
    }

    public void setEdit(String edit) {
        this.edit = edit;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setProvidedLanguage(String providedLanguage) {
        this.providedLanguage = providedLanguage;


    }
    public String getAvrRate() {
        return AvrRate;
    }

    public void setAvrRate(String avrRate) {
        AvrRate = avrRate;
    }

    public String getNumOfRate() {
        return numOfRate;
    }

    public void setNumOfRate(String numOfRate) {
        this.numOfRate = numOfRate;
    }


}
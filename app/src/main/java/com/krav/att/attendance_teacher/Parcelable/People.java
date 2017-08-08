package com.krav.att.attendance_teacher.Parcelable;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

/**
 * Created by 01547598 on 7/26/2017.
 */

public class People implements Parcelable {

    private Long peopleID;
    private String countryID;
    private String name;
    private LocalDate birthDate;
    private Long enrollmentNumber;
    private LocalDate gradeDate;
    private String email;
    private String celphone;
    private String phone;
    private String address1;
    private String address2;
    private String postalCode;
    private String bloodType;
    private String allergy;
    private String allergyDesc;
    private LocalDate nextGradeExam;
    private String whereOther;
    private String lookingOther;
    private String password;
    private Integer enrTypeID;
    private String oAuth;
    private LocalDateTime oAuthDate;
    private String userAgent;
    private String regionID;
    private Integer gradeID;
    private String birthDateS;
    private String gradeDateS;
    private String nextGradeExamS;
    private Integer genderID;
    private Integer whereID;
    private Integer lookID;

    protected People(Parcel in) {
        peopleID= in.readLong();
        countryID= in.readString();
        name= in.readString();
        String sBirthDate = in.readString();
        if (sBirthDate != null)
            birthDate = LocalDate.parse(sBirthDate);
        enrollmentNumber= in.readLong();
        String sGradeDate = in.readString();
        if (sGradeDate != null)
            gradeDate = LocalDate.parse(sGradeDate);
        email= in.readString();
        celphone= in.readString();
        phone= in.readString();
        address1= in.readString();
        address2= in.readString();
        postalCode= in.readString();
        bloodType= in.readString();
        allergy= in.readString();
        allergyDesc= in.readString();
        String sNextGradeExam = in.readString();
        if (sNextGradeExam != null)
            nextGradeExam = LocalDate.parse(sNextGradeExam);
        whereOther= in.readString();
        lookingOther= in.readString();
        password= in.readString();
        enrTypeID= in.readInt();
        oAuth= in.readString();
        String sOAuthDate = in.readString();
        if (sOAuthDate != null)
            oAuthDate = LocalDateTime.parse(sOAuthDate);
        userAgent= in.readString();
        regionID= in.readString();
        gradeID= in.readInt();
        birthDateS= in.readString();
        gradeDateS= in.readString();
        nextGradeExamS= in.readString();
        genderID= in.readInt();
        whereID= in.readInt();
        lookID= in.readInt();
    }

    public static final Creator<People> CREATOR = new Creator<People>() {
        @Override
        public People createFromParcel(Parcel in) {
            return new People(in);
        }

        @Override
        public People[] newArray(int size) {
            return new People[size];
        }
    };

    public Long getPeopleID() {
        return peopleID;
    }

    public void setPeopleID(Long peopleID) {
        this.peopleID = peopleID;
    }

    public String getCountryID() {
        return countryID;
    }

    public void setCountryID(String countryID) {
        this.countryID = countryID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirthDate() {
        if (birthDate != null)
            return birthDate;
        else
            return LocalDate.now();
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Long getEnrollmentNumber() {
        return enrollmentNumber;
    }

    public void setEnrollmentNumber(Long enrollmentNumber) {
        this.enrollmentNumber = enrollmentNumber;
    }

    public LocalDate getGradeDate() {
        if (gradeDate != null)
            return gradeDate;
        else
            return LocalDate.now();
    }

    public void setGradeDate(LocalDate gradeDate) {
        this.gradeDate = gradeDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCelphone() {
        return celphone;
    }

    public void setCelphone(String celphone) {
        this.celphone = celphone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getAllergy() {
        return allergy;
    }

    public void setAllergy(String allergy) {
        this.allergy = allergy;
    }

    public String getAllergyDesc() {
        return allergyDesc;
    }

    public void setAllergyDesc(String allergyDesc) {
        this.allergyDesc = allergyDesc;
    }

    public LocalDate getNextGradeExam() {
        if (nextGradeExam != null)
            return nextGradeExam;
        else
            return LocalDate.now();
    }

    public void setNextGradeExam(LocalDate nextGradeExam) {
        this.nextGradeExam = nextGradeExam;
    }

    public String getWhereOther() {
        return whereOther;
    }

    public void setWhereOther(String whereOther) {
        this.whereOther = whereOther;
    }

    public String getLookingOther() {
        return lookingOther;
    }

    public void setLookingOther(String lookingOther) {
        this.lookingOther = lookingOther;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getEnrTypeID() {
        return enrTypeID;
    }

    public void setEnrTypeID(Integer enrTypeID) {
        this.enrTypeID = enrTypeID;
    }

    public String getoAuth() {
        return oAuth;
    }

    public void setoAuth(String oAuth) {
        this.oAuth = oAuth;
    }

    public LocalDateTime getoAuthDate() {
        return oAuthDate;
    }

    public void setoAuthDate(LocalDateTime oAuthDate) {
        this.oAuthDate = oAuthDate;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getRegionID() {
        return regionID;
    }

    public void setRegionID(String regionID) {
        this.regionID = regionID;
    }

    public Integer getGradeID() {
        return gradeID;
    }

    public void setGradeID(Integer gradeID) {
        this.gradeID = gradeID;
    }

    public String getBirthDateS() {
        return birthDateS;
    }

    public void setBirthDateS(String birthDateS) {
        this.birthDateS = birthDateS;
    }

    public String getGradeDateS() {
        return gradeDateS;
    }

    public void setGradeDateS(String gradeDateS) {
        this.gradeDateS = gradeDateS;
    }

    public String getNextGradeExamS() {
        return nextGradeExamS;
    }

    public void setNextGradeExamS(String nextGradeExamS) {
        this.nextGradeExamS = nextGradeExamS;
    }

    public Integer getGenderID() {
        return genderID;
    }

    public void setGenderID(Integer genderID) {
        this.genderID = genderID;
    }

    public Integer getWhereID() {
        return whereID;
    }

    public void setWhereID(Integer whereID) {
        this.whereID = whereID;
    }

    public Integer getLookID() {
        return lookID;
    }

    public void setLookID(Integer lookID) {
        this.lookID = lookID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(peopleID);
        dest.writeString(countryID);
        dest.writeString(name);
        dest.writeString(birthDate != null ? birthDate.toString() : null);
        dest.writeLong(enrollmentNumber);
        dest.writeString(gradeDate != null ? gradeDate.toString() : null);
        dest.writeString(email);
        dest.writeString(celphone);
        dest.writeString(phone);
        dest.writeString(address1);
        dest.writeString(address2);
        dest.writeString(postalCode);
        dest.writeString(bloodType);
        dest.writeString(allergy);
        dest.writeString(allergyDesc);
        dest.writeString(nextGradeExam != null ? nextGradeExam.toString() : null);
        dest.writeString(whereOther);
        dest.writeString(lookingOther);
        dest.writeString(password);
        dest.writeInt(enrTypeID);
        dest.writeString(oAuth);
        dest.writeString(oAuthDate != null ? oAuthDate.toString() : null);
        dest.writeString(userAgent);
        dest.writeString(regionID);
        dest.writeInt(gradeID);
        dest.writeString(birthDateS);
        dest.writeString(gradeDateS);
        dest.writeString(nextGradeExamS);
        dest.writeInt(genderID);
        dest.writeInt(whereID);
        dest.writeInt(lookID);

    }
}

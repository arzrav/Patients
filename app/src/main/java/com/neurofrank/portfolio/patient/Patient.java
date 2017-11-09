package com.neurofrank.portfolio.patient;

import android.content.Context;

import com.neurofrank.portfolio.R;

import org.joda.time.LocalDate;
import org.joda.time.Years;

import java.util.UUID;

public class Patient {

    private UUID mId;
    private String mFirstName;
    private String mMiddleName;
    private String mLastName;
    private String mMobilePhone;
    private String mEmail;
    private String mHistory;
    private LocalDate mBirthday;

    public Patient() {
        //Генерирование уникального идентификатора
        this(UUID.randomUUID());
    }

    public Patient(UUID id) {
        mId = id;
    }

    public UUID getId() {
        return mId;
    }

    public String getFullName() {
        return mLastName + ' ' + mFirstName + ' ' + mMiddleName;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public String getMiddleName() {
        return mMiddleName;
    }

    public void setMiddleName(String middleName) {
        mMiddleName = middleName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    public String getPluralAge(Context context) {
        return context.getResources().getQuantityString(R.plurals.plurals_age, getAge(), getAge());
    }

    public int getAge() {
        return Years.yearsBetween(mBirthday, new LocalDate()).getYears();
    }

    public LocalDate getBirthday() {
        return mBirthday;
    }

    public void setBirthday(LocalDate birthday) {
        mBirthday = birthday;
    }

    public String getMobilePhone() {
        return mMobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        mMobilePhone = mobilePhone;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getHistory() {
        return mHistory;
    }

    public void setHistory(String history) {
        mHistory = history;
    }
}

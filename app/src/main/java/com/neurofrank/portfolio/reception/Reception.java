package com.neurofrank.portfolio.reception;

import org.joda.time.LocalDate;

import java.util.UUID;

public class Reception {

    private UUID mId;
    private UUID mPatientId;
    private String mTitle;
    private String mHistory;
    private LocalDate mDate;

    public Reception(UUID patientId) {
        this(UUID.randomUUID(), patientId);
    }

    public Reception(UUID id, UUID patientId) {
        mId = id;
        mPatientId = patientId;
    }

    public UUID getId() {
        return mId;
    }

    public UUID getPatientId() {
        return mPatientId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public LocalDate getDate() {
        return mDate;
    }

    public void setDate(LocalDate date) {
        mDate = date;
    }

    public String getHistory() {
        return mHistory;
    }

    public void setHistory(String history) {
        mHistory = history;
    }
}

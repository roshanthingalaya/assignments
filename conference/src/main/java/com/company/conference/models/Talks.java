package com.company.conference.models;

import com.company.conference.enums.SessionType;

import java.io.Serializable;
import java.time.LocalTime;

public class Talks implements Serializable, Comparable<Talks> {

    private static final long serialVersionUID = 1234567L;

    Integer id;
    LocalTime startTime;
    LocalTime endTime;
    SessionType type;
    String session;
    Integer duration;

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public SessionType getType() {
        return type;
    }

    public void setType(SessionType type) {
        this.type = type;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }



    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }


    @Override
    public int compareTo(Talks o) {
        return this.startTime.compareTo(o.startTime);
    }
}

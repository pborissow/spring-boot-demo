package com.example.demo.persistance;

import java.util.*;
import java.time.*;

import jakarta.persistence.*;


@Entity
@Table(name = "request")
public class RequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "request_id_seq")
    @SequenceGenerator(name = "request_id_seq", sequenceName = "request_id_seq",  allocationSize=1)
    @Column(name = "id")
    private Long id;
    private OffsetDateTime date;
    private String domain;
    private String path;
    private String clientIP;
    private String userAgent;

    public void setDate(OffsetDateTime date) {
        this.date = date;
    }

    public void setDate(Date date) {
        if (date==null) this.date = null;
        else{
            int timeZoneOffset = date.getTimezoneOffset();
            ZoneOffset zoneOffset = ZoneOffset.ofTotalSeconds(-1*timeZoneOffset*60);
            this.date = OffsetDateTime.ofInstant(date.toInstant(), zoneOffset);
        }
    }

    public Date getDate() {
        if (date==null) return null;
        return Date.from(date.toInstant());
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setClientIP(String clientIP) {
        this.clientIP = clientIP;
    }

    public String getClientIP() {
        return clientIP;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getUserAgent() {
        return userAgent;
    }
}
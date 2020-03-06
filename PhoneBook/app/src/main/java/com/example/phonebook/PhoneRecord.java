package com.example.phonebook;

public class PhoneRecord {
    public final String number;
    public final long date;
    public final int type;
    public final String location;
    public PhoneRecord(String number,long date,int type,String location)
    {
        this.number = number;
        this.date = date;
        this.type = type;
        this.location = location;
    }
}

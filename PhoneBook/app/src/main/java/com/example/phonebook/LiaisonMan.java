package com.example.phonebook;

public class LiaisonMan {
    public String number;
    public String name;
    public String group;
    public String location;
    public String file;
    public LiaisonMan(){
        number = "";
        name = "";
        group= "";
        location = "";
        file = "";
    }
    public LiaisonMan(String number, String name, String group, String location,String file)
    {
        this.number = number;
        this.name = name;
        this.group = group;
        this.location = location;
        this.file = file;
    }
}

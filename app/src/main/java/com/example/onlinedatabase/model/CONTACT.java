package com.example.onlinedatabase.model;

public class CONTACT {
    private int id;
    private String name;
    private String contact;
    private byte[] byteArrayBlob;

    public CONTACT( String name, String contact) {
        this.name = name;
        this.contact = contact;
    }

    public CONTACT(int id, String name, String contact,byte[] byteArrayBlob) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.byteArrayBlob=byteArrayBlob;
    }

    public byte[] getByteArrayBlob() {
        return byteArrayBlob;
    }

    public void setByteArrayBlob(byte[] byteArrayBlob) {
        this.byteArrayBlob = byteArrayBlob;
    }

    public CONTACT(){
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}

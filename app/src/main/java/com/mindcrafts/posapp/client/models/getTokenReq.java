package com.mindcrafts.posapp.client.models;



public class getTokenReq {
    private String TID;
    private String MID;

    // Empty constructor
    public getTokenReq() {
    }

    // Parameterized constructor
    public getTokenReq(String TID, String MID) {
        this.TID = TID;
        this.MID = MID;
    }

    // Getter and Setter for TID
    public String getTID() {
        return TID;
    }

    public void setTID(String TID) {
        this.TID = TID;
    }

    // Getter and Setter for MID
    public String getMID() {
        return MID;
    }

    public void setMID(String MID) {
        this.MID = MID;
    }
}


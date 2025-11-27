package com.mindcrafts.posapp.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.mindcrafts.posapp.iso8583.builder.Batch;

@Entity(tableName = "transactions")
public class Transaction {

    @PrimaryKey(autoGenerate = true)
    public int id;
    public String message320;
    public String MID;
    public String TID;

    public String batchNumber;

    public String authCode;
    public String responseCode;
    public String Amount;
    public String TransactionTime;
    public String TransactionDate;
    public String CardScheme;
    public String AID;
    public String PAN;
    public String TC;
    public String time;
    public String date;
    public String expDate;
    public String entrymode;
    public String RRN;

    public String s_status;
    public String v_status;

    public String cryptogaram;

    public int isSynced;

    public Transaction(String message320,
                       String MID,
                       String TID,
                       String batchNumber,
                       String authCode,
                       String responseCode,
                       String Amount,
                       String TransactionTime,
                       String TransactionDate,
                       String CardScheme,
                       String AID,
                       String PAN,
                       String TC,
                       String time,
                       String date,
                       String expDate,
                       String entrymode,
                       String RRN,
                       String cryptogaram,
                       String s_status,
                       String v_status,
                       int isSynced
                       ){
              this.message320 = message320;
              this.MID = MID;
              this.TID = TID;
              this.batchNumber = batchNumber;
              this.authCode = authCode;
              this.responseCode = responseCode;
              this.Amount = Amount;
              this.TransactionTime = TransactionTime;
              this.TransactionDate = TransactionDate;
              this.CardScheme = CardScheme;
              this.AID =AID;
              this.PAN =PAN;
              this.TC = TC;
              this.time = time;
              this.date = date;
              this.expDate = expDate;
              this.entrymode = entrymode;
              this.RRN = RRN;
              this.cryptogaram = cryptogaram;
              this.s_status = s_status;
              this.v_status = v_status;
              this.isSynced = isSynced;
    }

}



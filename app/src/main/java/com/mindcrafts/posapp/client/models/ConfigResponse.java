package com.mindcrafts.posapp.client.models;

import java.util.List;
import java.util.List;

public class ConfigResponse {

    private boolean isSuccess;
    private String message;
    private String serial_number;
    private String TID;
    private List<String> supported_aids;
    private List<Card> cards;
    private MerchantDetails merchantDetails;

    // Getters and Setters
    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSerial_number() {
        return serial_number;
    }

    public void setSerial_number(String serial_number) {
        this.serial_number = serial_number;
    }

    public String getTID() {
        return TID;
    }

    public void setTID(String TID) {
        this.TID = TID;
    }

    public List<String> getSupported_aids() {
        return supported_aids;
    }

    public void setSupported_aids(List<String> supported_aids) {
        this.supported_aids = supported_aids;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public MerchantDetails getMerchantDetails() {
        return merchantDetails;
    }

    public void setMerchantDetails(MerchantDetails merchantDetails) {
        this.merchantDetails = merchantDetails;
    }

    // Nested classes
    public static class Card {
        // Define card fields when available
    }

    public static class MerchantDetails {
        private String MID;
        private String MerchantName;
        private String BusinessName;
        private String PhoneNumber;
        private String Email;
        private String Address;

        // Getters and Setters
        public String getMID() {
            return MID;
        }

        public void setMID(String MID) {
            this.MID = MID;
        }

        public String getMerchantName() {
            return MerchantName;
        }

        public void setMerchantName(String merchantName) {
            MerchantName = merchantName;
        }

        public String getBusinessName() {
            return BusinessName;
        }

        public void setBusinessName(String businessName) {
            BusinessName = businessName;
        }

        public String getPhoneNumber() {
            return PhoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            PhoneNumber = phoneNumber;
        }

        public String getEmail() {
            return Email;
        }

        public void setEmail(String email) {
            Email = email;
        }

        public String getAddress() {
            return Address;
        }

        public void setAddress(String address) {
            Address = address;
        }
    }
}

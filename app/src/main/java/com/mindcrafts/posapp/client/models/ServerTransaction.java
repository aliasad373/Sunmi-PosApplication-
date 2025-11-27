package com.mindcrafts.posapp.client.models;
public class ServerTransaction {

    private String merchantId;
    private String terminalId;
    private String batchNo;
    private String amount;
    private String cardNumber;
    private String transactionTime;
    private String transactionDate;
    private String expiryDate;
    private String rrn;
    private String responseCode;
    private String stan;
    private String authNumber;
    private String cardScheme;
    private String transactionType;
    private String aid;
    private boolean settlementStatus;

    // Empty constructor
    public ServerTransaction() {
    }

    // All-args constructor
    public ServerTransaction(String merchantId, String terminalId, String batchNo, String amount, String cardNumber,
                       String transactionTime, String transactionDate, String expiryDate, String rrn,
                       String responseCode, String stan, String authNumber, String cardScheme,
                       String transactionType, String aid, boolean settlementStatus) {
        this.merchantId = merchantId;
        this.terminalId = terminalId;
        this.batchNo = batchNo;
        this.amount = amount;
        this.cardNumber = cardNumber;
        this.transactionTime = transactionTime;
        this.transactionDate = transactionDate;
        this.expiryDate = expiryDate;
        this.rrn = rrn;
        this.responseCode = responseCode;
        this.stan = stan;
        this.authNumber = authNumber;
        this.cardScheme = cardScheme;
        this.transactionType = transactionType;
        this.aid = aid;
        this.settlementStatus = settlementStatus;
    }

    // Getters and Setters
    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(String transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getRrn() {
        return rrn;
    }

    public void setRrn(String rrn) {
        this.rrn = rrn;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getStan() {
        return stan;
    }

    public void setStan(String stan) {
        this.stan = stan;
    }

    public String getAuthNumber() {
        return authNumber;
    }

    public void setAuthNumber(String authNumber) {
        this.authNumber = authNumber;
    }

    public String getCardScheme() {
        return cardScheme;
    }

    public void setCardScheme(String cardScheme) {
        this.cardScheme = cardScheme;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public boolean isSettlementStatus() {
        return settlementStatus;
    }

    public void setSettlementStatus(boolean settlementStatus) {
        this.settlementStatus = settlementStatus;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "merchantId='" + merchantId + '\'' +
                ", terminalId='" + terminalId + '\'' +
                ", batchNo='" + batchNo + '\'' +
                ", amount='" + amount + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", transactionTime='" + transactionTime + '\'' +
                ", transactionDate='" + transactionDate + '\'' +
                ", expiryDate='" + expiryDate + '\'' +
                ", rrn='" + rrn + '\'' +
                ", responseCode='" + responseCode + '\'' +
                ", stan='" + stan + '\'' +
                ", authNumber='" + authNumber + '\'' +
                ", cardScheme='" + cardScheme + '\'' +
                ", transactionType='" + transactionType + '\'' +
                ", aid='" + aid + '\'' +
                ", settlementStatus=" + settlementStatus +
                '}';
    }
}


package com.mindcrafts.posapp.activitiese.inventory.models;

public class InventoryPurchaseReq {

    private String offline_id;
    private String inventory_id;
    private double amount;
    private double subamount;
    private int stockIN;
    private int stockOUT;
    private double unitPrice;
    private int deleted;
    private String created_at;
    private String pst;
    private String pst_sync;
    private String deviceId;
    private String product_name;
    private String category_name;
    private String MID;
    private String TID;
    private String rrn;
    private String stan;
    private String notes; // JSON string: e.g. {"discount":5,"payment_mode":"cash"}
    private String transactionType;

    // --- Constructors ---
    public InventoryPurchaseReq() {
    }

    public InventoryPurchaseReq(String offline_id, String inventory_id, double amount, double subamount,
                                int stockIN, int stockOUT, double unitPrice, int deleted, String created_at,
                                String pst, String pst_sync, String deviceId, String product_name,
                                String category_name, String MID, String TID, String rrn, String stan,
                                String notes, String transactionType) {
        this.offline_id = offline_id;
        this.inventory_id = inventory_id;
        this.amount = amount;
        this.subamount = subamount;
        this.stockIN = stockIN;
        this.stockOUT = stockOUT;
        this.unitPrice = unitPrice;
        this.deleted = deleted;
        this.created_at = created_at;
        this.pst = pst;
        this.pst_sync = pst_sync;
        this.deviceId = deviceId;
        this.product_name = product_name;
        this.category_name = category_name;
        this.MID = MID;
        this.TID = TID;
        this.rrn = rrn;
        this.stan = stan;
        this.notes = notes;
        this.transactionType = transactionType;
    }

    // --- Getters and Setters ---
    public String getOffline_id() {
        return offline_id;
    }

    public void setOffline_id(String offline_id) {
        this.offline_id = offline_id;
    }

    public String getInventory_id() {
        return inventory_id;
    }

    public void setInventory_id(String inventory_id) {
        this.inventory_id = inventory_id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getSubamount() {
        return subamount;
    }

    public void setSubamount(double subamount) {
        this.subamount = subamount;
    }

    public int getStockIN() {
        return stockIN;
    }

    public void setStockIN(int stockIN) {
        this.stockIN = stockIN;
    }

    public int getStockOUT() {
        return stockOUT;
    }

    public void setStockOUT(int stockOUT) {
        this.stockOUT = stockOUT;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getPst() {
        return pst;
    }

    public void setPst(String pst) {
        this.pst = pst;
    }

    public String getPst_sync() {
        return pst_sync;
    }

    public void setPst_sync(String pst_sync) {
        this.pst_sync = pst_sync;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getMID() {
        return MID;
    }

    public void setMID(String MID) {
        this.MID = MID;
    }

    public String getTID() {
        return TID;
    }

    public void setTID(String TID) {
        this.TID = TID;
    }

    public String getRrn() {
        return rrn;
    }

    public void setRrn(String rrn) {
        this.rrn = rrn;
    }

    public String getStan() {
        return stan;
    }

    public void setStan(String stan) {
        this.stan = stan;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }
}


package com.mindcrafts.posapp.activitiese.inventory.models;


import java.util.List;

public class PurchaseReq {
    private List<InventoryPurchaseReq> data;

    public PurchaseReq(List<InventoryPurchaseReq> items) {
        this.data = items;
    }

    public List<InventoryPurchaseReq> getItems() { return data; }
    public void setItems(List<InventoryPurchaseReq> items) { this.data = items; }
}

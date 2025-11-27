package com.mindcrafts.posapp.activitiese.inventory.models;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class LiveStockResponse {

    @SerializedName("code")
    private int code;

    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private Data data;

    @SerializedName("message")
    private String message;

    // Getters and Setters
    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Data getData() { return data; }
    public void setData(Data data) { this.data = data; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    // ---------------------- Inner Class: Data ----------------------
    public static class Data {

        @SerializedName("products")
        private Products products;

        @SerializedName("categories")
        private List<String> categories;

        // Getters and Setters
        public Products getProducts() { return products; }
        public void setProducts(Products products) { this.products = products; }

        public List<String> getCategories() { return categories; }
        public void setCategories(List<String> categories) { this.categories = categories; }
    }

    // ---------------------- Inner Class: Products ----------------------
    public static class Products {

        @SerializedName("LiveStock")
        private List<LiveStockItem> liveStock;

        // Getters and Setters
        public List<LiveStockItem> getLiveStock() { return liveStock; }
        public void setLiveStock(List<LiveStockItem> liveStock) { this.liveStock = liveStock; }
    }

    // ---------------------- Inner Class: LiveStockItem ----------------------
    public static class LiveStockItem {

        @SerializedName("id")
        private int id;

        @SerializedName("deleted")
        private int deleted;

        @SerializedName("type")
        private String type;

        @SerializedName("category")
        private String category;

        @SerializedName("lowerCap")
        private double lowerCap;

        @SerializedName("maxPrice")
        private double maxPrice;

        @SerializedName("minPrice")
        private double minPrice;

        @SerializedName("name")
        private String name;

        @SerializedName("sku")
        private String sku;

        @SerializedName("unit")
        private String unit;

        @SerializedName("sPath")
        private String sPath;

        @SerializedName("created_at")
        private String createdAt;

        @SerializedName("updated_at")
        private String updatedAt;

        // Getters and Setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public int getDeleted() { return deleted; }
        public void setDeleted(int deleted) { this.deleted = deleted; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public double getLowerCap() { return lowerCap; }
        public void setLowerCap(double lowerCap) { this.lowerCap = lowerCap; }

        public double getMaxPrice() { return maxPrice; }
        public void setMaxPrice(double maxPrice) { this.maxPrice = maxPrice; }

        public double getMinPrice() { return minPrice; }
        public void setMinPrice(double minPrice) { this.minPrice = minPrice; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getSku() { return sku; }
        public void setSku(String sku) { this.sku = sku; }

        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }

        public String getsPath() { return sPath; }
        public void setsPath(String sPath) { this.sPath = sPath; }

        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

        public String getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    }
}


package com.mindcrafts.posapp.activitiese.inventory.models;

import java.util.List;

public class InventoryResponse {

    private int code;
    private String status;
    private ProductData data;

    public int getCode() { return code; }
    public String getStatus() { return status; }
    public ProductData getData() { return data; }

    public static class ProductData {
        private Products products;
        public Products getProducts() { return products; }
    }

    public static class Products {
        private List<Product> Fertilizer;
        private List<Product> Pesticides;

        private List<Product> Seed;
        public List<Product> getFertilizer() { return Fertilizer; }
        public List<Product> getPesticides() { return Pesticides; }
        public List<Product> getSeed() { return Seed; }
    }

    public static class Product {
        private int id;
        private int deleted;
        private String type;
        private String category;
        private int lowerCap;
        private int maxPrice;
        private int minPrice;
        private String name;
        private String sku;
        private String unit;
        private String sPath;
        private String created_at;
        private String updated_at;

        public int getId() { return id; }
        public int getDeleted() { return deleted; }
        public String getType() { return type; }
        public String getCategory() { return category; }
        public int getLowerCap() { return lowerCap; }
        public int getMaxPrice() { return maxPrice; }
        public int getMinPrice() { return minPrice; }
        public String getName() { return name; }
        public String getSku() { return sku; }
        public String getUnit() { return unit; }
        public String getsPath() { return sPath; }
        public String getCreated_at() { return created_at; }
        public String getUpdated_at() { return updated_at; }
    }
}


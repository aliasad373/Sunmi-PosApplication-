package com.mindcrafts.posapp.database.entity;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "inventory")
public class InventoryEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int type;

    private String jsonString;

    // Constructor
    public InventoryEntity(int type, String jsonString) {
        this.type = type;
        this.jsonString = jsonString;
    }

    // Getter and Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getJsonString() {
        return jsonString;
    }

    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }
}


package com.mindcrafts.posapp.database;

import android.content.Context;

import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.mindcrafts.posapp.activitiese.inventory.InventoryHome;
import com.mindcrafts.posapp.database.entity.InventoryEntity;
import com.mindcrafts.posapp.database.entity.Transaction;


@Database(entities = {Transaction.class, InventoryEntity.class}, version = 3)
public abstract class DBInstance extends RoomDatabase {

    private static volatile DBInstance INSTANCE;

    public abstract DAO userDao();

    public static DBInstance getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (DBInstance.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    DBInstance.class, "dgk_db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
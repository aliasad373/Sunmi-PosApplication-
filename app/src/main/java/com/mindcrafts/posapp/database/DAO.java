package com.mindcrafts.posapp.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.mindcrafts.posapp.database.entity.InventoryEntity;
import com.mindcrafts.posapp.database.entity.Transaction;

import java.util.List;


@Dao
public interface DAO {

    @Insert
    Long insert(Transaction tran);

    @Query("SELECT * FROM transactions")
    List<Transaction> getAllTransactions();


    @Query("SELECT * FROM `Transactions` WHERE id = :id")
    Transaction getTransactionById(int id);

    @Query("UPDATE transactions SET v_status = :s_void WHERE id = :id")
    void updateVoidStatus(int id, String s_void);


    @Query("SELECT * FROM transactions WHERE v_status = '0' AND s_status = '0'")
    List<Transaction> getUnsettledTransactions();


    @Query("UPDATE transactions SET s_status = '1' WHERE v_status = '0'")
    void updateSStatusWhere();


    //@Query("SELECT * FROM YourTableName WHERE v_status = 0 AND s_status =
    @Query("SELECT * FROM transactions WHERE batchNumber = :batchNumber")
    List<Transaction> getTransactionsByBatchNumber(String batchNumber);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertInventory(InventoryEntity inventory);

    @Query("SELECT COUNT(*) FROM inventory WHERE type = :type")
    int countByType(int type);

    @Query("SELECT * FROM inventory WHERE type = :type LIMIT 1")
    InventoryEntity getByType(int type);

    @Query("UPDATE inventory SET jsonString = :jsonString WHERE type = :type")
    void updateByType(int type, String jsonString);

    @Query("UPDATE transactions SET isSynced = 1 WHERE id = :id")
    void updateIsSyncedById(int id);

    @Query("SELECT * FROM transactions WHERE batchNumber = :batchNumber AND isSynced = 0")
    List<Transaction> getUnsyncedTransactionsByBatchNumber(String batchNumber);
}

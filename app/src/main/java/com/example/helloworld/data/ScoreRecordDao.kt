package com.example.helloworld.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ScoreRecordDao {

    @Query("SELECT * FROM score_records ORDER BY score DESC LIMIT 50")
    fun getAllRecords(): Flow<List<ScoreRecord>>

    @Query("SELECT * FROM score_records WHERE playerName = :playerName ORDER BY score DESC")
    fun getRecordsByPlayer(playerName: String): Flow<List<ScoreRecord>>

    @Insert
    suspend fun insertRecord(record: ScoreRecord)

    @Query("DELETE FROM score_records WHERE id = :id")
    suspend fun deleteRecord(id: Long)

    @Query("SELECT * FROM score_records ORDER BY score DESC LIMIT 10")
    fun getTop10Records(): Flow<List<ScoreRecord>>

    @Query("SELECT COUNT(*) FROM score_records WHERE playerName = :playerName")
    suspend fun getPlayerRecordCount(playerName: String): Int
}
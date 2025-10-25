package com.example.helloworld.repository

import com.example.helloworld.data.ScoreRecord
import com.example.helloworld.data.ScoreRecordDao
import kotlinx.coroutines.flow.Flow

class ScoreRepository(private val scoreRecordDao: ScoreRecordDao) {

    val allRecords: Flow<List<ScoreRecord>> = scoreRecordDao.getAllRecords()
    val top10Records: Flow<List<ScoreRecord>> = scoreRecordDao.getTop10Records()

    suspend fun insertRecord(record: ScoreRecord) {
        scoreRecordDao.insertRecord(record)
    }

    fun getRecordsByPlayer(playerName: String): Flow<List<ScoreRecord>> {
        return scoreRecordDao.getRecordsByPlayer(playerName)
    }

    suspend fun getPlayerRecordCount(playerName: String): Int {
        return scoreRecordDao.getPlayerRecordCount(playerName)
    }
}
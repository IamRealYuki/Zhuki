package com.example.helloworld.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.helloworld.data.ScoreRecord
import com.example.helloworld.repository.ScoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ScoreViewModel(private val repository: ScoreRepository) : ViewModel() {

    val allRecords: Flow<List<ScoreRecord>> = repository.allRecords
    val top10Records: Flow<List<ScoreRecord>> = repository.top10Records

    fun insertRecord(record: ScoreRecord) = viewModelScope.launch {
        repository.insertRecord(record)
    }

    fun getRecordsByPlayer(playerName: String): Flow<List<ScoreRecord>> {
        return repository.getRecordsByPlayer(playerName)
    }

    suspend fun getPlayerRecordCount(playerName: String): Int {
        return repository.getPlayerRecordCount(playerName)
    }
}
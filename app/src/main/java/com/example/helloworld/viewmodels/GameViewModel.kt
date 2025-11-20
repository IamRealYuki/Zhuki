package com.example.helloworld.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GameViewModel : ViewModel() {

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState

    fun updateScore(newScore: Int) {
        _gameState.value = _gameState.value.copy(score = newScore)
    }

    fun updateMiss(newMiss: Int) {
        _gameState.value = _gameState.value.copy(miss = newMiss)
    }

    fun updateGameState(
        score: Int = _gameState.value.score,
        miss: Int = _gameState.value.miss,
        isPlaying: Boolean = _gameState.value.isPlaying
    ) {
        _gameState.value = _gameState.value.copy(
            score = score,
            miss = miss,
            isPlaying = isPlaying
        )
    }

    fun resetGame() {
        _gameState.value = GameState()
    }
}

data class GameState(
    val score: Int = 0,
    val miss: Int = 0,
    val isPlaying: Boolean = false
)
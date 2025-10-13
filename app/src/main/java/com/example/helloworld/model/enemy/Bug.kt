package com.example.helloworld.model.enemy

abstract class Bug {
    abstract var sprite: String
    abstract var points: Int
    abstract var speed: Int
    abstract var size: Int
    abstract fun calculateNewPosition(
        currentX: Int,
        currentY: Int,
        screenWidth: Int,
        screenHeight: Int,
        viewWidth: Int,
        viewHeight: Int,
        speedMultiplier: Double
    ): Pair<Int, Int>
}

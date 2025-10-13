package com.example.helloworld.model.enemy

import kotlin.math.sin
import kotlin.random.Random

class Solodov(
    spritePath: String = "Solodov.png",
    pointValue: Int = 2280,
    moveSpeed: Int = 1,
    bugSize: Int = 4
) : Bug() {

    override var sprite: String = spritePath
    override var points: Int = pointValue
    override var speed: Int = moveSpeed
    override var size: Int = bugSize
    private var waveOffset = Random.nextFloat() * 10f

    override fun calculateNewPosition(
        currentX: Int,
        currentY: Int,
        screenWidth: Int,
        screenHeight: Int,
        viewWidth: Int,
        viewHeight: Int,
        speedMultiplier: Double
    ): Pair<Int, Int> {

        waveOffset += 0.1f
        val newX = currentX + (sin(waveOffset) * 3).toInt()
        val newY = currentY + (speed * speedMultiplier).toInt()

        return Pair(newX.coerceIn(0, screenWidth - viewWidth), newY)
    }
}

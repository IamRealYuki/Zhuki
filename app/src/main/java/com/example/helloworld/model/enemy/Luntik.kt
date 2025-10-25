package com.example.helloworld.model.enemy

import kotlin.random.Random

class Luntik (
    spritePath: String = "luntik.png",
    pointValue: Int = 3000,
    moveSpeed: Int = 2,
    bugSize: Int = 5
) : Bug() {

    override var sprite: String = spritePath
    override var points: Int = pointValue
    override var speed: Int = moveSpeed
    override var size: Int = bugSize
    private var direction = if (Random.nextBoolean()) 1 else -1

    override fun calculateNewPosition(
        currentX: Int,
        currentY: Int,
        screenWidth: Int,
        screenHeight: Int,
        viewWidth: Int,
        viewHeight: Int,
        speedMultiplier: Double
    ): Pair<Int, Int> {
        var newX = currentX + direction * (speed * speedMultiplier).toInt()
        val newY = currentY + speed

        if (newX <= 0 || newX >= screenWidth - viewWidth) {
            direction = -direction
            newX = newX.coerceIn(0, screenWidth - viewWidth)
        }

        return Pair(newX, newY)
    }
}
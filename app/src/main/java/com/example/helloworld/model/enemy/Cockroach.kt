package com.example.helloworld.model.enemy

class Cockroach (
    spritePath: String = "cockroach.png",
    pointValue: Int = 2900,
    moveSpeed: Int = 3,
    bugSize: Int = 3
) : Bug() {

    override var sprite: String = spritePath
    override var points: Int = pointValue
    override var speed: Int = moveSpeed
    override var size: Int = bugSize

    override fun calculateNewPosition(
        currentX: Int,
        currentY: Int,
        screenWidth: Int,
        screenHeight: Int,
        viewWidth: Int,
        viewHeight: Int,
        speedMultiplier: Double
    ): Pair<Int, Int> {
        val newY = currentY + (speed * speedMultiplier).toInt()
        return Pair(currentX, newY)
    }
}
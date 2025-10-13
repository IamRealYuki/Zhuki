package com.example.helloworld.model.enemy

import kotlin.random.Random

class Grasshopper (
    spritePath: String = "grasshopper.png",
    pointValue: Int = 4500,
    moveSpeed: Int = 5,
    bugSize: Int = 2
) : Bug() {

    override var sprite: String = spritePath
    override var points: Int = pointValue
    override var speed: Int = moveSpeed
    override var size: Int = bugSize
    private var jumpCounter = 0
    override fun calculateNewPosition(
        currentX: Int,
        currentY: Int,
        screenWidth: Int,
        screenHeight: Int,
        viewWidth: Int,
        viewHeight: Int,
        speedMultiplier: Double
    ): Pair<Int, Int> {
        jumpCounter++
        var newX = currentX
        var newY = currentY + (speed * speedMultiplier).toInt()

        if (jumpCounter >= 80) {
            newX += Random.nextInt(-300, 300)
            jumpCounter = 0
        }

        return Pair(newX.coerceIn(0, screenWidth - viewWidth), newY)
    }

    init {
        println("Создан жук по имени Солодов")
    }
}
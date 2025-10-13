package com.example.helloworld.model.enemy

class Solodov(
    spritePath: String = "sprites/bugs/solodov.png",
    pointValue: Int = 2280,
    moveSpeed: Int = 1,
    bugSize: Int = 4
) : Bug() {

    override var sprite: String = spritePath
    override var points: Int = pointValue
    override var speed: Int = moveSpeed
    override var size: Int = bugSize

    init {
        println("Создан жук по имени Солодов")
    }
}
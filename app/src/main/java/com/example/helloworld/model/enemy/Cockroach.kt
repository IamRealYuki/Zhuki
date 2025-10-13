package com.example.helloworld.model.enemy

class Cockroach (
    spritePath: String = "sprites/bugs/cockroach.png",
    pointValue: Int = 2900,
    moveSpeed: Int = 3,
    bugSize: Int = 3
) : Bug() {

    override var sprite: String = spritePath
    override var points: Int = pointValue
    override var speed: Int = moveSpeed
    override var size: Int = bugSize

    init {
        println("Создан жук по имени Солодов")
    }
}
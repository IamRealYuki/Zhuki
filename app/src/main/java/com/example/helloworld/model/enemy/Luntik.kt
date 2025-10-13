package com.example.helloworld.model.enemy

class Luntik (
    spritePath: String = "sprites/bugs/luntik.png",
    pointValue: Int = 3000,
    moveSpeed: Int = 2,
    bugSize: Int = 5
) : Bug() {

    override var sprite: String = spritePath
    override var points: Int = pointValue
    override var speed: Int = moveSpeed
    override var size: Int = bugSize

    init {
        println("Создан жук по имени Солодов")
    }
}
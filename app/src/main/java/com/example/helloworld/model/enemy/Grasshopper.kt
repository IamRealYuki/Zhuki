package com.example.helloworld.model.enemy

class Grasshopper (
    spritePath: String = "sprites/bugs/grasshopper.png",
    pointValue: Int = 4500,
    moveSpeed: Int = 5,
    bugSize: Int = 2
) : Bug() {

    override var sprite: String = spritePath
    override var points: Int = pointValue
    override var speed: Int = moveSpeed
    override var size: Int = bugSize

    init {
        println("Создан жук по имени Солодов")
    }
}
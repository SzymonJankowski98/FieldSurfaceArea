package com.mobile.areacounter.geometry

data class Vector(val startingPoint: MatPoint, val finishingPoint: MatPoint) {

    fun add(vector: Vector) : Vector {
        val moveX = vector.finishingPoint.x - vector.startingPoint.x
        val moveY = vector.finishingPoint.y - vector.startingPoint.y
        val movePoint = MatPoint(moveX, moveY)

        return Vector(finishingPoint, finishingPoint.add(movePoint))
    }

    fun isRising(): Boolean {
        return finishingPoint.y - startingPoint.y > 0
    }

    fun isLeftToRight(): Boolean {
        return finishingPoint.x - startingPoint.x > 0
    }

    fun isVertical(): Boolean {
        return finishingPoint.x - startingPoint.x == 0.0
    }

    fun getLength(): Double {
        return Math.sqrt(Math.pow(finishingPoint.x - startingPoint.x, 2.0) + Math.pow(finishingPoint.y - startingPoint.x, 2.0))
    }
}

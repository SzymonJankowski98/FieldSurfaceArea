package com.mobile.areacounter.geometry

data class MatPoint(val x : Double, val y : Double) {
    fun add(vector: Vector) : MatPoint {
        return MatPoint(x + vector.finishingPoint.x - vector.startingPoint.x, y + vector.finishingPoint.y - vector.startingPoint.y)
    }

    fun add(point: MatPoint) : MatPoint {
        return MatPoint(x + point.x, y + point.y)
    }
}

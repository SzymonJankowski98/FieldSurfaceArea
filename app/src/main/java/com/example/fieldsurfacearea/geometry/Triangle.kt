package com.mobile.areacounter.geometry

class Triangle(val pointA : MatPoint, val pointB : MatPoint, val pointC : MatPoint) {
    fun belongsToTriangle(point: MatPoint) : Boolean{
        val detv1 = calcVector(point, pointA, pointB)
        val detv2 = calcVector(point, pointB, pointC)
        val detv3 = calcVector(point, pointC, pointA)

        val hasNeg = detv1 < 0 || detv2 < 0 || detv3 < 0
        val hasPos = detv1 > 0 || detv2 > 0 || detv3 > 0

        return !(hasNeg && hasPos)
    }

    private fun calcVector(p1 : MatPoint, p2 : MatPoint, p3 : MatPoint) : Double{
        return (p1.x - p3.x) * (p2.y - p3.y) - (p2.x - p3.x) * (p1.y - p3.y)
    }
}
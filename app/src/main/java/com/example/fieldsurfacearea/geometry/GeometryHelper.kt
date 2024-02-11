package com.mobile.areacounter.geometry

import com.example.fieldsurfacearea.geometry.Line
import kotlin.math.acos
import kotlin.math.pow
import kotlin.math.sqrt

object GeometryHelper {
    fun calculateAngle(firstVector: Vector, secondVector: Vector) : Double {
        val firstVectorCommonStart = MatPoint(
            firstVector.finishingPoint.x - firstVector.startingPoint.x,
            firstVector.finishingPoint.y - firstVector.startingPoint.y)

        val secondVectorCommonStart = MatPoint(
            secondVector.finishingPoint.x - secondVector.startingPoint.x,
            secondVector.finishingPoint.y - secondVector.startingPoint.x)

        val vectorsDotProduct = firstVectorCommonStart.x * secondVectorCommonStart.x + firstVectorCommonStart.y * secondVectorCommonStart.y

        val absA = calcAbs(firstVectorCommonStart)
        val absB = calcAbs(secondVectorCommonStart)

        return acos(vectorsDotProduct / (absA * absB))
    }

    private fun calcAbs(a : MatPoint) : Double{
        return sqrt(a.x.pow(2.0) + a.y.pow(2.0))
    }

    fun calculateInnerAndOuterAngle(v1 : Vector, v2 : Vector) : Pair<Double, Double> {

        val alpha = Math.toDegrees(calculateAngle(v1, v2))

        if (bendsToLeft(v1, v2)){
            return Pair(180.0 - alpha, 180.0 + alpha)
        }

        return Pair(180.0 + alpha, 180.0 - alpha)

    }

    private fun bendsToLeft(v1: Vector, v2: Vector): Boolean  {
        return (v1.isVertical() && v1.isRising() && v2.finishingPoint.x < v1.startingPoint.x) ||
                (v1.isVertical() && !v1.isRising() && v2.finishingPoint.x > v1.startingPoint.x) ||
                (!v1.isVertical() && v1.isLeftToRight() && !Line.fromVector(v1).isGreaterThan(v2.finishingPoint)) ||
                (!v1.isVertical() && !v1.isLeftToRight() && Line.fromVector(v1).isGreaterThan(v2.finishingPoint))
    }
}
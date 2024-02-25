package com.mobile.areacounter.geometry

import com.example.fieldsurfacearea.Point
import com.example.fieldsurfacearea.geometry.Line
import kotlin.math.acos
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

object GeometryHelper {
    val random = Random

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

    fun toMatPoint(startingPoint: Point, finishingPoint: Point): Vector {
        //val earthRadius = 6371
        val MEDIAN : Double = 20_000.0
        val EQUATOR : Double =  40_075.0


        val latRadDif = Math.toRadians(Math.abs(startingPoint.latitude - finishingPoint.latitude))
        val lonRadDif = Math.toRadians(Math.abs(startingPoint.longitude - finishingPoint.longitude))

        val avgLatitudeRad = Math.toRadians((startingPoint.latitude + finishingPoint.latitude) / 2)

        val longitudeDirection: Double = if (finishingPoint.longitude - startingPoint.longitude > 0){
            1.0
        }else {
            -1.0
        }

        val latitudeDirection: Double = if (finishingPoint.latitude - startingPoint.latitude > 0){
            1.0
        }else{
            -1.0
        }

        val longitudeInKm = (EQUATOR / 2 * lonRadDif / Math.PI * Math.cos(avgLatitudeRad)) * longitudeDirection
        val latitudeInKm = (MEDIAN * latRadDif / Math.PI) * latitudeDirection

        return Vector(
            MatPoint(0.0,0.0),
            MatPoint(longitudeInKm, latitudeInKm))
    }

    fun calculateMonteCarloArea(polygon: Polygon, probes : Int = 1000): Double {
        val extremePoints = polygon.getExtremes()

        var pointsInside = 0

        val setOfTriangles = polygon.triangulate()

        for (index in 0..probes){
            if (setOfTriangles.filter {
                    it.belongsToTriangle(
                        MatPoint(
                            random.nextDouble(extremePoints.xMin, extremePoints.xMax),
                            random.nextDouble(extremePoints.yMin, extremePoints.yMax)
                        )
                    )
                }.toSet().isNotEmpty()){
                pointsInside += 1
            }
        }
        val squareX = Math.abs(extremePoints.xMax - extremePoints.xMin)
        val squareY = Math.abs(extremePoints.yMax - extremePoints.yMin)
        val squareArea = squareX * squareY

        return squareArea * pointsInside / probes
    }
}
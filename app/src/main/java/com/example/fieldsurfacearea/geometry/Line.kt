package com.example.fieldsurfacearea.geometry

import com.mobile.areacounter.geometry.MatPoint
import com.mobile.areacounter.geometry.Vector

class Line(private val a : Double, private val b : Double, private val isVertical : Boolean = false) {

    companion object {
        fun fromVector(vector : Vector) : Line{
            val valueChange = vector.finishingPoint.y - vector.startingPoint.y
            val positionChange = vector.finishingPoint.x - vector.startingPoint.x

            if (positionChange == 0.0){
                return Line(0.0, vector.startingPoint.x, true)
            }

            val a = if (valueChange != 0.0){
                positionChange / valueChange
            }else {
                0.0
            }

            val b = vector.startingPoint.y - vector.startingPoint.x * a

            return Line(a, b)
        }
    }
    fun getValue(x : Double) : Double {
        return x * a + b
    }

    fun isGreaterThan(p : MatPoint) : Boolean {
        return if(!isVertical){
            p.x * a + b > p.y
        }else{
            b > p.x
        }
    }
}
package com.mobile.areacounter

import com.mobile.areacounter.geometry.MatPoint
import com.mobile.areacounter.geometry.Triangle
import org.assertj.core.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class TriangleTest {

    companion object {
        @JvmStatic
        fun correctPointSource(): Stream<Arguments?>? {
            return Stream.of(
                Arguments.of(MatPoint(1.0, 1.0)),
                Arguments.of(MatPoint(2.0, 2.0)),
                Arguments.of(MatPoint(0.0, 5.0)))
        }

        @JvmStatic
        fun inCorrectPointSource(): Stream<Arguments?>? {
            return Stream.of(
                Arguments.of(MatPoint(6.0, 0.0)),
                Arguments.of(MatPoint(0.0, 6.0)),
                Arguments.of(MatPoint(5.0, 5.0)))
        }
    }
    @ParameterizedTest
    @MethodSource("correctPointSource")
    fun shouldCorrectlyCalculateIfPointBelongsToTriangle(point : MatPoint) {
        val triangle = Triangle(MatPoint(0.0, 0.0), MatPoint(0.0, 5.0), MatPoint(5.0, 0.0))
        val res = triangle.belongsToTriangle(point)
        Assertions.assertThat(res).isTrue()
    }

    @ParameterizedTest
    @MethodSource("inCorrectPointSource")
    fun shouldCorrectlyCalculateIfPointDoesNotBelongToTriangle(point : MatPoint) {
        val triangle = Triangle(MatPoint(0.0, 0.0), MatPoint(0.0, 5.0), MatPoint(5.0, 0.0))
        val res = triangle.belongsToTriangle(point)
        Assertions.assertThat(res).isFalse()
    }
}
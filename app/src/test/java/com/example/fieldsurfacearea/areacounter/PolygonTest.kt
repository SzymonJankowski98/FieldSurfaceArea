package com.mobile.areacounter

import com.example.fieldsurfacearea.geometry.PolygonMinMax
import com.mobile.areacounter.geometry.MatPoint
import com.mobile.areacounter.geometry.Polygon
import com.mobile.areacounter.geometry.Triangle
import com.mobile.areacounter.geometry.Vector
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class PolygonTest {

    companion object {
        @JvmStatic
        fun vectorSource(): Stream<Arguments?>? {
            return Stream.of(
                Arguments.of(
                    listOf(
                        Vector(MatPoint(2.0,2.0), MatPoint(5.0, 0.0)),
                        Vector(MatPoint(5.0, 0.0), MatPoint(4.0, -4.0)),
                        Vector(MatPoint(4.0, -4.0), MatPoint(-5.0, -3.0)),
                        Vector(MatPoint(-5.0, -3.0), MatPoint(-6.0, 1.0)),
                        Vector(MatPoint(-6.0,1.0), MatPoint(2.0,2.0))
                    ),
                    PolygonMinMax(-6.0, -4.0, 5.0, 2.0)
                ),
                Arguments.of(
                    listOf(
                        Vector(MatPoint(5.0,5.0), MatPoint(5.0, -5.0)),
                        Vector(MatPoint(5.0, -5.0), MatPoint(-5.0, -5.0)),
                        Vector(MatPoint(-5.0, -5.0), MatPoint(-5.0, 5.0)),
                        Vector(MatPoint(-5.0, 5.0), MatPoint(5.0, 5.0))
                    ),
                    PolygonMinMax(-5.0, -5.0, 5.0, 5.0)
                ),
                Arguments.of(
                    listOf(
                        Vector(MatPoint(0.0,6.0), MatPoint(5.0, -7.0)),
                        Vector(MatPoint(5.0, -7.0), MatPoint(-5.0, -8.0)),
                        Vector(MatPoint(-5.0, -8.0), MatPoint(0.0, 6.0))
                    ),
                    PolygonMinMax(-5.0, -8.0, 5.0, 6.0)
                )
            )
        }

        @JvmStatic
        fun triangulationVectorSource(): Stream<Arguments?>? {
            return Stream.of(
                Arguments.of(
                    listOf(
                        Vector(MatPoint(5.0, 5.0), MatPoint(5.0, -5.0)),
                        Vector(MatPoint(5.0, -5.0), MatPoint(-5.0, -5.0)),
                        Vector(MatPoint(-5.0, -5.0), MatPoint(-5.0, 5.0)),
                        Vector(MatPoint(-5.0, 5.0), MatPoint(5.0, 5.0))
                    ),
                    setOf(
                        Triangle(
                            MatPoint(5.0, 5.0),
                            MatPoint(-5.0, -5.0),
                            MatPoint(-5.0, 5.0)
                        ),
                        Triangle(
                            MatPoint(5.0, 5.0),
                            MatPoint(5.0, -5.0),
                            MatPoint(-5.0, -5.0)
                        )
                    )
                ),
                Arguments.of(
                    listOf(
                        Vector(MatPoint(2.0, 2.0), MatPoint(5.0, 0.0)),
                        Vector(MatPoint(5.0, 0.0), MatPoint(4.0, -4.0)),
                        Vector(MatPoint(4.0, -4.0), MatPoint(-5.0, -3.0)),
                        Vector(MatPoint(-5.0, -3.0), MatPoint(-6.0, 1.0)),
                        Vector(MatPoint(-6.0, 1.0), MatPoint(2.0, 2.0))
                    ),
                    setOf(
                        Triangle(
                            MatPoint(2.0, 2.0),
                            MatPoint(-6.0, 1.0),
                            MatPoint(-5.0, -3.0)
                        ),
                        Triangle(
                            MatPoint(2.0, 2.0),
                            MatPoint(-5.0, -3.0),
                            MatPoint(4.0, -4.0)
                        ),
                        Triangle(
                            MatPoint(2.0, 2.0),
                            MatPoint(5.0, 0.0),
                            MatPoint(4.0, -4.0)
                        )
                    )
                ),
                Arguments.of(
                    listOf(
                        Vector(MatPoint(6.0, 5.0), MatPoint(5.0, 5.0)),
                        Vector(MatPoint(5.0, 5.0), MatPoint(5.0, 1.0)),
                        Vector(MatPoint(5.0, 1.0), MatPoint(8.0, 1.0)),
                        Vector(MatPoint(8.0, 1.0), MatPoint(8.0, 5.0)),
                        Vector(MatPoint(8.0, 5.0), MatPoint(7.0, 5.0)),
                        Vector(MatPoint(7.0, 5.0), MatPoint(7.0, 2.0)),
                        Vector(MatPoint(7.0, 2.0), MatPoint(6.0, 2.0)),
                        Vector(MatPoint(6.0, 2.0), MatPoint(6.0, 5.0)),
                    ),
                    setOf(
                        Triangle(MatPoint(5.0, 1.0), MatPoint(5.0, 5.0), MatPoint(6.0, 5.0)),
                        Triangle(MatPoint(5.0, 1.0), MatPoint(6.0, 5.0), MatPoint(6.0, 2.0)),
                        Triangle(MatPoint(5.0, 1.0), MatPoint(6.0, 2.0), MatPoint(8.0, 1.0)),
                        Triangle(MatPoint(6.0, 2.0), MatPoint(7.0, 2.0), MatPoint(8.0, 1.0)),
                        Triangle(MatPoint(8.0, 1.0), MatPoint(7.0, 2.0), MatPoint(8.0, 5.0)),
                        Triangle(MatPoint(7.0, 2.0), MatPoint(7.0, 5.0), MatPoint(8.0, 5.0))
                    )
                )
            )
        }
    }



    @ParameterizedTest
    @MethodSource("triangulationVectorSource")
    fun testPolygon(vectorList: List<Vector>, expectedTriangleSet: Set<Triangle>){
        val polygon = Polygon(vectorList)

        val triangleSet = polygon.triangulate()

        Assertions.assertThat(triangleSet).isEqualTo(expectedTriangleSet)
    }

    @ParameterizedTest()
    @MethodSource("vectorSource")
    fun getExtremesTest(vectors : List<Vector>, expectedResult : PolygonMinMax){
        val polygon = Polygon(vectors)

        val result = polygon.getExtremes()

        Assertions.assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun triangleEqualsTest(){
        val MatPointA = MatPoint(5.0,5.0)
        val MatPointB = MatPoint(4.0, 12.0)
        val MatPointC = MatPoint(8.0, 23.0)
        val triangle1 = Triangle(MatPointA, MatPointB, MatPointC)
        val triangle2 = Triangle(MatPointA, MatPointC, MatPointB)

        Assertions.assertThat(triangle1).isEqualTo(triangle2)
        Assertions.assertThat(setOf(triangle1) == setOf(triangle2)).isTrue()
    }
}
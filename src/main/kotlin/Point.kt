import kotlin.math.*
import kotlin.random.Random

data class Point(val x: Double, val y: Double) {
    val length: Double
        get() = sqrt(x.pow(2) + y.pow(2))

    override fun toString(): String {
        return "($x, $y)"
    }

    fun normalize() = Point(
        x / length,
        y / length
    )
}

typealias Momentum = Point
typealias Vector = Point

operator fun Point.plus(that: Point) = Point(
    this.x + that.x,
    this.y + that.y
)

operator fun Point.minus(that: Point) = Point(
    this.x + that.x,
    this.y + that.y
)

operator fun Point.div(c: Double) = Point(
    this.x / c,
    this.y / c
)

operator fun Point.times(c: Int) = Point(
    this.x * c,
    this.y * c
)

infix fun Number.point(that: Number) = Point(this.toDouble(), that.toDouble())

fun generatePointInsideUnitCircle   (): Point {
    val r = sqrt(Random.nextDouble(0.0, 1.0))
    val t = Random.nextDouble(0.0, 1.0) * 2 * PI

    val x = r * cos(t)
    val y = r * sin(t)
    return x point y
}

fun generateMomentumWithLength1(): Momentum {
    val t = 2 * PI * Random.nextDouble(0.0, 1.0)

    val x = cos(t)
    val y = sin(t)

    return x point y
}
import org.jetbrains.letsPlot.coord.coordFixed
import org.jetbrains.letsPlot.geom.extras.arrow
import org.jetbrains.letsPlot.geom.geomPath
import org.jetbrains.letsPlot.geom.geomSegment
import org.jetbrains.letsPlot.ggplot
import org.jetbrains.letsPlot.themes.theme
import kotlin.math.*

fun main() {
    val initialPoint = generatePointInsideUnitCircle()
    val initialMomentum = generatePointInsideUnitCircle()

    println(initialPoint)
    println(initialMomentum)

    calcAndDraw(initialPoint, initialMomentum)
}

private const val DELTA = 3 // degree
private const val N = 10
private fun calcAndDraw(initialPoint: Point, initialMomentum: Momentum) {
    var point = initialPoint
    var momentum = initialMomentum
    val lines = mutableListOf<Line>()
    repeat(N) {
        val nextPoint = point + momentum
        val intersection = findIntersection(point lineTo nextPoint)
        lines.add(point lineTo intersection)

        if (it - 1 != N) {
            point = intersection
            momentum = calcNewMomentum(point, momentum)
        }
    }

    momentum *= -1

    val reverseLines = mutableListOf<Line>()
    var deviationFound = false
    repeat(N) {
        val nextPoint = point + momentum
        val intersection = findIntersection(point lineTo nextPoint)
        val line = point lineTo intersection
        reverseLines.add(line)
        if (atan(line.slope - lines[lines.lastIndex - it].slope) > DELTA && !deviationFound) {
            println("After ${it + 1} reflection the deviation is more than $DELTA")
            deviationFound = true
        }
        point = intersection
        momentum = calcNewMomentum(point, momentum)

    }

    if (!deviationFound) {
        println("There was no deviation more than $DELTA")
    }
    lines.forEach { println(it) }

    val plot = ggplot() +
            drawUnitCircle() +
            drawLines(lines, "blue") +
            drawLines(reverseLines, "red") + // comment this line to hide reversed lines
            theme() +
            coordFixed(1)

    plot.show()
}
private fun drawUnitCircle(): geomPath {
    val theta = (0..360).map { it.toDouble() }
    val xCircle = theta.map { cos(Math.toRadians(it)) }
    val yCircle = theta.map { sin(Math.toRadians(it)) }

    val dataCircle = mapOf(
        "x" to xCircle,
        "y" to yCircle
    )

    return geomPath(data = dataCircle, color = "green") { x = "x"; y = "y" }
}
private fun drawLines(lines: List<Line>, color: String): geomSegment {
    val dataPoints = mapOf(
        "startX" to lines.map { it.start.x },
        "startY" to lines.map { it.start.y },
        "endX" to lines.map { it.end.x },
        "endY" to lines.map { it.end.y }
    )

    return geomSegment(data = dataPoints, color = color, arrow = arrow(length = 10, angle = 45)) {
        x = "startX"
        y = "startY"
        xend = "endX"
        yend = "endY"
    }
}
private fun calcNewMomentum(point: Point, momentum: Momentum): Momentum {
    val (x, y) = point
    val (px, py) = momentum

    val a = y.pow(2) - x.pow(2)
    val b = -2 * x * y

    return ((a * px + b * py) point (b * px - a * py))
}
private fun findIntersection(line: Line): Point {
    val (startX, startY) = line.start
    val (endX, endY) = line.end
    val k = line.slope
    val b = line.yIntercept
    val _a = 1 + k.pow(2)
    val _b = 2 * k * b
    val _c = -1 + b.pow(2)

    val d = _b.pow(2) - 4 * _a * _c

    val ix1 = (-_b + sqrt(d)) / (2 * _a)
    val ix2 = (-_b - sqrt(d)) / (2 * _a)

    val iy1 = k * ix1 + b
    val iy2 = k * ix2 + b

    val intersections = listOf(ix1 point iy1, ix2 point iy2)
    return if (endX > startX)
        intersections.maxBy { it.x }
    else
        intersections.minBy { it.x }
}
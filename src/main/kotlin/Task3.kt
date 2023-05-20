import org.jetbrains.letsPlot.coord.coordFixed
import org.jetbrains.letsPlot.geom.geomLine
import org.jetbrains.letsPlot.geom.geomPath
import org.jetbrains.letsPlot.geom.geomSegment
import org.jetbrains.letsPlot.ggplot
import org.jetbrains.letsPlot.themes.theme
import kotlin.math.*


// NOTE: my centers of the circle are (-L/2, L/2)
fun main() {
    val l = 2.0
    val sides = listOf(-1, 1)

    var point = generatePointInsideUnitCircle() + (l / 2 point 0) * sides.random()
    var momentum = generateMomentumWithLength1()
    val lines = mutableListOf<Line>()
    repeat(30) {
        val line = point lineTo (point + momentum)
        val intersectionWithEdge = intersectionWithEdge(line, momentum)
        if (abs(intersectionWithEdge.x) < l / 2) {
            lines.add(point lineTo intersectionWithEdge)
            point = intersectionWithEdge
            momentum = Momentum(momentum.x, -momentum.y)
        } else {

            val (intersection, xc) = if (intersectionWithEdge.x >= l / 2) {
                Pair(intersectionWithRightSemiCircle(line, l), l / 2)
            } else {
                Pair(intersectionWithLeftSemiCircle(line, l), -l / 2)
            }

            lines.add(point lineTo intersection)
            point = intersection
            momentum = calcNewMomentum(point, momentum, xc)
        }
    }

    val plot = ggplot() +
            drawLeftSemiCircle(l) +
            plotLine((-l / 2 point 1) lineTo (l / 2 point 1)) +
            plotLine((-l / 2 point -1) lineTo (l / 2 point -1)) +
            drawRightSemiCircle(l) +
            drawLines(lines)
    theme() +
            coordFixed(1f)

    plot.show()
}

private fun drawLines(lines: List<Line>): geomSegment {
    val dataPoints = mapOf(
        "startX" to lines.map { it.start.x },
        "startY" to lines.map { it.start.y },
        "endX" to lines.map { it.end.x },
        "endY" to lines.map { it.end.y }
    )

    return geomSegment(data = dataPoints) {
        x = "startX"
        y = "startY"
        xend = "endX"
        yend = "endY"
    }
}

private fun intersectionWithEdge(line: Line, momentum: Momentum): Point {
    // y = +-1
    // y = kx + b
    // kx + b = +-1 => x = (+-1 - b) / k
    val y = if (momentum.y > 0) 1 else -1
    if (momentum.x == 0.0) {
        return line.start.x point y
    }
    return ((y - line.yIntercept) / line.slope) point y
}

private fun intersectionWithRightSemiCircle(line: Line, l: Double): Point {
    val k = line.slope
    val b = line.yIntercept
    val _a = 1 + k.pow(2)
    val _b = 2 * k * b - l
    val _c = -1 + b.pow(2) + (l / 2).pow(2)

    val d = _b.pow(2) - 4 * _a * _c

    val ix1 = (-_b + sqrt(d)) / (2 * _a)
    val ix2 = (-_b - sqrt(d)) / (2 * _a)

    val iy1 = k * ix1 + b
    val iy2 = k * ix2 + b

    val intersections = listOf(ix1 point iy1, ix2 point iy2)
    return intersections.maxBy { it.x }
}

private fun intersectionWithLeftSemiCircle(line: Line, l: Double): Point {
    val k = line.slope
    val b = line.yIntercept
    val _a = 1 + k.pow(2)
    val _b = 2 * k * b + l
    val _c = -1 + b.pow(2) + (l / 2).pow(2)

    val d = _b.pow(2) - 4 * _a * _c

    val ix1 = (-_b + sqrt(d)) / (2 * _a)
    val ix2 = (-_b - sqrt(d)) / (2 * _a)

    val iy1 = k * ix1 + b
    val iy2 = k * ix2 + b

    val intersections = listOf(ix1 point iy1, ix2 point iy2)
    return intersections.minBy { it.x }
}

private fun drawLeftSemiCircle(l: Double): geomPath {
    val theta = (90..270).map { it.toDouble() }
    val xCircle = theta.map { cos(Math.toRadians(it)) - l / 2 }
    val yCircle = theta.map { sin(Math.toRadians(it)) }

    val dataCircle = mapOf(
        "x" to xCircle,
        "y" to yCircle
    )

    return geomPath(data = dataCircle) { x = "x"; y = "y" }
}

private fun drawRightSemiCircle(l: Double): geomPath {
    val theta = (-90..90).map { it.toDouble() }
    val xCircle = theta.map { cos(Math.toRadians(it)) + l / 2 }
    val yCircle = theta.map { sin(Math.toRadians(it)) }

    val dataCircle = mapOf(
        "x" to xCircle,
        "y" to yCircle
    )

    return geomPath(data = dataCircle) { x = "x"; y = "y" }
}

private fun calcNewMomentum(point: Point, momentum: Momentum, xc: Double): Momentum {
    val (x, y) = point
    val (px, py) = momentum

    val a = y.pow(2) - (x - xc).pow(2)
    val b = -2 * (x - xc) * y

    return ((a * px + b * py) point (b * px - a * py))
}

fun plotLine(line: Line) = geomLine(
    data = mapOf(
        "x" to listOf(line.start.x, line.end.x),
        "y" to listOf(line.start.y, line.end.y)
    )
) {
    x = "x"
    y = "y"
}
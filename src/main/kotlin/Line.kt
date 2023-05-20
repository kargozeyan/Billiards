data class Line(val start: Point, val end: Point) {
    val slope: Double
        get() {
            if (start.x == end.x) {
                return 0.0
            }

            return (end.y - start.y) / (end.x - start.x)
        }

    val yIntercept: Double
        get() = start.y - slope * start.x
}

infix fun Point.lineTo(that: Point) = Line(this, that)
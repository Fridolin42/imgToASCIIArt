import java.awt.Color
import java.awt.Point
import java.awt.geom.Point2D

class Cluster {
    companion object {
        val clusterList = ArrayList<Cluster>()
    }

    init {
        clusterList.add(this)
    }

    val pixels = ArrayList<Pixel>()

    fun expandCluster(img: Array<Array<Pixel>>) {
        val neighbourList = ArrayList<Pixel>()
        pixels.forEach {
            if (it.x > 0) neighbourList.add(img[it.x - 1][it.y])
            if (it.x < img.size - 1) neighbourList.add(img[it.x + 1][it.y])
            if (it.y > 0) neighbourList.add(img[it.x][it.y - 1])
            if (it.y < img[0].size - 1) neighbourList.add(img[it.x][it.y + 1])
        }
        neighbourList.removeAll(neighbourList.filter { pixels.contains(it) }.toSet())
        val avg = avgColor()
        neighbourList
            .minBy {  it.distance(avg) + 0.25 * it.distance(avg) * middlePosition().distance(it.x.toDouble(), it.y.toDouble()) }
            .addToCluster(this)
    }

    private fun avgColor(): Color {
        val r = pixels.sumOf { it.color.red } / pixels.size
        val g = pixels.sumOf { it.color.green } / pixels.size
        val b = pixels.sumOf { it.color.blue } / pixels.size
        return Color(r, g, b)
    }

    private fun middlePosition(): Point {
        val x = pixels.sumOf { it.x } / pixels.size
        val y = pixels.sumOf { it.y } / pixels.size
        return Point(x, y)
    }
}
import java.awt.Color
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class Pixel(val x: Int, val y: Int, val color: Color) {
    private val clusters: ArrayList<Cluster> = ArrayList()

    init {
        addToCluster(Cluster())
    }

    fun distance(c1: Color): Double = sqrt(
        abs(c1.red - this.color.red).toDouble().pow(2) + abs(c1.green - this.color.green).toDouble()
            .pow(2) + abs(c1.blue - this.color.blue).toDouble().pow(2)
    )

    fun addToCluster(c: Cluster) {
        clusters.add(c)
        c.pixels.add(this)
    }

    fun clusterAmount(): Int = clusters.size
    fun calcBorderColor(img: Array<Array<Pixel>>): Color {
        if ((this.x > 0) && !img[this.x - 1][this.y].clusters.any { this.clusters.contains(it) }) return Color.BLACK
        if ((this.x < img.size - 1) && !img[this.x + 1][this.y].clusters.any { this.clusters.contains(it) }) return Color.BLACK
        if ((this.y > 0) && !img[this.x][this.y - 1].clusters.any { this.clusters.contains(it) }) return Color.BLACK
        if ((this.y > img[0].size - 1) && !img[this.x][this.y + 1].clusters.any { this.clusters.contains(it) }) return Color.BLACK
        return Color.WHITE
    }
}

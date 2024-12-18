import java.awt.Color
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class Pixel(val x: Int, val y: Int, val color: Color) {
    val clusters: ArrayList<Cluster> = ArrayList()
    var top: Pixel? = null
    var left: Pixel? = null
    var bottom: Pixel? = null
    var right: Pixel? = null

    init {
        addToCluster(Cluster())
    }

    fun distance(c1: Pixel): Double = sqrt(
        abs(c1.color.red - this.color.red).toDouble().pow(2) + abs(c1.color.green - this.color.green).toDouble()
            .pow(2) + abs(c1.color.blue - this.color.blue).toDouble().pow(2)
    )

    fun addToCluster(c: Cluster) {
        clusters.add(c)
        c.pixels.add(this)
    }

    fun clusterAmount (): Int = clusters.size
}

import java.awt.Color
import java.io.File
import javax.imageio.ImageIO

fun main() {
    val img = ImageIO.read(File("imgIn"))
    val pixels = Array(img.width) { x -> Array(img.height) { y -> Pixel(x, y, Color(img.getRGB(x, y))) } }
    for (x in 0 until img.width)
        for (y in 0 until img.height) {
            if (x > 0) pixels[x][y].left = pixels[x - 1][y]
            if (y > 0) pixels[x][y].bottom = pixels[x][y - 1]
            if (x < img.width - 1) pixels[x][y].right = pixels[x + 1][y]
            if (y < img.height - 1) pixels[x][y].top = pixels[x][y + 1]
        }

    repeat(25) {
        for (pixelColum in pixels) {
            for (pixel in pixelColum) {
                getClosestNeighbour(pixel, pixels).addToCluster(pixel.clusters[0])
            }
        }
    }
}

fun getClosestNeighbour(pixel: Pixel, img: Array<Array<Pixel>>): Pixel {
    val neightbourMap = HashMap<Pixel, Double>()
    for (i in (-1..1)) {
        for (j in (-1..1)) {
            val secondPixel = img[pixel.x + i][pixel.y + j]
            neightbourMap[secondPixel] = pixel.distance(secondPixel)
        }
    }
    return neightbourMap.minByOrNull { it.value }!!.key
}
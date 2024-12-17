import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.max
import kotlin.math.min


fun main() {
    val input = "albert"
    val img = ImageIO.read(File("./imgIn/$input.png"))
    val imgOut = filterImage(clusterImage(img))
    ImageIO.write(imgOut, "png", File(genImgName(input)))
}

fun filterImage(img: BufferedImage): BufferedImage {
    for (x in 0..<img.width)
        for (y in 0..<img.height)
            if (Color(img.getRGB(x, y)) == Color.BLACK &&
                calcBorderClusterSize(x, y, deepCopy(img)) < 10
            ) img.setRGB(x, y, Color.white.rgb)
    return img
}

fun calcBorderClusterSize(x: Int, y: Int, img: BufferedImage): Int {
    if (Color(img.getRGB(x, y)) != Color.black) return 0
    img.setRGB(x, y, Color.orange.rgb)
    var size = 1
    for (mx in max(0, x - 1)..min(x + 1, img.width - 1))
        for (my in max(0, y - 1)..min(y + 1, img.height - 1))
            size += calcBorderClusterSize(mx, my, img)
    return size
}

fun clusterImage(img: BufferedImage): BufferedImage {
    val pixels = Array(img.width) { x -> Array(img.height) { y -> Pixel(x, y, Color(img.getRGB(x, y))) } }
    repeat(25) { i ->
        print("\r$i/24")
        Cluster.clusterList.forEach { it.expandCluster(pixels) }
    }
    println("\ndone")
    val imgOut = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_RGB)
    for (x in 0..<img.width)
        for (y in 0..<img.height)
            imgOut.setRGB(x, y, pixels[x][y].calcBorderColor(pixels).rgb)
    return imgOut
}

fun genImgName(inputName: String): String {
    val files = File("./imgOut").listFiles()?.map { it.nameWithoutExtension }
    files?.let {
        for (i in 0..Int.MAX_VALUE)
            if (!files.contains("out_$inputName$i")) return "./imgOut/out_$inputName$i.png"
    }
    throw Error("Please Clean your folder")
}

fun deepCopy(bi: BufferedImage): BufferedImage {
    val cm = bi.colorModel
    val isAlphaPremultiplied = cm.isAlphaPremultiplied
    val raster = bi.copyData(null)
    return BufferedImage(cm, raster, isAlphaPremultiplied, null)
}
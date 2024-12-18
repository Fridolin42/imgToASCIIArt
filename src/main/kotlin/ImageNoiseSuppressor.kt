import java.awt.Color

import java.awt.image.BufferedImage


class ImageNoiseSuppressor {
    companion object {
        // Methode zur Anwendung des Bilateral-Filters auf ein BufferedImage
        fun applyBilateralFilter(inputImage: BufferedImage, radius: Int, sigmaSpatial: Double, sigmaRange: Double): BufferedImage? {
            val width = inputImage.width
            val height = inputImage.height
            val outputImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

            // Precompute spatial Gaussian weights
            val spatialKernel = Array(2 * radius + 1) { DoubleArray(2 * radius + 1) }
            for (i in -radius..radius) {
                for (j in -radius..radius) {
                    val distance = (i * i + j * j).toDouble()
                    spatialKernel[i + radius][j + radius] = Math.exp(-distance / (2 * sigmaSpatial * sigmaSpatial))
                }
            }

            // Apply filter
            for (x in 0 until width) {
                for (y in 0 until height) {
                    applyFilterToPixel(inputImage, outputImage, x, y, radius, sigmaRange, spatialKernel)
                }
            }
            return outputImage
        }

        // Berechnet den neuen Pixelwert für ein Pixel an (x, y)
        private fun applyFilterToPixel(inputImage: BufferedImage, outputImage: BufferedImage, x: Int, y: Int, radius: Int, sigmaRange: Double, spatialKernel: Array<DoubleArray>) {
            var weightSum = 0.0
            var redSum = 0.0
            var greenSum = 0.0
            var blueSum = 0.0
            val centerColor = Color(inputImage.getRGB(x, y))
            val centerRed = centerColor.red
            val centerGreen = centerColor.green
            val centerBlue = centerColor.blue
            val width = inputImage.width
            val height = inputImage.height
            for (i in -radius..radius) {
                for (j in -radius..radius) {
                    val neighborX = clamp(x + i, 0, width - 1)
                    val neighborY = clamp(y + j, 0, height - 1)
                    val neighborColor = Color(inputImage.getRGB(neighborX, neighborY))
                    val neighborRed = neighborColor.red
                    val neighborGreen = neighborColor.green
                    val neighborBlue = neighborColor.blue

                    // Intensitätsbasierte Gewichtung berechnen
                    val rangeWeightRed = Math.exp(-Math.pow((neighborRed - centerRed).toDouble(), 2.0) / (2 * sigmaRange * sigmaRange))
                    val rangeWeightGreen = Math.exp(-Math.pow((neighborGreen - centerGreen).toDouble(), 2.0) / (2 * sigmaRange * sigmaRange))
                    val rangeWeightBlue = Math.exp(-Math.pow((neighborBlue - centerBlue).toDouble(), 2.0) / (2 * sigmaRange * sigmaRange))
                    val weightRed = spatialKernel[i + radius][j + radius] * rangeWeightRed
                    val weightGreen = spatialKernel[i + radius][j + radius] * rangeWeightGreen
                    val weightBlue = spatialKernel[i + radius][j + radius] * rangeWeightBlue

                    // Summation für die gewichteten Farbwerte
                    redSum += neighborRed * weightRed
                    greenSum += neighborGreen * weightGreen
                    blueSum += neighborBlue * weightBlue
                    weightSum += weightRed // Hier reicht ein Gewicht aus, da sie ähnlich sind
                }
            }

            // Neue Farbwerte berechnen und auf das Bild anwenden
            val newRed = (redSum / weightSum).toInt()
            val newGreen = (greenSum / weightSum).toInt()
            val newBlue = (blueSum / weightSum).toInt()
            outputImage.setRGB(x, y, Color(clamp(newRed, 0, 255), clamp(newGreen, 0, 255), clamp(newBlue, 0, 255)).rgb)
        }

        // Hilfsmethode, um Werte innerhalb eines Bereichs zu begrenzen
        private fun clamp(value: Int, min: Int, max: Int): Int {
            return Math.max(min, Math.min(max, value))
        }
    }
}
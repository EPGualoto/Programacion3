import java.awt.{Color, Graphics, Graphics2D}
import javax.swing.{JFrame, JPanel}

object Grafica {
  def main(args: Array[String]): Unit = {

    // Datos obtenidos del ccv
    val edades = List(67,361,3048,1371,62,8,75,268,10,848)

    // Estadisticas basicas
    val meanEdad = edades.sum.toDouble / edades.size
    val variance = edades.map(e => Math.pow(e - meanEdad, 2)).sum / edades.size
    val stdDeviation = Math.sqrt(variance)

    println(s"Media de edades: $meanEdad")
    println(s"Desviación estándar de edades: $stdDeviation")

    // Crear el grafico de distribucion de edades
    val frame = new JFrame("Distribución de Edades")
   // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    frame.setSize(600, 400)
    frame.add(new HistogramPanel(edades))
    frame.setVisible(true)
  }

  class HistogramPanel(edades: List[Int]) extends JPanel {
    override def paintComponent(g: Graphics): Unit = {
      super.paintComponent(g)
      val g2d = g.asInstanceOf[Graphics2D]

      val binWidth = 20
      val binCount = edades.max / binWidth + 1
      val bins = Array.fill(binCount)(0)

      // Contar las frecuencias en cada bin
      edades.foreach { edad =>
        val binIndex = edad / binWidth
        bins(binIndex) += 1
      }

      // Dibujar el histograma
      val panelWidth = getWidth
      val panelHeight = getHeight
      val barWidth = panelWidth / bins.length

      g2d.setColor(Color.BLUE)
      for (i <- bins.indices) {
        val barHeight = (bins(i).toDouble / edades.size) * panelHeight
        g2d.fillRect(i * barWidth, (panelHeight - barHeight).toInt, barWidth, barHeight.toInt)
      }
    }
  }
}
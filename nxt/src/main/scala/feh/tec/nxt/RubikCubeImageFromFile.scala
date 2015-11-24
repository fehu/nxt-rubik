package feh.tec.nxt

import feh.tec.nxt.RubikCubeImage.Side
import feh.tec.rubik.RubikCube.SideName
import feh.util.file._


object RubikCubeImageFromFile {
  def raw(file: File): RubikCubeImage[String] = {
    val lines = file.withInputStream(File.read[Seq[String]]).get.toList
    val data = readImageLines(lines.tail, None)
    RubikCubeImage(
      data.groupBy(_._1)
        .mapValues(l => Side(l.map(p => p._2 -> p._3).toMap))
        .values.toSeq
    )
  }

  private def readImageLines(lines: List[String], side: Option[SideName]): List[(SideName, (Int, Int), String)] =
    lines match {
      case h :: t if h.trim.isEmpty => readImageLines(t, side)
      case h :: t if h startsWith "-- " =>
        val side = SideName.fromString(h.drop(3).trim)
        readImageLines(t, Some(side))
      case h :: t if side.isDefined => h.split(',') match {
        case Array(x, y, v) => (side.get, x.trim.toInt -> y.trim.toInt,  v.trim) :: readImageLines(t, side)
      }
      case Nil => Nil
    }
}

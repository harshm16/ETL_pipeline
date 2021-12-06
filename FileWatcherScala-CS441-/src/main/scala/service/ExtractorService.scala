package service

import constants.ShellCommands

import java.io.File
import scala.sys.process._

class ExtractorService {

  def getData(boundaries: String, file: File): Array[String] = {
    val firstLine: Int = boundaries.split(" ")(0).toInt + 1
    val lastLine: Int  = boundaries.split(" ")(1).toInt
    val data: Array[String] = s"${ShellCommands.sed} '$firstLine,$lastLine p' ${file.getAbsolutePath}".!!.split("\n")
    data
  }
}


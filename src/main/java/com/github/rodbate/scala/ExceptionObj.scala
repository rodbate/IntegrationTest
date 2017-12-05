package com.github.rodbate.scala


import scala.util.control.Breaks._

object ExceptionObj {

  def main(args: Array[String]): Unit = {


    val list = List(1, 0, -1, 10, 6)

    var times = 0

    breakable {
      for (i <- list) {
        times += 1
        if (i < 0) {
          break
        }
      }
    }

    println(s"times is $times")



    block {
      true
    }


  }


  @throws(classOf[IllegalArgumentException])
  def testEx(): Int = {
    0
  }

  def block(op: => Boolean): Unit = {
    println("block.....")
    op
  }

}
package com.github.rodbate.scala.oo


import scala.collection.mutable.ArrayBuffer


abstract class Element {
  println("parent class...")
  def contents: Array[String]
  val height: Int = contents.length
  val width: Int = if (height ==0) 0 else contents(0).length
}


class ArrayElement(val contents: Array[String]) extends Element {

  def this() = this(Array("hello"))

  override val width: Int = {
    var max = 0
    for (line <- contents) {
      if (line.length > max) {
        max = line.length
      }
    }
    max
  }


  override def toString: String = {
    val sb = new StringBuilder
    sb ++= s"Element height=$height  width=$width\n"
    contents.foreach(sb ++= _ ++= "\n")
    sb.toString()
  }
}





class Msg(val msg: String = "default msg")



trait Philo {
  def desc: Unit
}

class Phi(des: String) extends Philo {
  override def desc: Unit = println("implementation")
}


trait IntQueue {
  def get: Int
  def put(e: Int): Unit
  def hasNext: Boolean
}


class BasicIntQueue() extends IntQueue {
  val buf = new ArrayBuffer[Int]
  override def get: Int = buf.remove(0)

  override def put(e: Int): Unit = {
    println("basic int queue")
    buf += e
  }
  override def hasNext: Boolean = buf != null && buf.nonEmpty
}

trait DoublingQueue extends IntQueue {
  abstract override def put(e: Int): Unit = {
    println("doubling queue")
    super.put(e * 2)
  }
}

trait Incrementing extends IntQueue {
  abstract override def put(e: Int): Unit = {
    println("incrementing")
    super.put(e + 1)
  }
}

trait PositiveQueue extends IntQueue {
  abstract override def put(e: Int): Unit = if (e >= 0) super.put(e)
}



class ConQueue extends BasicIntQueue with DoublingQueue {
  override def put(e: Int): Unit = {
    println("con queue")
    super.put(e)
  }
}


class TestInt(val d: Int = 1)





sealed abstract class Expr

case class Var(name: String) extends Expr

case class Number(num: Double) extends Expr

case class UnOp(operator: String, arg: Expr) extends Expr

case class BinOp(operator: String, left: Expr, right: Expr) extends Expr



object Dir extends Enumeration {
  type Dir = Value
  val DOWN, LEFT, RIGHT = Value

  val UP = Value("heihei")
  def name: String = "DEFAULT"
}


trait Abstract {
  type T
  def transform(x: T): T
  val initial: T
  var current: T
}


class Sub extends Abstract {
  override type T = String

  override def transform(x: String): String = s"transform-$x"

  override val initial: String = "initial"
  override var current: String = initial

}



object ImplicitConversion {

  implicit def int2String(i: Int): String = s"$i"
}

class ImplicitConversionClass {
  val s: String = "23"

}



object Main {

  lazy val i: Int = 1

  def error(msg: String = "illegal argument ex"): Nothing = {
    throw new IllegalArgumentException(msg)
  }

  def main(args: Array[String]): Unit = {
    import ImplicitConversion._

    val i: Int = 578

    println(i length)

    val im = new ImplicitConversionClass

    val s: String = "23"

  }
}



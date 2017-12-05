
package com.github.rodbate.scala.oo





class Rational(n: Int, d: Int) extends Ordered[Rational]{
  require(d != 0)
  private val gcd: Int = gcd(n, d)
  val numerator: Int = n / gcd
  val denominator: Int = d / gcd

  def this(n: Int) = this(n, 1)

  override def toString: String = numerator + "/" + denominator

  def + (other: Rational): Rational =
    new Rational(numerator * other.denominator + denominator * other.numerator, denominator * other.denominator)

  def + (other: Int): Rational = this + new Rational(other)

  private def gcd(n: Int, d: Int): Int = if (d == 0) n else gcd(d, n % d)

  override def compare(that: Rational): Int = {
    if (that == null) {
      1
    } else {
      this.numerator * that.denominator - that.numerator * this.denominator
    }
  }

  override def equals(obj: scala.Any): Boolean = {
    if (obj == null) return false
    if (this.getClass == obj.getClass) {
      val that = obj.asInstanceOf[Rational]
      return (this eq that) ||  (this.numerator * that.denominator == that.numerator * this.denominator)
    }
    false
  }
}


object Rational {

  implicit def intToRational(i: Int): Rational = new Rational(i)

  def main(args: Array[String]): Unit = {

    val r1 = new Rational(1, 3)
    val r2 = new Rational(2, 6)
    val r3 = new Rational(6)

    println(r1 == r2)

  }
}
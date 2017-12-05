package com.github.rodbate.scala

import scala.collection.mutable

import scala.collection.immutable


class ChecksumAccumulator {
  private var sum = 0
  def add(b: Byte): Unit = sum += b
  def checksum: Int = ~(sum & 0xff) + 1

}

object ChecksumAccumulator {
  private val cache = mutable.Map.empty[String,Int]
  def get(key: String): Int = {
    if (cache.contains(key)){
      cache(key)
    } else {
      val acc = new ChecksumAccumulator
      for (c <- key)
        acc.add(c toByte)
      cache += (key -> acc.checksum)
      cache(key)
    }
  }
  /**
    * i
    * as aff fff
    * j
    *
    */
  def deleteChar(str: String): String = {

    val a = 2.unary_~

    var ret = str
    var i = 0
    var j = 0
    str.foreach(c => {

    })

    null
  }



}

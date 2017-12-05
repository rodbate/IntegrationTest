package com.github.rodbate.scala

import java.util.concurrent.CompletableFuture


object ObjectMain {


  def main(args: Array[String]): Unit = {

    //println(ChecksumAccumulator.get("checksum"))

    CompletableFuture.supplyAsync(() => {
      Thread.sleep(2000)
      "msg-1"
    }).thenApply(msg => {println(msg); "success"})
      .thenAccept(msg => printf(s"accept : $msg"))
      .join()

    //Thread.currentThread().join()



  }

  def sendMsg(): String = {
    require("a" != null)
    Thread.sleep(5000)
    "msg-1"
  }

  var sendMsg1 = () => {
    Thread.sleep(5000)
    "msg-1"
  }



  def notifyMsg(): Unit = {
      println("msg ... ")
  }
}
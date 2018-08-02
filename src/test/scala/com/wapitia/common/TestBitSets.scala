package com.wapitia.common

import scala.collection.BitSet

object TestBitSets extends App {

  def testBitSeq() {

    val bs = BitSet(4 , 5,  8,  9)

    val s = bs.toSeq
    println(s)
    println(s.getClass)
  }

  testBitSeq()
}

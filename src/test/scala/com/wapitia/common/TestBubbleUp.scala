package com.wapitia
package common

object TestBubbleUp extends App {

  def testNil() {
    println("testNil")
    val res = bubbleUp(List[Int]())
    assert(res == List())
  }

  def testSingle() {
    println("testSingle")
    val res = bubbleUp(List[Int](6))
    assert(res.head == 6)
  }

  def testPresorted1() {
    println("testPresorted1")
    val res = bubbleUp(List[Int](6, 8, 9 ,10))
    assert(res.head == 6)
  }

  def testMix1() {
    println("testMix1")
    val res = bubbleUp(List[Int](10, 7, 4, 8))
    assert(res.head == 4)
  }

  def testMix2() {
    println("testMix2")
    val res = bubbleUp(List[Int](10, 8, 16, 34, 7, 4, 8))
    assert(res.head == 4)
  }

  def testMix3() {
    println("testMix3")
    val res = bubbleUp(List[Int](34,  4, 16, 8, 7, 10, 8))
    println(res)
    assert(res.head == 4)
  }

  testNil()
  testSingle()
  testPresorted1()
  testMix1()
  testMix2()
  testMix3()
}

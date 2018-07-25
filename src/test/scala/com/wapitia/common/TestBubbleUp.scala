package com.wapitia
package common

object TestBubbleUp extends App {

  def testNil() {
    val res = bubbleUp(List[Int]())
    assert(res == List())
    println("testNil OK")
  }

  def testSingle() {
    val res = bubbleUp(List[Int](6))
    assert(res.head == 6)
    println("testSingle OK")
  }
  
  def testPresorted1() {
    val res = bubbleUp(List[Int](6, 8, 9 ,10))
    assert(res.head == 6)
    println("testPresorted1 OK")
  }
  
  def testMix1() {
    val res = bubbleUp(List[Int](10, 7, 4, 8))
    assert(res.head == 4)
    println("testMix1 OK")
  }
  
  
  def testMix2() {
    val res = bubbleUp(List[Int](10, 8, 16, 34, 7, 4, 8))
    assert(res.head == 4)
    println("testMix2 OK")
  }
  
  testNil()
  testSingle()
  testPresorted1()
  testMix1()
  testMix2()
}

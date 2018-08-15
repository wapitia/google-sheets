package com.wapitia
package common

import org.junit.Test
import org.junit.Assert._

class BubbleUpTest {

  @Test
  def testNil() {
    val res = bubbleUp(List[Int]())
    assertEquals(List(), res)
  }

  @Test
  def testSingle() {
    val res = bubbleUp(List[Int](6))
    assertEquals(6, res.head)
  }

  @Test
  def testPresorted1() {
    val res = bubbleUp(List[Int](6, 8, 9 ,10))
    assertEquals(6, res.head)
  }

  @Test
  def testMix1() {
    val res = bubbleUp(List[Int](10, 7, 4, 8))
    assertEquals(4, res.head)
  }

  @Test
  def testMix2() {
    val res = bubbleUp(List[Int](10, 8, 16, 34, 7, 4, 8))
    assertEquals(4, res.head)
  }

  @Test
  def testMix3() {
    val res = bubbleUp(List[Int](34,  4, 16, 8, 7, 10, 8))
    assertEquals(4, res.head)
  }
}

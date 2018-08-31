package com.wapitia
package common

import com.wapitia.calendar.Cycle
import org.junit.Test
import org.junit.Assert._

class CanonicalListTest {

  @Test def testCanonicalList() {
    assertEquals(List(2, 3, 4), canonicalList(4,3,2))
    assertEquals(List(1, 3), canonicalList(1,1,1,3))
    assertEquals(List(), canonicalList())
    assertEquals(List(1, 3, 4, 7, 8, 9), canonicalList(9,8,3,7,8,3,4,1,4,8))
    assertEquals(List("A", "B", "C"), canonicalList("B","C","B","A"))
  }

}

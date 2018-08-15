package com.wapitia
package properties

import java.util.{Properties => JavaProperties}
import java.io.{InputStream, StringReader}

import org.junit.Test
import org.junit.Assert._

object KeyedPropertiesTest

class KeyedPropertiesTest {

  @Test
  def testLoad1() {
    val accessClass: Class[_ <: KeyedPropertiesTest.type] = KeyedPropertiesTest.getClass

    val in: InputStream = accessClass.getResourceAsStream("testKeyedProps.properties")
    val props = loadJavaProperties(in)
    println(props)
//    assertEquals(props, "myprops")
  }

}

package com.wapitia.properties

import com.wapitia.rb.meta.RBMetaProperties
import java.io.InputStream
import java.util.{Properties => JavaProperties}

class TestKeyedProperties

object TestKeyedProperties extends App {

  def testLoad1() {
    val accessClass: Class[_ <: TestKeyedProperties.type] = TestKeyedProperties.getClass

    val in: InputStream = accessClass.getResourceAsStream("/rbmapmetadata.properties")
    val props = loadProperties(in)
    println(props)
  }

  testLoad1()

}

package com.wapitia
package properties

import java.util.{Properties => JavaProperties}
import java.io.{InputStream, StringReader}

import org.junit.Test
import org.junit.Assert._
import java.io.FileOutputStream
import java.io.File

object KeyedPropertiesTest

class KeyedPropertiesTest {

  @Test
  def testLoad1() {
    val accessClass: Class[_ <: KeyedPropertiesTest.type] = KeyedPropertiesTest.getClass

    val in: InputStream = accessClass.getResourceAsStream("testKeyedProps.properties")
    val props = loadJavaProperties(in)
    val fo: File = new File("target/testResults/KeyPropertiesTest/testKeyedPropsOut.xml")
    val par = fo.getParentFile()
    val created = par.mkdirs()
    println(s"created? $created")
    val fos: FileOutputStream = new FileOutputStream(fo)
    props.storeToXML(fos, "my comment\nMore comments")
    println(props)
//    assertEquals(props, "myprops")
  }

}

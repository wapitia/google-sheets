package com.wapitia
package properties

import java.util.{Properties => JavaProperties}
import java.io.{InputStream, StringReader}

import org.junit.Test
import org.junit.Ignore
import org.junit.Assert._
import java.io.FileOutputStream
import java.io.File

object KeyedPropertiesTest

class KeyedPropertiesTest {

  @Test
  def tryLoad() {
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
  }

  def getLicencePolicy(keyProps: KeyedProperties, whichState: Option[String]): Option[String] = {
    val subMap: Map[String,Option[String]] = Map( "state" -> whichState )
    keyProps.getKeyedProperty(subMap, "dmv.${state}.licence.policy")
  }

  @Ignore // WIP @Test
  def testLP() {
    val accessClass: Class[_ <: KeyedPropertiesTest.type] = KeyedPropertiesTest.getClass

    val in: InputStream = accessClass.getResourceAsStream("testKeyedProps.properties")
    val keyProps: KeyedProperties = KeyedProperties.load(in)
    val testMap = Map(
      "30-day w/birthday" -> getLicencePolicy(keyProps, Some("CO")),
      "30-day" -> getLicencePolicy(keyProps, Some("WY")),
      "No grace period AT_ANY_TIME_FOR_NE!" -> getLicencePolicy(keyProps, Some("NE")),
      "No grace period AT_ANY_TIME_FOR_UNKNOWN!" -> getLicencePolicy(keyProps, None),
      "working-on-it in NY" -> getLicencePolicy(keyProps, Some("NY")),
      "working-on-it, perhaps" -> getLicencePolicy(keyProps, Some("KY"))
    )
    testMap.foreach { case (exp, act) =>  assertEquals(exp, act) }
  }

  @Test
  def testPatternParser() {

    import KeyedProperties._

    val jprops: java.util.Properties = new java.util.Properties()
    jprops.setProperty("county", "Nye")
    jprops.setProperty("state.with.Nye", "Nye County of Nevada")

    val kp: Params = Map("state" -> Option("Nevada"))

    val parser: PatternParser = new PatternParser(kp, jprops)

    val res2: String = parser.parse("state${county}foo")
    println(res2)

    val res: String = parser.parse("state")
    println(res)

    val res3: String = parser.parse("state.of.${state}")
    println(res3)

    val res4: String = parser.parse("state.with.${county}")
    println(res4)

  }

}

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
    testMap.foreach { case (exp, act) => assertEquals(exp, act) }
  }

  @Test
  def testPatternParser() {

    import KeyedProperties._

    val jprops: java.util.Properties = new java.util.Properties()
    jprops.setProperty("county", "Nye")
    jprops.setProperty("state.with.Nye", "Nye County of Nevada")
    jprops.setProperty("restofty", "ty")

    val kp: KeyedPropertiesParams = Map("state" -> Option("Nevada"))

//    val parser: PatternExpander = new PatternExpander(kp, jprops, LookupFunc, new PatternEvaluator(DefaultBadLookupFunc))
    val parser: PatternExpander = PatternExpander.default(kp, jprops, PatternEvaluator.Default)

    val testMap = Map(
      "state${" -> parser.parse("state${"),
      "state$" -> parser.parse("state$"),
      "state{" -> parser.parse("state{"),
      "stateNyefoo" -> parser.parse("state${county}foo"),
      "state${county}foo" -> parser.parse("state$${county}foo"),
      "state" -> parser.parse("state"),
      "state.of.Nevada" -> parser.parse("state.of.${state}"),
      "state.of.${state}" -> parser.parse("state.of.$${state}"),
      "state.with.Nye" -> parser.parse("state.with.${county}"),
        // malformed patterns returned unparsed
      "$restofty" -> parser.parse("$restofty"),
      "{$restofty}" -> parser.parse("{$restofty}"),
      "coun_NOT_FOUND" -> parser.parse("${coun}"),
      "coun{ty}" -> parser.parse("coun{${restofty}}"),
      "Nye" -> parser.parse("${coun${restofty}}"),
      "coun_NOT_FOUND}" -> parser.parse("${coun}}"),
      "coun{$r_NOT_FOUND" -> parser.parse("${coun{$r}")
    )
    testMap.foreach { case (exp, act) => assertEquals(exp, act) }
//    testMap.foreach { case (exp, act) => println("" + exp + " -> " + act) }
  }

  @Test
  def testGetKeyedProperty1() {

    import KeyedProperties._

    val jprops: java.util.Properties = new java.util.Properties()
    jprops.setProperty("county", "Nye")
    jprops.setProperty("state.with.Nye", "Nye County of Nevada")
    jprops.setProperty("stateNyefoo", "Nye Foo")
    jprops.setProperty("state.of.Nevada", "Whole of nevada state")

    val keyedProps = KeyedProperties(jprops)

    val kp: KeyedPropertiesParams = Map("state" -> Option("Nevada"))

    val testMap = Map(
      Some("Nye Foo") -> keyedProps.getKeyedProperty(kp, "state${county}foo"),
      Some("Nevada") -> keyedProps.getKeyedProperty(kp, "state"),
      Some("Whole of nevada state") -> keyedProps.getKeyedProperty(kp, "state.of.${state}"),
      Some("Nye County of Nevada") -> keyedProps.getKeyedProperty(kp, "state.with.${county}"),
      None -> keyedProps.getKeyedProperty(kp, "bogus")
    )
    testMap.foreach { case (exp, act) => assertEquals(exp, act) }
  }
}

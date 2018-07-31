package com.wapitia.rb
package meta

import java.util.{Properties => JavaProperties}

sealed case class PropSub(name: String, key: String, dots: Int)

class RBMetaProperties(props: JavaProperties) {

  val suite: Option[String] = Option(props.getProperty("rb.suite"))

  /**
   * Get a property of one of the forms:
   *
   * ==Form 1: ==
   *
   *   getProperty("rb.md.sheet.name.region")
   *   - look for the string property value corresponding to "rb.md.sheet.name.region".
   *   - If found return the value as Some string, otherwise return None
   *
   * ==Form 2: (suite substitution with no dot manipulation) ==
   *
   *   getProperty("rb.md.{suite}sheet.name.region")
   *
   * ==Form 3: (suite substitution with optional trailing dot) ==
   *
   *   getProperty("{suite.}sheet.name.region")
   *
   * ==Form 4: (suite substitution with optional leading dot) ==
   *
   *   getProperty("rb.md{.suite}")
   *
   */
  def getProperty(name: String): Option[String] = {
    val subName = "suite"
    val subsList = List(
        PropSub(name, "{" + subName + "}", 0),
        PropSub(name, "{" + subName + ".}", 1),
        PropSub(name, "{." + subName + "}", 1),
        PropSub(name, "{." + subName + ".}", 2))
    getProperty(name, subsList, suite)
  }

  def getProperty(name: String, substKey: List[PropSub], subst: Option[String]): Option[String]  = {
    None
  }
}

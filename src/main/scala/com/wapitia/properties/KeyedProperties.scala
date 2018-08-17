package com.wapitia
package properties

import java.util.{Properties => JavaProperties}
import com.wapitia.common.{Enum,EValue}

/** KeyedProperties wraps a Java `Properties` object with pattern substitution
 *  availability.
 *
 *  @example
 *
 *  `
 *  # Properties file
 *  dmv.CO.licence.policy = 30-day w/birthday
 *  dmv.WY.licence.policy = 30-day
 *  dmv.[NY|NJ|DEFAULT].licence.policy = working-on-it${state ? " in " + state | ", perhaps"}
 *  dmv.licence.policy = No grace period ${foo}
 *
 *  foo = AT_ANY_TIME_FOR_${state | "UNKNOWN"}!
 *
 *  `
 *
 *  Scala usage:
 *  {{{
 *  def getLicencePolicy(keyProps: KeyedProperties, whichState: Option[String]): Option[String] = {
 *      val subMap: Map[String,Option[String]] = Map( "state" -> whichState )
 *      keyProps.getKeyedProperty(subMap, "dmv.${state}.licence.policy")
 *  }
 *
 *  def testLP(keyProps: KeyedProperties) {
 *
 *    getLicencePolicy(keyProps, Some("CO")) // returns "30-day w/birthday"
 *    getLicencePolicy(keyProps, Some("WY")) // returns "30-day"
 *    getLicencePolicy(keyProps, Some("NE")) // returns "No grace period AT_ANY_TIME_FOR_NE!"
 *    getLicencePolicy(keyProps, None)       // returns "No grace period AT_ANY_TIME_FOR_UNKNOWN!"
 *    getLicencePolicy(keyProps, Some("NY")) // returns "working-on-it in NY"
 *    getLicencePolicy(keyProps, Some("KY")) // returns "working-on-it, perhaps"
 *  }
 *  }}}
 *
 *  TODO: This is the plan anyway
 *
 */
class KeyedProperties(props: JavaProperties, keySubstitution: KeySubstitutionFlavor) {

  // public access to underlying java Properties so that they might be stored externally, etc.
  val javaProperties = props

  def getProperty(key: String): Option[String] = getProperty(key, None)

  def getProperty(key: String, defaultValue: String): Option[String] = getProperty(key, Some(defaultValue))

  def getProperty(key: String, optDefault: Option[String]): Option[String] = optDefault match {
    case Some(defaults) => Option(javaProperties.getProperty(key, defaults))
    case None           => Option(javaProperties.getProperty(key))
  }

  def getKeyedProperty(keyMaps: Map[String,Option[String]], key: String): Option[String] = getKeyedProperty(keyMaps, key, None)

  def getKeyedProperty(keyMaps: Map[String,Option[String]], key: String, defaultValue: String): String =
    getKeyedProperty(keyMaps, key, Some(defaultValue)).get

  // TODO
  def getKeyedProperty(keyMaps: Map[String,Option[String]], key: String, optDefault: Option[String]): Option[String] = {
    ???
  }

}

object KeyedProperties {

  import KeySubstitutionFlavor._

  val DefaultKeySubsitutionFlavor = NormalizeDots

  def apply(props: JavaProperties, keySubstitution: KeySubstitutionFlavor) =
    new KeyedProperties(props, keySubstitution)

  def load(is: java.io.InputStream): KeyedProperties =
    builder().fromInputStream(is).build()

  def load(reader: java.io.Reader): KeyedProperties =
    builder().fromReader(reader).build()

  def load(mappedProps: Map[String,String]): KeyedProperties =
    builder().fromMappedProperties(mappedProps).build()

  def load(is: java.io.InputStream, keyProps: KeyedProperties): KeyedProperties =
    builder().fromInputStream(is).defaults(keyProps.javaProperties).build()

  def load(reader: java.io.Reader, keyProps: KeyedProperties): KeyedProperties =
    builder().fromReader(reader).defaults(keyProps.javaProperties).build()

  def load(mappedProps: Map[String,String], keyProps: KeyedProperties): KeyedProperties =
    builder().fromMappedProperties(mappedProps).defaults(keyProps.javaProperties).build()

  def builder() = new Builder(DefaultKeySubsitutionFlavor, None, None, None, None, None)

  class Builder(
      keySubstitutionFlavor: KeySubstitutionFlavor,
      fromInputStreamOpt: Option[java.io.InputStream],
      fromReaderOpt: Option[java.io.Reader],
      mappedPropsOpt: Option[Map[String,String]],
      defaultJavPropsOpt: Option[JavaProperties],
      inputTypeOpt: Option[InputType])
  {
    def fromInputStream(inputStream: java.io.InputStream): Builder =
      new Builder(keySubstitutionFlavor, Some(inputStream), fromReaderOpt,
        mappedPropsOpt, defaultJavPropsOpt, inputTypeOpt)

    def fromReader(reader: java.io.Reader): Builder =
      new Builder(keySubstitutionFlavor, fromInputStreamOpt, Some(reader),
        mappedPropsOpt, defaultJavPropsOpt, inputTypeOpt)

    def fromMappedProperties(mappedProps: Map[String,String]): Builder =
      new Builder(keySubstitutionFlavor, fromInputStreamOpt, fromReaderOpt,
        Some(mappedProps), defaultJavPropsOpt, inputTypeOpt)

    def keyCollapseDoubleDots(): Builder =
      new Builder(NormalizeDots, fromInputStreamOpt, fromReaderOpt,
        mappedPropsOpt, defaultJavPropsOpt, inputTypeOpt)

    def defaults(javaProps: JavaProperties): Builder =
      new Builder(keySubstitutionFlavor, fromInputStreamOpt, fromReaderOpt,
        mappedPropsOpt, Some(javaProps), inputTypeOpt)

    def asXml(): Builder =
      new Builder(keySubstitutionFlavor, fromInputStreamOpt, fromReaderOpt,
        mappedPropsOpt, defaultJavPropsOpt, Some(InputType.Flat))

    def asFlatfile(): Builder =
      new Builder(keySubstitutionFlavor, fromInputStreamOpt, fromReaderOpt,
        mappedPropsOpt, defaultJavPropsOpt, Some(InputType.Xml))

    def build(): KeyedProperties = {
      val jProps: JavaProperties = defaultJavPropsOpt match {
        case Some(defaults) => new JavaProperties(defaults)
        case None           => new JavaProperties()
      }
      val inputType = inputTypeOpt.getOrElse(InputType.Flat)
      fromInputStreamOpt.map(loadIntoJavaProperties(_, jProps, inputType))
      fromReaderOpt.map(loadIntoJavaProperties(_, jProps, inputType))
      mappedPropsOpt.map(loadIntoJavaProperties(_, jProps))

      new KeyedProperties(jProps, keySubstitutionFlavor)
    }

  }

}

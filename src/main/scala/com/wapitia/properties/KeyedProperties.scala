package com.wapitia.properties

import java.util.{Properties => JavaProperties}
import com.wapitia.common.{Enum,EValue}

/** KeyedProperties extends the Java `Properties` object with wildcards access.
 *
 *  @example
 *
 *  Properties file:
 *  {{{
 *  # Properties file
 *  dmv.CO.licence.policy = 30-day w/birthday
 *  dmv.WY.licence.policy = 30-day
 *  dmv.[NY|NJ|DEFAULT].licence.policy = working-on-it${state ? " in " + state | ", perhaps"}
 *  dmv.licence.policy = No grace period ${foo}
 *
 *  foo = AT_ANY_TIME_FOR_${state | "UNKNOWN"}!
 *  }}}
 *
 *
 *  Scala usage:
 *  {{{
 *
 *  def getLicencePolicy(val keyProps: KeyProperties, whichState: Option[String]): String = {
 *      val subMap = { "state" -> whichState }
 *      keyProps.getKeyedProperty(subMap, "dmv.${state}.licence.policy")
 *  }
 *
 *  def testLP(val keyProps: KeyProperties) {
 *
 *    getLicencePolicy(keyProps, Some["CO"]) // returns "30-day w/birthday"
 *    getLicencePolicy(keyProps, Some["WY"]) // returns "30-day"
 *    getLicencePolicy(keyProps, Some["NE"]) // returns "No grace period AT_ANY_TIME_FOR_NE!"
 *    getLicencePolicy(keyProps, None)       // returns "No grace period AT_ANY_TIME_FOR_UNKNOWN!"
 *    getLicencePolicy(keyProps, Some["NY"]) // returns "working-on-it in NY"
 *    getLicencePolicy(keyProps, Some["KY"]) // returns "working-on-it, perhaps"
 *
 *  }}}
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

  def getKeyedProperty(keyMaps: Map[String,Option[String]], key: String, defaultValue: String): Option[String] =
    getKeyedProperty(keyMaps, key, Some(defaultValue))

  // TODO
  def getKeyedProperty(keyMaps: Map[String,Option[String]], key: String, optDefault: Option[String]): Option[String] = {
    ???
  }

}

sealed trait KeySubstitutionFlavor extends KeySubstitutionFlavor.Value with EValue[KeySubstitutionFlavor]

object KeySubstitutionFlavor extends Enum[KeySubstitutionFlavor] {

  /** If "foo.SUBST.bar" is not a property for some SUBST substitution, look then for "foo.bar". */
  case object CollapseDoubleDots extends KeySubstitutionFlavor

  val enumValues = List(CollapseDoubleDots)
}

object KeyedProperties {

  import KeySubstitutionFlavor._

  val DefaultKeySubsitutionFlavor = CollapseDoubleDots

  def apply(props: JavaProperties, keySubstitution: KeySubstitutionFlavor) = new  KeyedProperties(props, keySubstitution)

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

  def builder() = new Builder(DefaultKeySubsitutionFlavor, None, None, None, None)

  class Builder(
    keySubstitutionFlavor: KeySubstitutionFlavor,
    fromInputStreamOpt: Option[java.io.InputStream],
    fromReaderOpt: Option[java.io.Reader],
    mappedPropsOpt: Option[Map[String,String]],
    defaultJavPropsOpt: Option[JavaProperties])
  {
    def fromInputStream(inputStream: java.io.InputStream): Builder =
      new Builder(keySubstitutionFlavor, Some(inputStream), fromReaderOpt, mappedPropsOpt, defaultJavPropsOpt)

    def fromReader(reader: java.io.Reader): Builder =
      new Builder(keySubstitutionFlavor, fromInputStreamOpt, Some(reader), mappedPropsOpt, defaultJavPropsOpt)

    def fromMappedProperties(mappedProps: Map[String,String]): Builder =
      new Builder(keySubstitutionFlavor, fromInputStreamOpt, fromReaderOpt, Some(mappedProps), defaultJavPropsOpt)

    def keyCollapseDoubleDots(): Builder =
      new Builder(CollapseDoubleDots, fromInputStreamOpt, fromReaderOpt, mappedPropsOpt, defaultJavPropsOpt)

    def defaults(javaProps: JavaProperties): Builder =
      new Builder(keySubstitutionFlavor, fromInputStreamOpt, fromReaderOpt, mappedPropsOpt, Some(javaProps))

    def build(): KeyedProperties = {
      val jProps: JavaProperties = defaultJavPropsOpt match {
        case Some(defaults) => new JavaProperties(defaults)
        case None           => new JavaProperties()
      }
      fromInputStreamOpt.map(loadIntoJavaProperties(_, jProps))
      fromReaderOpt.map(loadIntoJavaProperties(_, jProps))
      mappedPropsOpt.map(loadIntoJavaProperties(_, jProps))

      new KeyedProperties(jProps, keySubstitutionFlavor)
    }

  }

}

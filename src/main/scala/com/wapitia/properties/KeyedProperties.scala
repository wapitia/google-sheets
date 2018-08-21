package com.wapitia
package properties

import java.util.{Properties => JavaProperties}
import com.wapitia.common.{Enum,EValue}

/** KeyedProperties wraps a Java `Properties` object with pattern substitution
 *  availability.
 *
 *  The property keys may be normal keys in which the value is obtained in a regular way, or the key
 *  may contain group patterns of the form: `pre.[kopt1|kopt2|koptn].post = ...` which matches
 *  both `pre.kopt1.post`, `pre.kopt2.post` and `pre.koptn.post`.
 *
 *  The property values may be normal values in which the value is returned as usual, or the value
 *  may contain substitution patterns of the form: `Unchanged text ${some.other.key} more unchanged`
 *  which will substitute the value from this property's some.other.key value for that text.
 *
 *  @example
 *  Properties file contains:
 *  `
 *  ...
 *  key1 = John
 *  key2 = Hello ${key1}!
 *  ...
 *  `
 *
 *  Code contains:
 *  {{{
 *    val keyProps: KeyedProperties = KeyProperties.load(...)
 *    ...
 *    keyProps.getKeyedProperty("dmv.${state}.licence.policy")
 *    ...
 *  }}}
 *
 *
 *
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
 *      val params: KeyedProperties.Params = Map( "state" -> whichState )
 *      keyProps.getKeyedProperty(params, "dmv.${state}.licence.policy")
 *  }
 *
 *  def testLP(in: java.io.InputStream) {
 *      val keyProps: KeyedProperties = KeyedProperties.load(in)
 *      val testMap = Map(
 *        "30-day w/birthday"                        -> getLicencePolicy(keyProps, Some("CO")),
 *        "30-day"                                   -> getLicencePolicy(keyProps, Some("WY")),
 *        "No grace period AT_ANY_TIME_FOR_NE!"      -> getLicencePolicy(keyProps, Some("NE")),
 *        "No grace period AT_ANY_TIME_FOR_UNKNOWN!" -> getLicencePolicy(keyProps, None),
 *        "working-on-it in NY"                      -> getLicencePolicy(keyProps, Some("NY")),
 *        "working-on-it, perhaps"                   -> getLicencePolicy(keyProps, Some("KY"))
 *      )
 *      testMap.foreach { case (exp, act) =>  assertEquals(exp, act) }
 *  }
 *  }}}
 *
 *  TODO: This is the plan anyway
 *
 */
class KeyedProperties(props: JavaProperties, keySubstitution: KeySubstitutionFlavor) {

  import KeyedProperties._

//  type KeySequence = List[String]

  // public access to wrapped java Properties so that client might store them externally, etc.
  val javaProperties = props

  def getProperty(key: String): Option[String] = getProperty(key, None)

  def getProperty(key: String, defaultValue: String): Option[String] = getProperty(key, Some(defaultValue))

  def getProperty(key: String, optDefault: Option[String]): Option[String] = optDefault match {
    case Some(defaults) => Option(props.getProperty(key, defaults))
    case None           => Option(props.getProperty(key))
  }

  def getKeyedProperty(key: String): Option[String] = getKeyedProperty(NoParams, key, None)

  def getKeyedProperty(params: Params, key: String): Option[String] = getKeyedProperty(params, key, None)

  def getKeyedProperty(key: String, defaultValue: String): String =
    getKeyedProperty(NoParams, key, Some(defaultValue)).get

  def getKeyedProperty(params: Params, key: String, defaultValue: String): String =
    getKeyedProperty(params, key, Some(defaultValue)).get

  def getKeyedProperty(params: Params, key: String, optDefault: Option[String]): Option[String] = {
    val resolvedKey: String = parse(key, params)
    val rawValue: Option[String] = getValue(resolvedKey, params, props)
    val rawOrDefault: Option[String] = rawValue.orElse(optDefault)
    rawOrDefault.map(parse(_, params))
  }

  def parse(rawVal: String, params: Params): String = new PatternParser(params, props, DefaultBadLookupFunc).parse(rawVal)

}

object KeyedProperties {

  import KeySubstitutionFlavor._

  type Params = Map[String,Option[String]]

  val NoParams = Map.empty.asInstanceOf[Params]
  val DefaultKeySubsitutionFlavor = NormalizeDots

  val dollar = '$'
  val opencurly = '{'
  val closecurly = '}'

  def lookupFormat(lu: String): String = "" + dollar + opencurly + lu + closecurly

  def DefaultBadLookupFunc(lu: String): String = lookupFormat(lu + "_NOT_FOUND")

  def apply(props: JavaProperties) =
    new KeyedProperties(props, DefaultKeySubsitutionFlavor)

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

  /** Get the value given the resolved key, first from params, then from props. `None` when not found */
  def getValue(key: String, params: Params, props: JavaProperties): Option[String] = {
    // either branch may return `None`
    if (params.contains(key))
      // `params.get(key)` isn't `None`, but returns `Some(Option[String])`
      params.get(key).get
    else
      // `getProperty` will return `null` if not found; `Option` wraps `null` as `None`
      Option(props.getProperty(key))
  }

  class PatternParser(params: Params, props: JavaProperties, badLookupFunc: String => String) {

    def parse(s: String): String = praw(s)

    def resolve(str: String): String =
      getValue(str, params, props).getOrElse(badLookupFunc(str))

    private def praw(s: String): String = s.length match {
      case 0 => ""
      case _ => (s.head, s.tail) match {
        case (ch,t) if ch == dollar => pdol(t)
        case (ch,t) => "" + ch + praw(t)
      }
    }

    // previous char was the format entry char '$'
    private def pdol(s: String): String = s.length match {
      case 0 => "" + dollar  // doesn't match '${' treat '$' as raw character
      case _ => (s.head, s.tail) match {
        case (ch,t) if ch == dollar => "" + dollar + parse(t) // matches escape sequence '$$', so push and back to state 0
        case (ch,t) if ch == opencurly => pcurly(t, "")
        case (ch,t) => "" + dollar + ch + praw(t) // doesn't match '${' treat '$' as raw character
      }
    }

    // previous two chars were "${"
    private def pcurly(s: String, pataccum: String): String = s.length match {
      case 0 => "" + dollar + opencurly + pataccum // ran off end so treat '${' as raw characters
      case _ => (s.head, s.tail) match {
        // end found: pataccum is resolved substitution, so substitute it
        case (ch,t) if ch == dollar => pcurly(pdol(t), pataccum)
        case (ch,t) if ch == opencurly => rawuntilclosecurly(t, pataccum + opencurly)
        case (ch,t) if ch == closecurly => praw(resolve(pataccum)) + praw(t)
        case (ch,t) => pcurly(t, pataccum + ch)
      }
    }

    private def rawuntilclosecurly(s: String, pataccum: String): String = s.length match {
      case 0 => "" + dollar + opencurly + pataccum // ran off end so treat '${' seq as raw characters
      case _ => (s.head, s.tail) match {
        // end found: pataccum is resolved substitution, so substitute it
        case (ch,t) if ch == closecurly => praw(resolve(pataccum)) + praw(t)
        case (ch,t) => rawuntilclosecurly(t, pataccum + ch)
      }
    }

  }

}

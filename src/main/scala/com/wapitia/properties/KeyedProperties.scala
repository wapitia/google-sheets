package com.wapitia
package properties

import java.util.{Properties => JavaProperties}

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
class KeyedProperties(props: JavaProperties, makePatternExpander: KeyedProperties.PatternExpanderMaker) {

  /** public access to wrapped java Properties so that client might store them externally, etc. */
  val javaProperties = props

  def getProperty(key: String): Option[String] = getProperty(key, None)

  def getProperty(key: String, defaultValue: String): Option[String] = getProperty(key, Some(defaultValue))

  def getProperty(key: String, optDefault: Option[String]): Option[String] = optDefault match {
    case Some(defaults) => Option(props.getProperty(key, defaults))
    case None           => Option(props.getProperty(key))
  }

  def getKeyedProperty(key: String): Option[String] =
    getKeyedProperty(KeyedProperties.NoParams, key, None)

  def getKeyedProperty(key: String, defaultValue: String): String =
    getKeyedProperty(KeyedProperties.NoParams, key, Some(defaultValue)).get

  def getKeyedProperty(params: KeyedPropertiesParams, key: String): Option[String] =
    getKeyedProperty(params, key, None)

  def getKeyedProperty(params: KeyedPropertiesParams, key: String, defaultValue: String): String =
    getKeyedProperty(params, key, Some(defaultValue)).get

  def getKeyedProperty(params: KeyedPropertiesParams, key: String, optDefault: Option[String]): Option[String] =
    makePatternExpander(params, props).getKeyedProperty(key, optDefault)
}

object KeyedProperties {

  /** Producer of a `PatternExpander` when given `KeyedPropertiesParams`, and `JavaProperties`.
   *  The produced `PatternExpander` is configured with an implicit `PatternEvaluator`.
   */
  type PatternExpanderMaker = (KeyedPropertiesParams, JavaProperties) => PatternExpander

  val NoParams = Map.empty.asInstanceOf[KeyedPropertiesParams]

  def DefaultPatternEvaluator = PatternEvaluator.Default

  val DefaultPatternExpanderMaker: PatternExpanderMaker =
    (params: KeyedPropertiesParams, props: JavaProperties) => PatternExpander.default(params, props, DefaultPatternEvaluator)

  def apply(props: JavaProperties) =
    new KeyedProperties(props, DefaultPatternExpanderMaker)

  def load(is: java.io.InputStream): KeyedProperties =
    loader().fromInputStream(is).build()

  def load(reader: java.io.Reader): KeyedProperties =
    loader().fromReader(reader).build()

  def load(mappedProps: Map[String,String]): KeyedProperties =
    loader().fromMappedProperties(mappedProps).build()

  def load(is: java.io.InputStream, keyProps: KeyedProperties): KeyedProperties =
    loader().fromInputStream(is).parentJavaProperties(keyProps.javaProperties).build()

  def load(reader: java.io.Reader, keyProps: KeyedProperties): KeyedProperties =
    loader().fromReader(reader).parentJavaProperties(keyProps.javaProperties).build()

  def load(mappedProps: Map[String,String], keyProps: KeyedProperties): KeyedProperties =
    loader().fromMappedProperties(mappedProps).parentJavaProperties(keyProps.javaProperties).build()

  def loader() = new LoaderBuilder(None, None, None, None, None, None, None)

  class LoaderBuilder(
      fromInputStreamOpt: Option[java.io.InputStream],
      fromReaderOpt: Option[java.io.Reader],
      mappedPropsOpt: Option[Map[String,String]],
      defaultJavPropsOpt: Option[JavaProperties],
      inputTypeOpt: Option[InputType],
      patternEvaluatorOpt: Option[PatternEvaluator],
      patternExpanderOpt: Option[PatternExpanderMaker])
  {
    /** Provide the input stream from which to load Java Properties.
     *  This should be called only once, as any previous input stream will be superceded by this new one.
     */
    def fromInputStream(inputStream: java.io.InputStream): LoaderBuilder =
      new LoaderBuilder(Some(inputStream), fromReaderOpt, mappedPropsOpt, defaultJavPropsOpt, inputTypeOpt,
        patternEvaluatorOpt, patternExpanderOpt)

    /** Provide the Reader from which to load Java Properties.
     *  This should be called only once, as any previous reader will be superceded by this new one.
     */
    def fromReader(reader: java.io.Reader): LoaderBuilder =
      new LoaderBuilder(fromInputStreamOpt, Some(reader), mappedPropsOpt, defaultJavPropsOpt, inputTypeOpt,
        patternEvaluatorOpt, patternExpanderOpt)

    /** Provide a map of property keys to values to be loaded into a new Java Properties instance. */
    def fromMappedProperties(mappedProps: Map[String,String]): LoaderBuilder =
      new LoaderBuilder(fromInputStreamOpt, fromReaderOpt, Some(mappedProps), defaultJavPropsOpt, inputTypeOpt,
        patternEvaluatorOpt, patternExpanderOpt)

    /** Provide the default Java Properties instance which will be wrapped by a newly created JavaProperties */
    def parentJavaProperties(javaProps: JavaProperties): LoaderBuilder =
      new LoaderBuilder(fromInputStreamOpt, fromReaderOpt, mappedPropsOpt, Some(javaProps), inputTypeOpt,
        patternEvaluatorOpt, patternExpanderOpt)

    /** Load from Input Stream or reader as a flat file (`InputType.Flat`), as opposed to an XML file. */
    def asFlatfile(): LoaderBuilder =
      new LoaderBuilder(fromInputStreamOpt, fromReaderOpt, mappedPropsOpt, defaultJavPropsOpt, Some(InputType.Flat),
        patternEvaluatorOpt, patternExpanderOpt)

    /** Load from the input stream or reader as an XML file  (`InputType.Xml`), as opposed to a flat file. */
    def asXml(): LoaderBuilder =
      new LoaderBuilder(fromInputStreamOpt, fromReaderOpt, mappedPropsOpt, defaultJavPropsOpt, Some(InputType.Xml),
        patternEvaluatorOpt, patternExpanderOpt)

    /** Provide a `PatternEvaluator` instance. If `patternExpanderMaker` is also defined, `patternEvaluator` will ignored. */
    def patternEvalulator(patternEvaluator: PatternEvaluator) : LoaderBuilder =
      new LoaderBuilder(fromInputStreamOpt, fromReaderOpt, mappedPropsOpt, defaultJavPropsOpt, inputTypeOpt,
        Some(patternEvaluator), patternExpanderOpt)

    /** Provide a `PatternExpanderMaker` instance. The PatternExpanderMaker supercedes the PatternEvaluator,
     *  so that if both are defined, `patternEvaluator` will ignored since The PatternExpander carries
     *  its own PatternEvaluator.
     */
    def patternExpanderMaker(patternExpanderMaker: PatternExpanderMaker) : LoaderBuilder =
      new LoaderBuilder(fromInputStreamOpt, fromReaderOpt, mappedPropsOpt, defaultJavPropsOpt, inputTypeOpt,
        patternEvaluatorOpt, Some(patternExpanderMaker))

    /** Load and build JavaProperties, configure and return a new `KeyedProperties` instance wrapping the newly
     *  built JavaProperties instance, giving a new pattern expander according to the configuration.
     *  When provided, the newly created JavaProperties' keys will be loaded and superceded with the defaults,
     *  input stream, reader, and mapped properties in that order.
     */
    def build(): KeyedProperties = {

      val jProps: JavaProperties = defaultJavPropsOpt match {
        case Some(defaults) => new JavaProperties(defaults)
        case None           => new JavaProperties()
      }
      val inputType = inputTypeOpt.getOrElse(InputType.Flat)
      fromInputStreamOpt.map(loadIntoJavaProperties(_, jProps, inputType))
      fromReaderOpt.map(loadIntoJavaProperties(_, jProps, inputType))
      mappedPropsOpt.map(loadIntoJavaProperties(_, jProps))
      val patternEvaluator: PatternEvaluator = patternEvaluatorOpt.getOrElse(DefaultPatternEvaluator)

      val patternExpanderMaker: PatternExpanderMaker = patternExpanderOpt match {
        case Some(pemaker) => pemaker
        case None          => (params: KeyedPropertiesParams, props: JavaProperties) =>
                               PatternExpander.default(params, props, patternEvaluator)
      }

      new KeyedProperties(jProps, patternExpanderMaker)
    }

  }

}

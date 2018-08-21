package com.wapitia
package properties

import java.util.{Properties => JavaProperties}

trait PatternEvaluator {

  /** Get the value given the resolved key, first from params, then from props. `None` when not found */
  def getValue(key: String, params: KeyedPropertiesParams, props: JavaProperties): Option[String] = {
    // either branch may return `None`
    if (params.contains(key))
      // `params.get(key)` isn't `None`, but returns `Some(Option[String])`
      params.get(key).get
    else
      // `getProperty` will return `null` if not found; `Option` wraps `null` as `None`
      Option(props.getProperty(key))
  }

  def resolve(str: String, params: KeyedPropertiesParams, props: JavaProperties): String

}

object PatternEvaluator {

  type BadLookupFunc = String => String

  def apply(badlookup: BadLookupFunc): PatternEvaluator =
    new SimplePatternEvaluator(badlookup)

  def DefaultBadLookupFunc(lu: String): String = lu + "_NOT_FOUND"

  def Default = apply(DefaultBadLookupFunc)

  class SimplePatternEvaluator(badLookupFunc: PatternEvaluator.BadLookupFunc) extends PatternEvaluator {

    def resolve(str: String, params: KeyedPropertiesParams, props: JavaProperties): String =
      getValue(str, params, props).getOrElse(badLookupFunc(str))

  }
}

package com.wapitia
package properties

import java.util.{Properties => JavaProperties}
import properties.{KeyedPropertiesParams => KeyedParams}
import properties.{OptionalPropertyValue => OptVal}

trait PatternEvaluator {

  /** Get the value given the resolved key, first from params, then from props. `None` when not found.
   *  key must be non-null and should be non-blank.
   */
  def getValue(key: String, params: KeyedParams, props: JavaProperties): OptVal = {
    val trimedKey = key.trim

    // either branch may return `None`
    if (params.contains(trimedKey))
      // `params.get(key)` isn't `None`, but returns `Some(OptVal)`
      params.get(trimedKey).get
    else
      // `getProperty` will return `null` if not found; `Option` wraps `null` as `None`
      Option(props.getProperty(trimedKey))
  }

  def resolve(str: String, params: KeyedParams, props: JavaProperties): String

}

class SimplePatternEvaluator(badLookupFunc: PatternEvaluator.BadLookupFunc) extends PatternEvaluator {

  override def resolve(key: String, params: KeyedParams, props: JavaProperties): String =
    getValue(key, params, props).getOrElse(badLookupFunc(key))

}

object PatternEvaluator {

  type BadLookupFunc = String => String

  def apply(badlookup: BadLookupFunc): PatternEvaluator =
    new SimplePatternEvaluator(badlookup)

  def DefaultBadLookupFunc(key: String): String = key + "_NOT_FOUND"

  def Default = apply(DefaultBadLookupFunc)
}

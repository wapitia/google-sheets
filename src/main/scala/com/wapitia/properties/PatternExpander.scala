package com.wapitia
package properties

import java.util.{Properties => JavaProperties}

trait PatternExpander {

  /** Parse and resolve the given string. Expanding and resolving each encountered pattern. */
  def parse(str: String): String

  /** Resolve the keyed property by the given name, or else return the optional default value.
   *  @return the resolved key's value if found, or the optDefault if it is Something, or else `None`.
   */
  def getKeyedProperty(key: String, optDefault: Option[String]): Option[String]

}

/** Expands embedded string patterns of the form `${param.key}`.
 *  The `${...}` pattern is escaped by the inclusion of a preceding dollar sign, as in `$${don't change}` which
 *  will expand to `${don't change}`.
 *  The pattern is recursive so that this expander attempts to expand the pattern `${key.frag.${embedded-part}}`,
 *  and here care must be made so that infinite recursion doesn't happen.
 *  Does not expand the pattern `{ whatever }` if not preceded by a dollar sign. This is either a bug or a feature.
 *  When a pattern is expanded, its contents are then expanded too, recursively, so that an infinite
 *  loop may occur here as well.
 */
class SimplePatternExpander(params: KeyedPropertiesParams, props: JavaProperties, eval: PatternEvaluator) extends PatternExpander {

  val dollar = '$'
  val opencurly = '{'
  val closecurly = '}'

  override def getKeyedProperty(key: String, optDefault: Option[String]): Option[String] = {
    val resolvedKey: String = parse(key)
    val rawValue: Option[String] = eval.getValue(resolvedKey, params, props)
    val rawOrDefault: Option[String] = rawValue.orElse(optDefault)
    rawOrDefault.map(parse(_))
  }

  override def parse(s: String): String = praw(s)

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
      case (ch,t) if ch == dollar => "" + dollar + praw(t) // matches escape sequence '$$', so push and back to state 0
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
      case (ch,t) if ch == closecurly => praw(eval.resolve(pataccum, params, props)) + praw(t)
      case (ch,t) => pcurly(t, pataccum + ch)
    }
  }

  private def rawuntilclosecurly(s: String, pataccum: String): String = s.length match {
    case 0 => "" + dollar + opencurly + pataccum // ran off end so treat '${' seq as raw characters
    case _ => (s.head, s.tail) match {
      // end found: pataccum is resolved substitution, so substitute it
      case (ch,t) if ch == closecurly => praw(eval.resolve(pataccum, params, props)) + praw(t)
      case (ch,t) => rawuntilclosecurly(t, pataccum + ch)
    }
  }

}

object PatternExpander {

  def default(params: KeyedPropertiesParams, props: JavaProperties, eval: PatternEvaluator): PatternExpander =
    new SimplePatternExpander(params, props, eval)

}

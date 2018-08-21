package com.wapitia
package properties

import java.util.{Properties => JavaProperties}

trait PatternExpander {

  def getValue(key: String): Option[String]

  def parse(s: String): String
}

object PatternExpander {

  def default(params: KeyedPropertiesParams, props: JavaProperties, eval: PatternEvaluator): PatternExpander =
    new SimplePatternExpander(params, props, eval)

  /** Expands embedded string patterns of the form `${param.key}`.
   *  The `${...}` pattern is escaped by the inclusion of a preceding dollar sign, as in `$${don't change}` which
   *  will expand to `${don't change}`.
   *  The pattern is recursive so that this expander attempts to expand the pattern `${key.frag.${embedded-part}}`,
   *  and here care must be made so that infinite recursion doesn't happen.
   */
  class SimplePatternExpander(params: KeyedPropertiesParams, props: JavaProperties, eval: PatternEvaluator) extends PatternExpander {

    val dollar = '$'
    val opencurly = '{'
    val closecurly = '}'

    override def getValue(key: String): Option[String] =
      eval.getValue(key, params, props)

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

}

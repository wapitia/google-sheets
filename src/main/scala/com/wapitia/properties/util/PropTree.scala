package com.wapitia
package properties

import java.util.{Properties => JavaProperties}

/** wapitia properties utilities collection.
 *  Given a properties collection having string key-value entries of the form:
 *
 *  `
 *  dmv.CO.licence.policy = 30-day w/birthday
 *  dmv.WY.licence.policy = 30-day
 *  dmv.[NY|NJ|DEFAULT].licence.[policy|law] = working-on-it${state ? " in " + state | ", perhaps"}?
 *  dmv.licence.policy = No grace period ${foo}
 *
 *  foo = bar
 *  `
 *  This will create a hierarchy of the form:
 *
 *  `
 *  PropTree: {
 *    "dmv": {
 *       "CO": { "licence": { "policy": Cat("30-day w/birthday") } }
 *       "WY": { "licence": { "policy": Cat("30-day") } }
 *       "NY": { "licence": {
 *         "policy": Cat("working-on-it", R("state", C(" in ", Ref("state", Cat(" in ", Self))), ", perhaps"), "?"), (1)
 *         "law":             -> (1)
 *       } }
 *       "NJ": { "licence": {
 *         "policy":          -> (1)
 *         "law":             -> (1)
 *       } }
 *       "DEFAULT": { "licence": {
 *         "policy":          -> (1)
 *         "law":             -> (1)
 *       } }
 *       "licence": { "policy": Cat("No grace period ", Ref(foo)) }
 *    }
 *    "foo": Cat("bar")
 *  }
 *  `
 *  Notes:
 *  o  "dmv.NY.licence.law", "dmv.NJ.licence.policy", "dmv.NJ.licence.law", "dmv.DEFAULT.licence.policy",
 *     "dmv.DEFAULT.licence.law", each point to the same shared structure that is referred to by
 *     "dmv.NY.licence.policy". The outer product of the permutations are represented here.
 *  o  In the example above, we see that the input char sequence within the "${"..."}"
 *     follow a java-like expression construct where quoted elements are strings and the rest are operations
 *     and variable references.
 *     Thus, the "state" in " + state" doesn't require dereferencing such as "$state"
 *
 */
object PropTree {

  val keyFrameSep = '.'

  trait Cat

  /** read all of the string property names from the java properties
   *  table, creating an hierarchical tree of those properties and their values
   *  through its dot "." separators.
   */
  def parse(javaProps: JavaProperties): PropTree = {
    val propKeys = javaProps.stringPropertyNames()
    ???
  }

}

class PropTree {

}

package com.wapitia
package properties

import java.util.{Properties => JavaProperties}

/** wapitia properties utilities collection.
 *
 */
package object util {

  /** read all of the string property names from the java properties
   *  table, creating an hierarchical tree of those properties and their values
   *  through its dot "." separators.
   */
  def asPropTree(javaProps: JavaProperties) = PropTree.parse(javaProps)

}

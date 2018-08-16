package com.wapitia
package properties

import com.wapitia.common.{Enum,EValue}

sealed trait KeyPatternFormat extends KeyPatternFormat.Value with EValue[KeyPatternFormat]

object KeyPatternFormat extends Enum[KeyPatternFormat] {

  /** Look for Substitution key patterns like "${my.key} */
  case object DollarCurlies extends KeyPatternFormat

  val enumValues = List(DollarCurlies)
}

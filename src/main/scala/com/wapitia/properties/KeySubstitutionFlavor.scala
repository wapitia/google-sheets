package com.wapitia
package properties

import com.wapitia.common.{Enum,EValue}

sealed trait KeySubstitutionFlavor extends KeySubstitutionFlavor.Value with EValue[KeySubstitutionFlavor]

object KeySubstitutionFlavor extends Enum[KeySubstitutionFlavor] {

  /** If "foo.SUBST.bar" is not a property for some SUBST substitution, look then for "foo.bar". */
  case object NormalizeDots extends KeySubstitutionFlavor

  val enumValues = List(NormalizeDots)
}


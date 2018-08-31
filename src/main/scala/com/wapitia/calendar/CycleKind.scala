package com.wapitia
package calendar

import com.wapitia.common.{Enum,EValue}

/** Kinds of schedule cycles, falls into cycles either incremented daily or monthly
 *
 *  {{{
 *    enum CycleKind {
 *      case DayLike
 *      case MonthLike
 *    }
 *  }}}
 */
sealed trait CycleKind extends CycleKind.Value with EValue[CycleKind]

object CycleKind extends Enum[CycleKind] {
  case object DayLike extends CycleKind
  case object MonthLike extends CycleKind

  val enumValues = List(DayLike, MonthLike)
}

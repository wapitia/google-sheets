package com.wapitia
package calendar

import com.wapitia.common.{Enum,EValue}
import CycleKind._

/** Enumeration of supported periodic schedule cycles.
 *  DayLike vs MonthLike refers to how a given cycle is incremented,
 *  either as a multiple of days or as a multiple of months,
 *  backed by `DailySchedule` or `MonthlySchedule` respectively.
 *  The increment algorithms are different between the two.
 *
 *  {{{
 *    enum Cycle(kind: CycleKind, cycleSize: Int) {
 *      case Daily extends Cycle(DayLike, 1)
 *      case BiDaily extends Cycle(DayLike, 2)
 *      case Weekly extends Cycle(DayLike, 7)
 *      case BiWeekly extends Cycle(DayLike, 14)
 *      case Monthly extends Cycle(MonthLike, 1)
 *      case BiMonthly extends Cycle(MonthLike, 2)
 *      case Quarterly extends Cycle(MonthLike, 3)
 *      case SemiAnnually extends Cycle(MonthLike, 6)
 *      case Annually extends Cycle(MonthLike, 12)
 *
 *      def asDays: Double = ...
 *      def asMonths: Double = ...
 *    }
 *  }}}
 *
 *  "SemiMonthly" is not supported as a unique cycle. The user instead must
 *  use the Monthly cycle having multiple schedule days in the cycle.
 */
sealed trait Cycle extends Cycle.Value with EValue[Cycle] {
  val kind: CycleKind
  val cycleSize: Int

  /** The number of days in the cycle.
   *  When this is a DayLike cycle (Daily, Weekly, etc), then the
   *  result is an integer number of days.
   *  When this is a MonthLike cycle (Monthly, Quarterly, Annually, etc),
   *  the number of days is not an integer, but is a multiple of the
   *  average days per month, 30.436875
   */
  def asDays: Double = kind match {
    case DayLike => cycleSize.toDouble
    case MonthLike => cycleSize.toDouble * DaysPerMonth
  }

  /** The number of months in the cycle.
   *  When this is a DayLike cycle (Daily, Weekly, etc), then the
   *  result is not an integer but is a multiple of the
   *  months per day, 0.03285...
   *
   *  When this is a MonthLike cycle (Monthly, Quarterly, Annually, etc),
   *  the number of is an integer multiple of the number of months
   *  in the cycle.
   */
  def asMonths: Double = kind match {
    case DayLike => cycleSize.toDouble / DaysPerMonth
    case MonthLike => cycleSize.toDouble
  }
}

object Cycle extends Enum[Cycle] {

  case object Daily extends Cycle{ val kind = DayLike; val cycleSize = 1 }
  case object BiDaily extends Cycle{ val kind = DayLike; val cycleSize = 2 }
  case object Weekly extends Cycle{ val kind = DayLike; val cycleSize = 7 }
  case object BiWeekly extends Cycle{ val kind = DayLike; val cycleSize = 14 }
  case object Monthly extends Cycle{ val kind = MonthLike; val cycleSize = 1 }
  case object BiMonthly extends Cycle{ val kind = MonthLike; val cycleSize = 2 }
  case object Quarterly extends Cycle{ val kind = MonthLike; val cycleSize = 3 }
  case object SemiAnnually extends Cycle{ val kind = MonthLike; val cycleSize = 6 }
  case object Annually extends Cycle{ val kind = MonthLike; val cycleSize = 12 }

  val enumValues = List(Daily, BiDaily, Weekly, BiWeekly, Monthly, BiMonthly, Quarterly, SemiAnnually, Annually)
}

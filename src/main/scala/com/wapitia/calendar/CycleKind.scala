package com.wapitia
package calendar

import com.wapitia.common.{Enum,EValue}

/** Enumeration of common canonical periodic schedule cycles,
 *  though ad-hoc cycle durations are possible in the Wapitia calendar system.
 *
 *  {{{
 *    enum Cycle(daysInCycle: Int, monthsInCycle: Int) {
 *      case Daily extends Cycle(1, 0)
 *      case BiDaily extends Cycle(2, 0)
 *      case Weekly extends Cycle(7, 0)
 *      case BiWeekly extends Cycle(14, 0)
 *      case Monthly extends Cycle(0, 1)
 *      case BiMonthly extends Cycle(0, 2)
 *      case Quarterly extends Cycle(0, 3)
 *      case SemiAnnually extends Cycle(0, 6)
 *      case Annually extends Cycle(0, 12)
 *
 *      def asDays: Double = ...
 *      def asMonths: Double = ...
 *    }
 *  }}}
 */
sealed trait CycleKind extends CycleKind.Value with EValue[CycleKind] {
  val daysInCycle: Int
  val monthsInCycle: Int

  val asDays: Double = monthsInCycle.asInstanceOf[Double] * DaysPerMonth + daysInCycle.asInstanceOf[Double]
  val asMonths: Double = monthsInCycle.asInstanceOf[Double] + daysInCycle.asInstanceOf[Double] / DaysPerMonth
}

object CycleKind extends Enum[CycleKind] {
  case object Daily extends CycleKind{ val daysInCycle = 1; val monthsInCycle = 0 }
  case object BiDaily extends CycleKind{ val daysInCycle = 2; val monthsInCycle = 0 }
  case object Weekly extends CycleKind{ val daysInCycle = 7; val monthsInCycle = 0 }
  case object BiWeekly extends CycleKind{ val daysInCycle = 14; val monthsInCycle = 0 }
  case object Monthly extends CycleKind{ val daysInCycle = 0; val monthsInCycle = 1 }
  case object BiMonthly extends CycleKind{ val daysInCycle = 0; val monthsInCycle = 2 }
  case object Quarterly extends CycleKind{ val daysInCycle = 0; val monthsInCycle = 3 }
  case object SemiAnnually extends CycleKind{ val daysInCycle = 0; val monthsInCycle = 6 }
  case object Annually extends CycleKind{ val daysInCycle = 0; val monthsInCycle = 12 }

  val enumValues = List(Daily, BiDaily, Weekly, BiWeekly, Monthly, BiMonthly, Quarterly, SemiAnnually, Annually)
}

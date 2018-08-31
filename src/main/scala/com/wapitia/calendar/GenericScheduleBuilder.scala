package com.wapitia
package calendar

import java.time.LocalDate
import scala.collection.BitSet

trait GenericScheduleBuilder {

  /** Reset the size of this schedule, in number of days or number of months depending on its cycle configuration.
   *  Note that GenericScheduleBuilders are made with a valid cycle size already, so this function should not
   *  normally be used.
   */
  def offset(offs: Int): GenericScheduleBuilder

  def build(): Schedule
}

/** Build a schedule of any kind using a Generic overlay on the concrete schedule types. */
// TODO
object ScheduleFactory {

  import CycleKind._

  def builder(cycleKind: Cycle): GenericScheduleBuilder = cycleKind.kind match {
    case DayLike => new GenericDailyScheduleBuilder(cycleKind, 0, None)
    case MonthLike => new GenericMonthlyScheduleBuilder(cycleKind)
  }

  class GenericDailyScheduleBuilder(ck: Cycle, offset: Int, validfCycleSheduleDayMapOpt: Option[LocalDate => BitSet]) extends
      DailySchedule.Builder[Schedule](DailyCycle(ck.cycleSize, offset), validfCycleSheduleDayMapOpt) with GenericScheduleBuilder
  { sup: DailySchedule.Builder[Schedule] =>

    override def offset(offs: Int): GenericScheduleBuilder = {
      new GenericDailyScheduleBuilder(ck, offs, validfCycleSheduleDayMapOpt)
    }

    override def build(): Schedule = sup.build()
  }

  class GenericMonthlyScheduleBuilder(ck: Cycle) extends
      MonthlySchedule.Builder[Schedule](None, None, MonthlyCycle.builder().cycleSize(ck.cycleSize).offset(0)) with GenericScheduleBuilder
  { sup: MonthlySchedule.Builder[Schedule] =>
    override def offset(offs: Int): GenericScheduleBuilder =
      ???

    override def build(): Schedule = sup.build()
  }

}

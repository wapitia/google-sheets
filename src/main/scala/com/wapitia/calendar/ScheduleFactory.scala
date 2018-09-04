package com.wapitia
package calendar

import java.time.LocalDate
import scala.collection.BitSet

/** Build a schedule of any kind using a Generic overlay on the concrete schedule types. */
// TODO WIP
object ScheduleFactory {

  import CycleKind._

  def builder(cycle: Cycle): GenericScheduleBuilder = cycle.kind match {
    case DayLike => cycleDays(cycle.cycleSize)
    case MonthLike => cycleMonths(cycle.cycleSize)
  }

  def cycleDays(nDays: Int): GenericDailyScheduleBuilder = new GenericDailyScheduleBuilder(nDays, 0, None)

  def cycleWeeks(nWeeks: Int): GenericDailyScheduleBuilder = cycleDays(nWeeks * DaysPerWeek)

  def cycleMonths(nMonths: Int): GenericMonthlyScheduleBuilder = new GenericMonthlyScheduleBuilder(nMonths, 0, None, None)

}

class GenericDailyScheduleBuilder(cycleSize: Int, offset: Int, validfCycleSheduleDayMapOpt: Option[LocalDate => BitSet])
  extends DailyScheduleBuilder[Schedule](DailyCycle(cycleSize, offset), validfCycleSheduleDayMapOpt)
  with GenericScheduleBuilder
{

  override def offset(offs: Int): GenericScheduleBuilder = {
    new GenericDailyScheduleBuilder(cycleSize, offs, validfCycleSheduleDayMapOpt)
  }

  override def build(): Schedule = super[DailyScheduleBuilder].build()
}

class GenericMonthlyScheduleBuilder(cycleSize: Int, offset: Int, dayfuncOpt: Option[ScheduleDayOfMonth],
    workingSchedOpt: Option[WorkingSchedule])
  extends MonthlyScheduleBuilder[Schedule](dayfuncOpt, workingSchedOpt, MonthlyCycle.builder().cycleSize(cycleSize).offset(offset))
  with GenericScheduleBuilder
{

  override def offset(offs: Int): GenericScheduleBuilder =
    new GenericMonthlyScheduleBuilder(cycleSize, offs, dayfuncOpt, workingSchedOpt)

  override def build(): Schedule = super[MonthlyScheduleBuilder].build()
}

package com.wapitia
package calendar

import java.time.LocalDate
import scala.collection.BitSet

trait GenericScheduleBuilder {

  /** offset in days from normal cycle anchor date
   */
  def offset(offs: Int): GenericScheduleBuilder

  def build(): Schedule
}

/** Build a schedule of any kind using a Generic overlay on the concrete schedule types. */
// TODO WIP
object ScheduleFactory {

  import CycleKind._

  def builder(cycleKind: Cycle): GenericScheduleBuilder = cycleKind.kind match {
    case DayLike => new GenericDailyScheduleBuilder(cycleKind, 0, None)
    case MonthLike => new GenericMonthlyScheduleBuilder(cycleKind, 0, None, None)
  }

}

class GenericDailyScheduleBuilder(ck: Cycle, offset: Int, validfCycleSheduleDayMapOpt: Option[LocalDate => BitSet])
    extends DailyScheduleBuilder[Schedule](DailyCycle(ck.cycleSize, offset), validfCycleSheduleDayMapOpt)
    with GenericScheduleBuilder
{

  override def offset(offs: Int): GenericScheduleBuilder = {
    new GenericDailyScheduleBuilder(ck, offs, validfCycleSheduleDayMapOpt)
  }

  override def build(): Schedule = super[DailyScheduleBuilder].build()
}

class GenericMonthlyScheduleBuilder(ck: Cycle, offset: Int, dayfuncOpt: Option[ScheduleDayOfMonth],
    workingSchedOpt: Option[WorkingSchedule])
    extends MonthlyScheduleBuilder[Schedule](dayfuncOpt, workingSchedOpt, MonthlyCycle.builder().cycleSize(ck.cycleSize).offset(offset))
    with GenericScheduleBuilder
{

  override def offset(offs: Int): GenericScheduleBuilder =
    new GenericMonthlyScheduleBuilder(ck, offs, dayfuncOpt, workingSchedOpt)

  override def build(): Schedule = super[MonthlyScheduleBuilder].build()
}

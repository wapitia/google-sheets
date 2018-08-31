package com.wapitia
package calendar

trait GenericScheduleBuilder

/** Build a schedule of any kind using a Generic overlay on the concrete schedule types. */
// TODO
object ScheduleFactory {

  import CycleKind._

  def builder(cycleKind: Cycle): GenericScheduleBuilder = cycleKind.kind match {
    case DayLike => new GenericDailyScheduleBuilder(cycleKind)
    case MonthLike => new GenericMonthlyScheduleBuilder(cycleKind)
  }

  class GenericDailyScheduleBuilder(ck: Cycle) extends DailySchedule.Builder(DailyCycle(ck.cycleSize, 0), None) with GenericScheduleBuilder {
  }

  class GenericMonthlyScheduleBuilder(ck: Cycle) extends MonthlySchedule.Builder(None, None, MonthlyCycle.builder().cycleSize(ck.cycleSize).offset(0)) with GenericScheduleBuilder {
  }

}

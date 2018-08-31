package com.wapitia
package calendar

import java.time.LocalDate

/** A schedule suitable for creating a stream of monthly dates.
 *  Monthly-like Schedules include bi-monthly, semi-monthly, quarterly, annually,
 *  anything that aligns with the first of some month.
 *
 * @param monthlyCycle positive int indicating the frequency of the schedule
 *                     at the granularity of one month.
 * @param monthOffset  non-negative int is offset within cycle for when to start counting.
 *                     0 means start in January, for example.
 *                     must be less than monthlyCycle.
 *
 */
class MonthlySchedule(dom: ScheduleDayOfMonth, working: WorkingSchedule, monCycle: MonthlyCycle)
extends Schedule
{

  import MonthlySchedule._

  def monthCycleIncr(candidateDate: LocalDate): LocalDate = {
    monthWithAdjust(candidateDate.plusMonths(monCycle.monthsInCycle))
  }

  // infuse the given month with the proper day of month according to the given DayOfMonthAdjustment
  def monthWithAdjust(monDate: LocalDate): LocalDate = {
    monDate.withDayOfMonth(dom.dayOfMonthOf(monDate))
  }

  override def onOrAfter(onOrAfterDate: LocalDate): Stream[LocalDate] = {

    def assureOnOrAfter(candidateDate: LocalDate): LocalDate =
      if (candidateDate.isBefore(onOrAfterDate))
        assureOnOrAfter(monthCycleIncr(candidateDate))
      else
        monthWithAdjust(candidateDate)

    val earliestPossibleCycleDate = monCycle.cycleStartDate(onOrAfterDate)
    val candidateCycleDate = assureOnOrAfter(earliestPossibleCycleDate)

    def loop(v: LocalDate): Stream[LocalDate] = v #:: loop(monthCycleIncr(v))

    loop(candidateCycleDate)
  }

}

object MonthlySchedule {

  def builder() = new MonthlyScheduleBuilder[MonthlySchedule](None, None,  MonthlyCycle.builder())

  /** Monthly on the given day with no adjustment */
  def monthly(day1: Int): MonthlySchedule =
    builder().dayOfMonth(day1)
      .workingSched(WorkingSchedule.any).monthlyCycle(MonthlyCycle.Monthly).build

  /** First day of the month with no adjustment */
  def firstOfMonth(): MonthlySchedule =
    builder().dayOfMonth(ScheduleDayOfMonth.FirstDay)
      .workingSched(WorkingSchedule.any).monthlyCycle(MonthlyCycle.Monthly).build

  /** First day of the month adjusted by working schedule */
  def firstOfMonth(working: WorkingSchedule): MonthlySchedule =
    builder().dayOfMonth(ScheduleDayOfMonth.FirstDay)
      .workingSched(working).monthlyCycle(MonthlyCycle.Monthly).build

  /** The last day of the month, which fluctuates 31,28,31, etc. */
  def endOfMonth(): MonthlySchedule =
    builder().dayOfMonth(ScheduleDayOfMonth.LastDay)
      .workingSched(WorkingSchedule.any).monthlyCycle(MonthlyCycle.Monthly).build

  /** The last day of the month adjusted by working schedule */
  def endOfMonth(working: WorkingSchedule): MonthlySchedule =
    builder().dayOfMonth(ScheduleDayOfMonth.LastDay)
      .workingSched(working).monthlyCycle(MonthlyCycle.Monthly).build

  def biMonthly(day1: Int, monthOffset: Int): Schedule = ???  // TODO

  def biMonthly(day1: Int, sampleDate: LocalDate): Schedule = ???  // TODO

  def quarterly(sampleDate: Int, monthOffset: Int): Schedule = ???  // TODO

  def quarterly(dayOfMonth: Int, sampleMonth: LocalDate): Schedule = ???  // TODO

  def annually(dayOfYear: Int): Schedule = ???  // TODO

  def annually(sampleDate: LocalDate): Schedule = ???  // TODO

}

class MonthlyScheduleBuilder[A <: Schedule](
    dayfuncOpt: Option[ScheduleDayOfMonth],
    workingSchedOpt: Option[WorkingSchedule],
    monCycleBuilder: MonthlyCycle.Builder) {

  def monthsInCycle(nMonths: Int): MonthlyScheduleBuilder[A] = new MonthlyScheduleBuilder[A](dayfuncOpt, workingSchedOpt, monCycleBuilder.cycleSize(nMonths))

  def monthOffset(nOffset: Int): MonthlyScheduleBuilder[A] = new MonthlyScheduleBuilder[A](dayfuncOpt, workingSchedOpt, monCycleBuilder.offset(nOffset))

  def monthlyCycle(monCycle: MonthlyCycle): MonthlyScheduleBuilder[A] = new MonthlyScheduleBuilder[A](dayfuncOpt, workingSchedOpt, monCycleBuilder.set(monCycle))

  def dayOfMonth(day: Int): MonthlyScheduleBuilder[A] = dayOfMonth(new BoundedFixedScheduleDayOfMonth(day))

  def dayOfMonth(dayOfMonthAdj: ScheduleDayOfMonth): MonthlyScheduleBuilder[A] = new MonthlyScheduleBuilder[A](Some(dayOfMonthAdj), workingSchedOpt, monCycleBuilder)

  def workingSched(sched: WorkingSchedule): MonthlyScheduleBuilder[A]  = new MonthlyScheduleBuilder[A](dayfuncOpt, Some(sched), monCycleBuilder)

  def build(): A = {
    val schedDayOfMonth: ScheduleDayOfMonth = dayfuncOpt.getOrElse(ScheduleDayOfMonth.FirstDay)
    val workingSched: WorkingSchedule = workingSchedOpt.getOrElse(WorkingSchedule.any)
    val monthlyCycle: MonthlyCycle = monCycleBuilder.build()
    new MonthlySchedule(schedDayOfMonth, workingSched, monthlyCycle).asInstanceOf[A]
  }

}

package com.wapitia
package calendar

import java.time.LocalDate

/** Create a schedule object suitable for creating a stream of monthly dates.
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
class MonthlySchedule(day1f: DayOfMonthAdjust, working: WorkingSchedule, monthCycle: Int, monthOffset: Int)
extends Schedule
{

  import MonthlySchedule._

  def monthCycleIncr(candidateDate: LocalDate): LocalDate = {
    monthWithAdjust(candidateDate.plusMonths(monthCycle))
  }

  // infuse the given month with the proper day of month according to the given DayOfMonthAdjustment
  def monthWithAdjust(monDate: LocalDate): LocalDate = {
    monDate.withDayOfMonth(day1f.dayOfMonthOf(monDate))
  }

  override def onOrAfter(onOrAfterDate: LocalDate): Stream[LocalDate] = {

    def assureOnOrAfter(candidateDate: LocalDate): LocalDate =
      if (candidateDate.isBefore(onOrAfterDate))
        assureOnOrAfter(monthCycleIncr(candidateDate))
      else
        monthWithAdjust(candidateDate)

    val earliestPossibleCycleDate = cycleMonthOnOrBefore(onOrAfterDate, monthCycle, monthOffset)
    val candidateCycleDate = assureOnOrAfter(earliestPossibleCycleDate)

    def loop(v: LocalDate): Stream[LocalDate] = v #:: loop(monthCycleIncr(v))

    loop(candidateCycleDate)
  }


}

object MonthlySchedule {

  val FirstDayOfMonth = 1
  val LowestLastDayOfMonth = 28   // that would be February
  val HighestLastDayOfMonth = 31

  def builder() = new Builder(None, None, None, None)

  class Builder(
      dayfuncOpt: Option[DayOfMonthAdjust],
      workingSchedOpt: Option[WorkingSchedule],
      monthCycleOpt: Option[Int],
      monthOffsetOpt: Option[Int]
  ) {

    def monthly(dayOfMonth: Int): Builder = monthly(new DayOfMonthBounded(dayOfMonth))

    def monthly(dayOfMonthAdj: DayOfMonthAdjust) = dayOfMonth(dayOfMonthAdj).cycle(1).monthOffset(0)

    def cycle(nMonths: Int): Builder = new Builder(dayfuncOpt, workingSchedOpt, Some(nMonths), monthOffsetOpt)

    def monthOffset(nOffset: Int): Builder = new Builder(dayfuncOpt, workingSchedOpt, monthCycleOpt, Some(nOffset))

    def dayOfMonth(dayOfMonthAdj: DayOfMonthAdjust): Builder  = new Builder(Some(dayOfMonthAdj), workingSchedOpt, monthCycleOpt, monthOffsetOpt)

    def workingSched(sched: WorkingSchedule): Builder  = new Builder(dayfuncOpt, Some(sched), monthCycleOpt, monthOffsetOpt)

    def validate {
      if (dayfuncOpt.isEmpty) throw new RuntimeException("MonthSchedule.Builder month of day not defined")
      if (monthCycleOpt.isEmpty) throw new RuntimeException("MonthSchedule.Builder monthly cycle not defined")
      if (monthCycleOpt.get <= 0) throw new RuntimeException("MonthSchedule.Builder monthly cycle must be a positive integer")
      if (monthOffsetOpt.isEmpty) throw new RuntimeException("MonthSchedule.Builder monthly cycle not defined")
      if (monthOffsetOpt.get < 0) throw new RuntimeException("MonthSchedule.Builder monthly offset must be a non-negative integer")
      if (monthOffsetOpt.get >= monthCycleOpt.get) throw new RuntimeException("MonthSchedule.Builder monthly offset must be less than cycle size")
    }

    def build(): Schedule = {
      validate
      new MonthlySchedule(dayfuncOpt.get, workingSchedOpt.getOrElse(WorkingSchedule.any),
        monthCycleOpt.get, monthOffsetOpt.get)
    }
  }

  /** Monthly on the given day with no adjustment */
  def monthly(day1: Int): Schedule = builder().monthly(day1).build

  /** The last day of the month, which fluctuates 31,28,31, etc. */
  def endOfMonth(): Schedule = builder().monthly(new DayOfMonthBounded(HighestLastDayOfMonth)).build

  /** The last day of each month but for which  */
  def endOfMonth(working: WorkingSchedule): Schedule = builder().monthly(new DayOfMonthBounded(HighestLastDayOfMonth)).build

  /**
   * @param monthOffset
   */
  def biMonthly(day1: Int, monthOffset: Int): Schedule = ???  // TODO

  /**
   * @param monthOffset
   */
  def biMonthly(day1: Int, sampleMonth: LocalDate): Schedule = ???  // TODO

  /**
   * @param monthOffset
   */
  def quarterly(quarterlyDay: Int, monthOffset: Int): Schedule = ???  // TODO

  /**
   * @param monthOffset
   */
  def quarterly(dayOfMonth: Int, sampleMonth: LocalDate): Schedule = ???  // TODO

  /**
   * @param monthOffset
   */
  def annually(dayOfYear: Int): Schedule = ???  // TODO

  /**
   * @param monthOffset
   */
  def annually(sampleDate: LocalDate): Schedule = ???  // TODO

  def multiMonthSchedule(dayOfMonthAdj: DayOfMonthAdjust, startMonth: Int, monthlyCycle: Int): Schedule =
    builder.monthly(new DayOfMonthBounded(HighestLastDayOfMonth)).build

  /** find the latest month in the cycle that is on or before the given month
   *  The day of month from the targetDate is not used, just the year and month.
   *  The result date will start at day-of-month 1.
   */
  def cycleMonthOnOrBefore(targetDate: LocalDate, monthCycle: Int, monthOffset: Int): LocalDate = {
    val cyclemonth0 = cycleMonth0(targetDate, monthCycle, monthOffset)
    val (resYear:Int, resMonth0:Int) = norm(targetDate.getYear, cyclemonth0)
    val resultDate = LocalDate.of(resYear, resMonth0+1, 1)
    resultDate
  }

  def cycleMonth0(targetDate: LocalDate, monthCycle: Int, monthOffset: Int): Int = {
    val targetMonth0 = targetDate.getMonthValue - 1  // 0 to 11
    val nunc: Int = Math.floor((targetMonth0 - monthOffset).toFloat / monthCycle.toFloat).toInt
    val cyclemonth0: Int = nunc * monthCycle + monthOffset
    cyclemonth0
  }

  def norm(year: Int, month0: Int): (Int,Int) =
    if (month0 >= MonthsPerYear)
      norm(year+1, month0 - MonthsPerYear)
    else if (month0 < 0)
      norm(year-1, month0 + MonthsPerYear)
    else
      (year,month0)



}



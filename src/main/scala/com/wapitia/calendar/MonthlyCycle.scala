package com.wapitia
package calendar

import java.time.LocalDate
import java.time.YearMonth

/** Defines a monthly cycle in terms of number of months in the cycle, and an offset to start.
 *
 *  @param monthCycle number of months in the cycle, must be a positive number.
 *  @param monthOffset offset of the start month in the cycle, must be non-negative and less than monthCycle.
 */
case class MonthlyCycle(monthsInCycle: Int, monthOffset: Int) extends CycleTemplate(monthsInCycle, monthOffset) {

  /** Determine the index of the month of a target date within a monthly cycle.
   *  `
   *  monthCycle monthOffset Month of date       = result
   *  ========== =========== ==================  = ======
   *      3           0      Jan, Apr, Jul, Oct      0
   *      3           0      Feb, May, Aug, Nov      1
   *      3           0      Mar, Jun, Sep, Dec      2
   *      3           1      Feb, May, Aug, Nov      0
   *      3           1      Mar, Jun, Sep, Dec      1
   *      3           1      Jan, Apr, Jul, Oct      2
   *      3           2      Mar, Jun, Sep, Dec      0
   *      3           2      Jan, Apr, Jul, Oct      1
   *      3           2      Feb, May, Aug, Nov      2
   *  `
   *  @param targetDate date whose month's cycleMonth is to be calculated
   */
  def cycleMonth(targetDate: LocalDate): Int = {
    val mIndex = targetDate.getMonthValue - 1  // 0 to 11
    (mIndex + monthsInCycle - monthOffset) % monthsInCycle
  }

  /** Return the first day of the cycle in which the target date resides.
   *  Note that this assumes that all monthly cycles start at the first of a month,
   *  and no partial months are possible. This may change in the future.
   */
  def cycleStartDate(targetDate: LocalDate): LocalDate = {
    val cyclemonth0 = cycleMonth(targetDate)
    val yearMonth = norm(targetDate.getYear, targetDate.getMonthValue - cyclemonth0)
    LocalDate.of(yearMonth.getYear, yearMonth.getMonthValue, 1)
  }

  /** Normalize the given year/month to a year/month where the month is within 1 to 12 inclusive.
   *  Note that this is not the fastest algorithm when the given month is way out of
   *  bounds, but the usage here works with an input that is within 1 year of the result.
   */
  def norm(year: Int, month1: Int): YearMonth =
    if (month1 > MonthsPerYear)
      norm(year+1, month1 - MonthsPerYear)
    else if (month1 < 1)
      norm(year-1, month1 + MonthsPerYear)
    else
      YearMonth.of(year,month1)

}

object MonthlyCycle {

  val Monthly: MonthlyCycle = MonthlyCycle(1, 0)

  def defaultMonthCycle(): Int = throw new RuntimeException("Month Cycle Not Defined")

  def builder(): Builder = new Builder(None, None, defaultMonthCycle)

  /** Builder class adding convenience methods to accumulate each parameter individually or together. */
  class Builder(monthCycleOpt: Option[Int], monthOffsetOpt: Option[Int], monthCycleDefault: => Int)
    extends CycleTemplate.Builder[MonthlyCycle,MonthlyCycle.Builder](monthCycleOpt, monthOffsetOpt, monthCycleDefault)
  {
    override def builderConstructor(monthCycleOpt: Option[Int], monthOffsetOpt: Option[Int], monthCycleDefault: => Int): Builder =
      new Builder(monthCycleOpt, monthOffsetOpt, monthCycleDefault)

    override def objConstructor(cycleSize: Int, offset: Int): MonthlyCycle = MonthlyCycle(cycleSize, offset)
  }

  /** Calculate the cycle start date for a given date in the cycle.
   *  The day of month from the targetDate is not used, just the year and month.
   *  The result cycle start date will start at the first day of a month
   */
  def cycleStartDate(monthCycle: Int, monthOffset: Int, targetDate: LocalDate): LocalDate =
    MonthlyCycle(monthCycle, monthOffset).cycleStartDate(targetDate)

  /** Determine the index of the month of a target date within a monthly cycle.
   *
   *  For example, in a quarterly cycle starting at the beginning of the year,
   *  monthCycle is 3 because there are 3 months in a quarter, monthOffset is 0 because
   *  the offset starts in January. A target date anywhere in January, April, July, October will return 0,
   *  a target date anywhere in February, May, August, November will return 1, etc.
   *
   *  @param monthCycle number of months in the cycle, ge 1
   *  @param monthOffset which month constitutes month 0. ge 0, lt monthCycle.
   *  @param targetDate date whose month is to be indexed within a given target date.
   */
  def cycleMonthIndex(monthCycle: Int, monthOffset: Int, targetDate: LocalDate): Int =
    MonthlyCycle(monthCycle, monthOffset).cycleMonth(targetDate)

}

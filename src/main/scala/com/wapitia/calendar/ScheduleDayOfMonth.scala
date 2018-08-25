package com.wapitia
package calendar

import java.time.LocalDate

/** A scheduled day of the month when given some particular date.
 *  Defines a particular day of the month as a schedule day.
 */
trait ScheduleDayOfMonth {
  def dayOfMonthOf(month: LocalDate): Int
}

/** Day of month will be the value of the given constructor argument.
 *  No bound check is made, so is risky when dayOfMonth gt 28 as not all months have
 *  more than 28 days.
 *
 *  @param dayOfMonth should be gt 1 and less than the minumum days in each provided month.
 */
class FixedScheduleDayOfMonth(dayOfMonth: Int) extends ScheduleDayOfMonth {

  override def dayOfMonthOf(month: LocalDate): Int = dayOfMonth max 1
}

/** Fixed day of month  */
class BoundedFixedScheduleDayOfMonth(dayOfMonth: Int) extends FixedScheduleDayOfMonth(dayOfMonth)  {

  override def dayOfMonthOf(month: LocalDate): Int = super.dayOfMonthOf(month) min month.lengthOfMonth

}

object ScheduleDayOfMonth {

  val FirstDay = new FixedScheduleDayOfMonth(1)
  val LastDay = new BoundedFixedScheduleDayOfMonth(31)
}


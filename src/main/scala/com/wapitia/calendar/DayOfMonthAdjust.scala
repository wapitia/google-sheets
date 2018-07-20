package com.wapitia
package calendar

import java.time.LocalDate

/**
 * Adjustment to a candidate day-of-month for each given month.
 */
trait DayOfMonthAdjust {
  def dayOfMonthOf(month: LocalDate): Int
}


/** Day of month will be the value of the given constructor argument.
 *  No adjusting takes place, which is risky.
 */
class DayOfMonthIdentity(dayOfMonth: Int) extends DayOfMonthAdjust {
  
  override def dayOfMonthOf(month: LocalDate): Int = dayOfMonth
  
}

class DayOfMonthBounded(dayOfMonth: Int) extends DayOfMonthAdjust {
  
  override def dayOfMonthOf(month: LocalDate): Int = dayOfMonth.max(1).min(month.lengthOfMonth)
  
}


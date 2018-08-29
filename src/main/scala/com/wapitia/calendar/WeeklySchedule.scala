package com.wapitia
package calendar

import java.time.LocalDate
import java.time.DayOfWeek._

/** Helper functions facilitating DailySchedule's interface.
 *  A weekly schedule is a narrow type of Daily Schedule with 7-day cycles.
 */
object WeeklySchedule {

  /** Produce a weekly stream starting from the given initial date.
   *  This is a thin helper on top of the DailySchedule builder's
   *  interface which sets the week start day as SUNDAY, which shouldn't matter,
   *  and sets the cycle offset derived from the initial date's day of week.
   */
  def from(initialDate: LocalDate): Stream[LocalDate] =
    DailySchedule
      .multipleWeekly(1, SUNDAY, 0)
      .withWeekDaysInCycle(initialDate.getDayOfWeek)
      .build()
      .onOrAfter(initialDate)

}

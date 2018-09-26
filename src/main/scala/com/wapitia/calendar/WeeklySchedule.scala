package com.wapitia
package calendar

import java.time.{DayOfWeek, LocalDate}
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
  def weeklyStarting(initialDate: LocalDate): Stream[LocalDate] =
    multipleWeekly(1, SUNDAY, 0)
    .withWeekDaysInSchedule(initialDate.getDayOfWeek)
    .build()
    .starting(initialDate)

  /** Produce a weekly stream starting from the given initial date.
   *  This is a thin helper on top of the DailySchedule builder's
   *  interface which sets the week start day as SUNDAY, which shouldn't matter,
   *  and sets the cycle offset derived from the initial date's day of week.
   */
  def biweeklyStarting(initialDate: LocalDate): Stream[LocalDate] =
    multipleWeekly(weeksInCycle=2, startDayOfWeek=SUNDAY, startCycleWeekOffset=0)
    .withWeekDayOffsetsInSchedule((biweeklyOffset(initialDate), initialDate.getDayOfWeek))
    .build()
    .starting(initialDate)

  def biweeklyOffset(initialDate: LocalDate): Int = multiWeeklyOffset(2, initialDate)

  private val MWFudgeDays = 0
//  private val MWFudgeDays = 5   // fails @ -06
//  private val MWFudgeDays = -2   // fails @
  def multiWeeklyOffset(numWeeks: Int, initialDate: LocalDate): Int = {
    // TODO: Reduce
    val res = ((Epoch.until(initialDate).getDays() + MWFudgeDays) / DaysPerWeek) % numWeeks
    res
  }

  /** Builder traverses weekly with the week starting on Sunday.
   *
   *  @usage
   *  Create a weekly schedule where each Friday is a day in the schedule,
   *  and where the week starts on a Sunday. In this case, the starting day of the week
   *  does not matter.
   *  {{{
   *    val dsched: DailySchedule = DailySchedule.weeklyStartingSunday()
   *      .withWeekDaysInCycle(FRIDAY)
   *      .build()
   *  }}}
   *
   */
  def weeklyStartingSunday(): DailyScheduleBuilder[DailySchedule] = weekly(SUNDAY)

  /** Builder traverses weekly with the week starting on Monday.
   *
   *  @usage
   *  Create a weekly schedule where each Tuesday is a day in the schedule,
   *  and where the week starts on a Monday. In this case, the starting day of the week
   *  does not matter.
   *  {{{
   *    val dsched: DailySchedule = DailySchedule.weeklyStartingMonday()
   *      .withWeekDaysInCycle(TUESDAY)
   *      .build()
   *  }}}
   *
   */
  def weeklyStartingMonday(): DailyScheduleBuilder[DailySchedule] = weekly(MONDAY)

  /** Builder traverses weekly, starting at a particular day of the week. */
  def weekly(startDayOfWeek: DayOfWeek): DailyScheduleBuilder[DailySchedule] =
    multipleWeekly(1, startDayOfWeek, 0)

  /** Builder traverses weekly, starting at a particular day of the week.
   *
   *  @param startDayOfWeek start day of week of cycle.
   *  @param startCycleWeekOffset offset in weeks for the start of the cycle.
   */
  def multipleWeekly(weeksInCycle: Int, startDayOfWeek: DayOfWeek, startCycleWeekOffset: Int): DailyScheduleBuilder[DailySchedule] = {
    require(weeksInCycle > 0)
    require(startCycleWeekOffset >= 0)
    require(startCycleWeekOffset < weeksInCycle)
    val dailyCycle = DailyCycle.multipleWeekly(weeksInCycle, startDayOfWeek, startCycleWeekOffset)
    DailySchedule.builder(dailyCycle)
  }

}

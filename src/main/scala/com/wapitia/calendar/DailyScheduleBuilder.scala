package com.wapitia
package calendar

import java.time.{DayOfWeek, LocalDate}
import java.time.temporal.ChronoUnit._
import DayOfWeek._

import DailySchedule._

import scala.collection.BitSet

/** Daily and weekly schedule builder starts with the number of days in the cycle and
 *  an offset indicating the start day(s) of the cycle.
 *
 *  @param nCycleDays is the number of days in the cycle, and can be any positive integer.
 *           1 means daily, 2 means every two days, 7 means weekly, etc.
 *  @param dayOffset indicates the first day of the cycle. It is a modulo of nCycleDays,
 *           so must be between 0 and (nCycleDays-1)
 *           This is relative to the Wapita calendar EPOCH (1970-01-01).
 *           In other words to start on 1970-01-01, dayOffset would be 0.
 */
class DailyScheduleBuilder[A <: Schedule](
    dailyCycle: DailyCycle,
    validfCycleSheduleDayMapOpt: Option[LocalDate => BitSet])
{

  /** Builder traverses every day. Cycle Number of Days is 1, and offset is 0. */
  def withCycleSheduleDayMapFunction(fCycleSheduleDayMapFunc: LocalDate => BitSet): DailyScheduleBuilder[A] =
    new DailyScheduleBuilder[A](dailyCycle, Some(fCycleSheduleDayMapFunc))

  /**
   * O-based list of days in the cycle, in whatever order, relative to the dayOffset
   * Each cycleDay must be ge 0 and lt nCycleDays
   */
  def withScheduleDays(scheduleDays: Int*): DailyScheduleBuilder[A] =
    withCycleSheduleDayMapFunction(_ => cycleScheduleDayMap(scheduleDays))

  /** Set the cyclic day(s) of the week in which the schedule falls.
   *  REQUIREMENT: The number of days in the cycle (parameter 'nCycleDays) must be a
   *  multiple of the DAYS_PER_WEEK, 7.
   */
  def withWeekDayOffsetsInSchedule( dows: (Int, DayOfWeek)*): DailyScheduleBuilder[A] = {
    require(dailyCycle.daysInCycle % DaysPerWeek == 0)
    withScheduleDays(dows.map {
      case (weekOffset, dayOfWeek) => dailyCycle.weekCycleOffset(weekOffset,dayOfWeek)
    }: _*)
  }

  /** Set the cyclic day(s) of the week in which the schedule falls.
   *  REQUIREMENT: The number of days in the cycle (parameter 'nCycleDays)
   *  must be a multiple of the DAYS_PER_WEEK, 7.
   */
  def withWeekDaysInSchedule(dows: DayOfWeek*): DailyScheduleBuilder[A] = {
    require(dailyCycle.daysInCycle % DaysPerWeek == 0)
    val dayOffsets: Seq[Int] = dows.map(tup => dailyCycle.dayOfWeekOffset(tup))
    withScheduleDays(dayOffsets: _*)
  }

  def weekdays(): DailyScheduleBuilder[A] =
    withWeekDaysInSchedule(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY)

  def weekends(): DailyScheduleBuilder[A] =
    withWeekDaysInSchedule(SATURDAY, SUNDAY)

  def build(): A = {
    val modDayOffset = dailyCycle.dayOffset % dailyCycle.daysInCycle
    val validfCycleSheduleDayMap =
      validfCycleSheduleDayMapOpt.getOrElse((ld: LocalDate) => firstDayBitMap())

    new DailySchedule(validfCycleSheduleDayMap, dailyCycle).asInstanceOf[A]
  }
}

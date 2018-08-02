package com.wapitia
package calendar

import java.time.LocalDate
import scala.collection.immutable.BitSet

/** The WorkingSchedule keeps ranges of passable dates, such as work days.
 *  Overlaid onto another schedule, this defines the gaps of valid dates.
 *  A WorkingSchedule instance must be able to (quickly) calculate
 *  valid "working" dates that are in proximity to some arbitrarily chosen date.
 */
trait WorkingSchedule extends ScheduleDateFilter {

  /** Test if the given date passes, so is a valid date. Return true iff valid. */
  override def test(date: LocalDate): Boolean

  /** Return the first date in its passable date ranges.
   *  Return Some[LocalDate.min] for an open-ended date range (no real beginning).
   *  Return None if there are no passable dates in all of the working schedule.
   */
  def firstDate(): Option[LocalDate]

  /** Return the last date in its passable date ranges.
   *  Return Some[LocalDate.max] for an open-ended date range (no real ending).
   *  Return None if there are no passable dates in all of the working schedule.
   */
  def lastDate(): Option[LocalDate]

  /** Return the next passable date after the given one, or 'None if there isn't one.
   *  A valid return date should be an actual date near the input date,
   *  and not LocalDate.min or LocalDate.max */
  def nextDateAfter(date: LocalDate): Option[LocalDate]

  /** Return the previous passable date before the given one, or 'None if there isn't one.
   *  A valid return date should be an actual date near the input date,
   *  and not LocalDate.min or LocalDate.max */
  def prevDateBefore(date: LocalDate): Option[LocalDate]

  def workingSchedPolicy: WorkingSchedulePolicy = WorkingSchedule.DefaultPolicy

}

object WorkingSchedule {

  val DefaultPolicy = WorkingSchedulePolicy.SkipDay

//  class WeeklyWorkingSchedule extends WorkingSchedule {
//
//
//
//    /** Test if the given date passes, so is a valid date. Return true iff valid. */
//    def test(date: LocalDate): Boolean
//
//    def firstDate(): Option[LocalDate]
//
//    /** Return the last date in its passable date ranges.
//     *  Return Some[LocalDate.max] for an open-ended date range (no real ending).
//     *  Return None if there are no passable dates.
//     */
//    def lastDate(): Option[LocalDate]
//
//    /** Return the next passable date after the given one, or None if there isn't one */
//    def nextDateAfter(date: LocalDate): Option[LocalDate]
//
//    /** Return the previous passable date before the given one, or None if there isn't one */
//    def prevDateBefore(date: LocalDate): Option[LocalDate]
//
//  }

  val any: WorkingSchedule = new WorkingSchedule {
    /** Return the first date in its passable date ranges.
     *  Return Some[LocalDate.min] for an open-ended date range (no real beginning).
     *  Return None if there are no passable dates.
     */
    def firstDate(): Option[LocalDate] = Some(LocalDate.MIN)

    /** Return the last date in its passable date ranges.
     *  Return Some[LocalDate.max] for an open-ended date range (no real ending).
     *  Return None if there are no passable dates.
     */
    def lastDate(): Option[LocalDate] = Some(LocalDate.MAX)

    /** Return the next passable date after the given one, or None if there isn't one */
    def nextDateAfter(date: LocalDate): Option[LocalDate] = Some(date.plusDays(1L))

    /** Return the previous passable date before the given one, or None if there isn't one */
    def prevDateBefore(date: LocalDate): Option[LocalDate] = Some(date.minusDays(1L))

    def test(date: LocalDate): Boolean = true

    val workingSchedulePolicy = DefaultPolicy

  }

  val weekdaysMF: WorkingSchedule = new WorkingSchedule {
    val dayMap: BitSet = BitSet(7)

    /** Return the first date in its passable date ranges.
     *  Return Some[LocalDate.min] for an open-ended date range (no real beginning).
     *  Return None if there are no passable dates.
     */
    def firstDate(): Option[LocalDate] = Some(LocalDate.MIN)

    /** Return the last date in its passable date ranges.
     *  Return Some[LocalDate.max] for an open-ended date range (no real ending).
     *  Return None if there are no passable dates.
     */
    def lastDate(): Option[LocalDate] = Some(LocalDate.MAX)

    /** Return the next passable date after the given one, or None if there isn't one */
    def nextDateAfter(date: LocalDate): Option[LocalDate] = Some(date.plusDays(1L))

    /** Return the previous passable date before the given one, or None if there isn't one */
    def prevDateBefore(date: LocalDate): Option[LocalDate] = Some(date.minusDays(1L))

    def test(date: LocalDate): Boolean = true

    val workingSchedulePolicy = DefaultPolicy

  }

  def everySecondFriday(): WorkingSchedule = {
    ???
  }

}

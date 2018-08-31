package com.wapitia
package calendar

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit._

import scala.collection.BitSet

/** A schedule object suitable for creating a stream of daily-based dates.
 *  Day-like Schedules include daily, weekly, biweekly, semi-weekly,
 *  anything that aligns with some constant multiple of days.
 *
 *  @param nCycleDays the number of days in the cycle
 *  @param dayOffset starting offset within cycle, relative to com.wapitia.calendar.JAVA_DATE_DAY_0
 *  @param fCycleSheduleDayMap given a date in some cycle, the function provides a BitSet whose
 *                     size is nCycleDays and whose bits represent valid candidate days in the schedule,
 *                     relative to the first day in the cycle.
 */
case class DailySchedule(fCycleSheduleDayMap: LocalDate => BitSet, dailyCycle: DailyCycle)
extends Schedule {

  import DailySchedule._

  override def onOrAfter(onOrAfterDate: LocalDate): Stream[LocalDate] = {
    val acd: LocalDate = cycleAnchorDate(onOrAfterDate)
    assert(acd.toEpochDay() <= onOrAfterDate.toEpochDay())
    assert(acd.toEpochDay() > onOrAfterDate.toEpochDay() - dailyCycle.daysInCycle)
    val res = chunkCycleStream(onOrAfterDate, acd, dailyCycle.daysInCycle, fCycleSheduleDayMap)
    res
  }

  /** The first day (day 0) of the cycle in which onOrAfterDate resides,
   * according to nCycleDays and dayOffset.
   */
  def cycleAnchorDate(onOrAfterDate: LocalDate): LocalDate =
    DailySchedule.cycleAnchorDate(onOrAfterDate, dailyCycle)
}

object DailySchedule {

  import DayOfWeek._

  def builder(dailyCycle: DailyCycle): DailyScheduleBuilder[DailySchedule] = new DailyScheduleBuilder[DailySchedule](dailyCycle, None)

  /** Every day is a day of the schedule */
  def daily() = builder(DailyCycle.Daily)

  /** Builder traverses weekly with the week starting on Sunday. */
  def weeklyStartingSunday(): DailyScheduleBuilder[DailySchedule] = weekly(SUNDAY)

  /** Builder traverses weekly with the week starting on Sunday. */
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
    builder(dailyCycle)
  }

  def cycleScheduleDayMap(cycleDays: Seq[Int]): BitSet =
    BitSet(cycleDays: _*)

  /** A BitSet where every day in the cycle (0.. cycleSize-1) is set */
  def everyDayBitMap(cycle: Int): BitSet = BitSet(0 to (cycle-1): _*)

  /** A BitSet where only the single day as given by the offset is set */
  def offsetDayBitMap(offset: Int): BitSet = BitSet(offset)

  /** A `BitSet` where only the first day (0) set.
   *  This is the `DailyScheduleBuilder`'s default for any cycle start date when one isn't defined.
   */
  def firstDayBitMap(): BitSet = BitSet(0)

  /** The first day (day 0) of the cycle in which onOrAfterDate resides,
   * according to nCycleDays and dayOffset.
   */
  def cycleAnchorDate(onOrAfterDate: LocalDate, dailyCycle: DailyCycle): LocalDate = {
    val anchorDateOffset = dailyCycle.anchorDayOffset(onOrAfterDate.toEpochDay())
    onOrAfterDate.plusDays(anchorDateOffset)
  }

  // for endless cycle loops which occur when the cycle chunks do not produce valid schedule dates in that timeframe
  // this defines how many empty cycles to allow before throwing up our hands in some exception
  // This can happen because the cycle schedule day map may be user defined.
  val MaximumAllowedEmptyCyclesInStreamBeforeThrowing = 100

  /** For some schedule generate a finite stream of all sequential candidate dates which fall on or after
   *  some particular date.
   *
   *  @param onOrAfterDate starting date for the stream of candidate dates
   *  @param cycleAnchorDate date of the first day of the first candidate cycle, a calculated date
   *         normalized to be on or before the `onOrAfterDate, but within nCycleDays of that date
   *  @param nCycleDays the number of days in the schedule, must be a positive integer
   *  @param scheduleDayMap function when given the a cycle start date will return the BitSet
   *         whose bits represent each schedule day of that cycle, marking those that are to be added to the schedule stream.
   *
   */
  def chunkCycleStream(onOrAfterDate: LocalDate, cycleAnchorDate: LocalDate, nCycleDays: Int, scheduleDayMap: LocalDate => BitSet): Stream[LocalDate] = {

    require(!cycleAnchorDate.isAfter(onOrAfterDate))
    require(onOrAfterDate.isBefore(cycleAnchorDate.plusDays(nCycleDays)))
    require(nCycleDays > 0)

    // ++ ENDLESS LOOP CHECKING
    // stopgap: when there are too many sequential empty bitsets
    var sequentialEmptyCycle = 0
    def cycleBitCheck(cycleBitSet: BitSet) {
      if (cycleBitSet.isEmpty) {
        sequentialEmptyCycle += 1
        if (sequentialEmptyCycle > MaximumAllowedEmptyCyclesInStreamBeforeThrowing)
          throw new RuntimeException(s"Exiting chunkCycleStream as there are $sequentialEmptyCycle sequential empty cycles")
      }
      else {
        sequentialEmptyCycle = 0
      }
    }
    // -- ENDLESS LOOP CHECKING

    /** Generate a finite stream of the candidate dates for the cycle whose first day is the given start date.
     *
     */
    def chunkSeq(cycleStartDate: LocalDate): Stream[LocalDate] = {
      val candidateBitsInCycle: BitSet = scheduleDayMap(cycleStartDate)
      val trueBitSet: BitSet =
        if (candidateBitsInCycle.isEmpty)
          BitSet.empty
        else if (cycleStartDate.isBefore(onOrAfterDate)) {
          val daysToExclude = DAYS.between(cycleStartDate, onOrAfterDate)
          if (daysToExclude >= nCycleDays)
            BitSet.empty
          else {
            val bitsToExclude = BitSet((0 to (daysToExclude.toInt - 1)): _*)
            candidateBitsInCycle &~ bitsToExclude
          }
        }
        else {
          candidateBitsInCycle
        }
      // ++ ENDLESS LOOP CHECKING
      cycleBitCheck(trueBitSet)
      // -- ENDLESS LOOP CHECKING
      trueBitSet.toStream.map(i => cycleStartDate.plusDays(i))
    }

    def loop(cycleStartDate: LocalDate): Stream[LocalDate] = {
      chunkSeq(cycleStartDate) #::: loop(cycleStartDate.plusDays(nCycleDays))
    }

    loop(cycleAnchorDate)
  }

}

/**
 * The Daily Schedule builder starts with the number of days in the cycle and
 * an offset indicating the start day(s) of the cycle.
 *
 * @param nCycleDays is the number of days in the cycle, and can be any positive integer.
 *                     1 means daily, 2 means every two days, 7 means weekly, etc.
 * @param dayOffset indicates the first day of the cycle. It is a modulo of nCycleDays,
 *                     so must be between 0 and (nCycleDays-1)
 *                     This is relative to the Wapita calendar EPOCH (1970-01-01).
 *                     In other words to start on 1970-01-01, dayOffset would be 0.
 */
class DailyScheduleBuilder[A <: Schedule](
    dailyCycle: DailyCycle,
    validfCycleSheduleDayMapOpt: Option[LocalDate => BitSet])
{
  import DailySchedule._
  import DayOfWeek._

  /** Builder traverses every day. Cycle Number of Days is 1, and offset is 0. */
  def withCycleSheduleDayMapFunction(fCycleSheduleDayMapFunc: LocalDate => BitSet): DailyScheduleBuilder[A] =
    new DailyScheduleBuilder[A](dailyCycle, Some(fCycleSheduleDayMapFunc))

  /**
   * O-based list of days in the cycle, in whatever order, relative to the dayOffset
   * Each cycleDay must be ge 0 and lt nCycleDays
   */
  def withCycleDays(cycleDays: Int*): DailyScheduleBuilder[A] =
    withCycleSheduleDayMapFunction(_ => cycleScheduleDayMap(cycleDays))

  /** Set the cyclic day(s) of the week in which the schedule falls.
   *  REQUIREMENT: The number of days in the cycle (parameter 'nCycleDays) must be a
   *  multiple of the DAYS_PER_WEEK, 7.
   */
  def withWeekDayOffsetsInCycle( dows: (Int, DayOfWeek)*): DailyScheduleBuilder[A] = {
    require(dailyCycle.daysInCycle % DaysPerWeek == 0)
    withCycleDays(dows.map {
      case (weekOffset, dayOfWeek) => dailyCycle.weekCycleOffset(weekOffset,dayOfWeek)
    }: _*)
  }

  /** Set the cyclic day(s) of the week in which the schedule falls.
   *  REQUIREMENT: The number of days in the cycle (parameter 'nCycleDays)
   *  must be a multiple of the DAYS_PER_WEEK, 7.
   */
  def withWeekDaysInCycle(dows: DayOfWeek*): DailyScheduleBuilder[A] = {
    require(dailyCycle.daysInCycle % DaysPerWeek == 0)
    val dayOffsets: Seq[Int] = dows.map(tup => dailyCycle.dayOfWeekOffset(tup))
    withCycleDays(dayOffsets: _*)
  }

  def weekdays(): DailyScheduleBuilder[A] =
    withWeekDaysInCycle(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY)

  def weekends(): DailyScheduleBuilder[A] =
    withWeekDaysInCycle(SATURDAY, SUNDAY)

  def build(): A = {
    val modDayOffset = dailyCycle.dayOffset % dailyCycle.daysInCycle
    val validfCycleSheduleDayMap =
      validfCycleSheduleDayMapOpt.getOrElse((ld: LocalDate) => firstDayBitMap())

    new DailySchedule(validfCycleSheduleDayMap, dailyCycle).asInstanceOf[A]
  }
}

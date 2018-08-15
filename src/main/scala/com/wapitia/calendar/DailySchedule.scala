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
case class DailySchedule(nCycleDays: Int, dayOffset: Int, fCycleSheduleDayMap: LocalDate => BitSet)
extends Schedule {

  import DailySchedule._

  override def onOrAfter(onOrAfterDate: LocalDate): Stream[LocalDate] = {
    val acd: LocalDate = cycleAnchorDate(onOrAfterDate)
    assert(acd.toEpochDay() <= onOrAfterDate.toEpochDay())
    assert(acd.toEpochDay() > onOrAfterDate.toEpochDay() - nCycleDays)
    val res = chunkCycleStream(onOrAfterDate, acd, nCycleDays, fCycleSheduleDayMap)
    res
  }

  /** The first day (day 0) of the cycle in which onOrAfterDate resides,
  // according to nCycleDays and dayOffset.
   *
   */
  def cycleAnchorDate(onOrAfterDate: LocalDate): LocalDate = DailySchedule.cycleAnchorDate(onOrAfterDate, nCycleDays, dayOffset)
}

object DailySchedule {

  import DayOfWeek._

  val DayOfWeekOffset = dayOfWeekOffset(Epoch.getDayOfWeek, 0)

  def builder(nCycleDays: Int, dayOffset: Int) = new Builder(
      nCycleDays = nCycleDays,
      dayOffset = dayOffset,
      validfCycleSheduleDayMapOpt = None,
      flagAllfCycleSheduleDayMap = false)

  /** Every day is a day of the schedule */
  def daily() = builder(1,0)

  /** Builder traverses weekly, starting at a particular day of the week. */
  def weekly(startDayOfWeek: DayOfWeek): Builder = mutipleWeekly(1, startDayOfWeek, 0)

  /** Builder traverses weekly with the week starting on Sunday. */
  def weeklyStartingSunday(): Builder = weekly(SUNDAY)

  /** Builder traverses weekly, starting at a particular day of the week. */
  def mutipleWeekly(weeksInCycle: Int, startDayOfWeek: DayOfWeek, startCycleWeekOffset: Int): Builder = {
    require(weeksInCycle > 0)
    require(startCycleWeekOffset >= 0)
    require(startCycleWeekOffset < weeksInCycle)
    builder(DaysPerWeek * weeksInCycle, calcCycleWeekOffset(startDayOfWeek, startCycleWeekOffset))
  }

  /** Builder traverses weekly, starting at a particular day of the week. */
  def mutipleWeekly(weeksInCycle: Int, startDayOfWeek: DayOfWeek, withFirstWeekInCycleHavingDate: LocalDate): Builder =
    builder(DaysPerWeek * weeksInCycle, startCycleWeekOffset(withFirstWeekInCycleHavingDate, weeksInCycle, startDayOfWeek))

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
  class Builder(
      nCycleDays: Int,
      dayOffset: Int,
      validfCycleSheduleDayMapOpt: Option[LocalDate => BitSet],
      flagAllfCycleSheduleDayMap: Boolean
  ) {
    require(nCycleDays > 0, "number of days in cycle must be some positive integer")
    require(dayOffset >= 0, "dayOffset must be zero or a positive integer")
    require(dayOffset < nCycleDays, "dayOffset must be less than the number of days in the cycle")

    /** Builder traverses every day. Cycle Number of Days is 1, and offset is 0. */

    def withCycleSheduleDayMapFunction(fCycleSheduleDayMapFunc: LocalDate => BitSet) = new Builder(nCycleDays, dayOffset, Some(fCycleSheduleDayMapFunc), false)

    /**
     * O-based list of days in the cycle, in whatever order, relative to the dayOffset
     * Each cycleDay must be ge 0 and lt nCycleDays
     */
    def withCycleDays(cycleDays: Int*) = withCycleSheduleDayMapFunction(_ => cycleSheduleDayMap(cycleDays))

    /** Set the cyclic day(s) of the week in which the schedule falls.
     *  REQUIREMENT: The number of days in the cycle (parameter 'nCycleDays) must be a multiple of the DAYS_PER_WEEK, 7.
     */
    def withWeekDayOffsetsInCycle( dows: (Int, DayOfWeek)*) = {
      require(nCycleDays % DaysPerWeek == 0)
      withCycleDays(dows.map(tup => tup._1 * DaysPerWeek + dayOfWeekOffset(tup._2, dayOffset)): _*)
    }

    /** Set the cyclic day(s) of the week in which the schedule falls.
     *  REQUIREMENT: The number of days in the cycle (parameter 'nCycleDays) must be a multiple of the DAYS_PER_WEEK, 7.
     */
    def withWeekDaysInCycle(dows: DayOfWeek*) = {
      require(nCycleDays % DaysPerWeek == 0)
      val dayOffsets: Seq[Int] = dows.map(tup => dayOfWeekOffsetX(tup, nCycleDays, dayOffset))
      println(dayOffsets mkString ", ")
      withCycleDays(dayOffsets: _*)
    }

    def weekdays() = withWeekDaysInCycle(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY)

    def weekends() = withWeekDaysInCycle(SUNDAY, SATURDAY)

    def withAllfCycleSheduleDayMap() = new Builder(nCycleDays, dayOffset, None, flagAllfCycleSheduleDayMap = true)

    def build(): DailySchedule = {
      val modDayOffset = dayOffset % nCycleDays
      val validfCycleSheduleDayMap =
        if (flagAllfCycleSheduleDayMap)
          (ld: LocalDate) => everyDayMap(nCycleDays)
        else
          validfCycleSheduleDayMapOpt.getOrElse((ld: LocalDate) => everyDayMap(nCycleDays))

      new DailySchedule(
        nCycleDays = nCycleDays,
        dayOffset = modDayOffset,
        fCycleSheduleDayMap = validfCycleSheduleDayMap)
    }
  }

  def dayOfWeekOffset(dow: DayOfWeek, cycleStartDayOffset: Int): Int = {
    val dowo = dow.ordinal()
    val res = dow.ordinal() - cycleStartDayOffset
    if (res < 0) res + DaysPerWeek
    else res
  }

  def dayOfWeekOffsetX(dow: DayOfWeek, nCycleDays: Int, dayOffset: Int): Int = {
    val dowo = dow.ordinal
    val res = dow.ordinal - dayOffset
    if (res < 0) res + DaysPerWeek
    else res
  }

  /** Calculate the day offset
   *
   */
  def startCycleWeekOffset(withFirstWeekInCycleHavingDate: LocalDate, weeksInCycle: Int, startDayOfWeek: DayOfWeek): Int = {
    ???
  }

  def calcCycleWeekOffset(startDayOfWeek: DayOfWeek, startCycleWeekOffset: Int): Int = {
    startCycleWeekOffset * DaysPerWeek + DayOfWeekOffset
  }

  def cycleSheduleDayMap(cycleDays: Seq[Int]): BitSet = BitSet(cycleDays: _*)

  def everyDayMap(cycle: Int): BitSet = BitSet(0 to (cycle-1): _*)

  /** The first day (day 0) of the cycle in which onOrAfterDate resides,
   * according to nCycleDays and dayOffset.
   */
  def cycleAnchorDate(onOrAfterDate: LocalDate, nCycleDays: Int, dayOffset: Int): LocalDate = {
    val bde: Long = (dayOffset - onOrAfterDate.toEpochDay()) % nCycleDays
    val baseDateEpoch: Long = if (bde > 0) bde - nCycleDays else bde
    val res = onOrAfterDate.plusDays(baseDateEpoch)
    res
  }

  /** For some schedule generate a finite stream of all sequential candidate dates which fall on or after
   *  some particular date.
   *
   *  @param onOrAfterDate starting date for the stream of candidate dates
   *  @param cycleAnchorDate date of the first day of the first candidate cycle, a calculated date
   *         normalized to be on or before the `onOrAfterDate, but within nCycleDays of that date
   *  @param nCycleDays the number of days in the schedule, must be a positive integer
   *  @param fCycleScheduleDayMap function that when given the start date of some cycle will return a bitset
   *         whose bits represent each day of that cycle, marking those that are to be added to the schedule stream.
   *
   */
  def chunkCycleStream(onOrAfterDate: LocalDate, cycleAnchorDate: LocalDate, nCycleDays: Int, fCycleSheduleDayMap: LocalDate => BitSet): Stream[LocalDate] = {

    require( ! cycleAnchorDate.isAfter(onOrAfterDate))
    require(cycleAnchorDate.isAfter(onOrAfterDate.minusDays(nCycleDays.toLong)))
    require(nCycleDays > 0)

    /** Generate a finite stream of the candidate dates for the cycle whose first day is the given start date.
     *
     */
    def chunkSeq(cycleStartDate: LocalDate): Stream[LocalDate] = {
      val candidateBitsInCycle: BitSet = fCycleSheduleDayMap(cycleStartDate)
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
      trueBitSet.toStream.map(i => cycleStartDate.plusDays(i))
    }

    def loop(cycleStartDate: LocalDate): Stream[LocalDate] = chunkSeq(cycleStartDate) #::: loop(cycleStartDate.plusDays(nCycleDays))

    loop(cycleAnchorDate)
  }

}

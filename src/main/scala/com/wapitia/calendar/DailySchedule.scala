package com.wapitia
package calendar

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

  override def starting(onOrAfterDate: LocalDate): Stream[LocalDate] = {
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

  def builder(dailyCycle: DailyCycle): DailyScheduleBuilder[DailySchedule] = new DailyScheduleBuilder[DailySchedule](dailyCycle, None)

  /** Every day is a day of the schedule */
  def daily() = builder(DailyCycle.Daily)

  /** Create a BitSet where days in the cycle are explicitly given in a 0-based sequence. */
  def cycleScheduleDayMap(cycleDays: Seq[Int]): BitSet =
    BitSet(cycleDays: _*)

  /** A BitSet where every day in the cycle (0.. nDays-1) is set */
  def everyDayBitMap(nDays: Int): BitSet = BitSet(0 to (nDays-1): _*)

  /** A BitSet where only the single day as given by the offset is set */
  def offsetDayBitMap(dayOffset: Int): BitSet = BitSet(dayOffset)

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

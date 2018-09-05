package com.wapitia
package calendar

import java.time.DayOfWeek

/** Defines a daily cycle in terms of number of days in the cycle, and an offset to start.
 *
 *  @param daysInCycle number of days in the cycle, must be a positive number.daysInCycle
 *  @param dayOffset offset of the start day in the cycle, must be non-negative and less than dayCycle.
 */
case class DailyCycle(daysInCycle: Int, dayOffset: Int) extends CycleTemplate(daysInCycle, dayOffset) {

  import DailyCycle._

  def dayOfWeekOffset(dow: DayOfWeek): Int = {
    require(daysInCycle % DaysPerWeek == 0)
    val res = daysInCycle + dow.ordinal() - dayOffset - DayOfWeekOffset
    res % DaysPerWeek
  }

  def anchorDayOffset(epochOnOrAfter: Long): Long = {
    val bde: Long = (daysInCycle + dayOffset - epochOnOrAfter) % daysInCycle
    val baseDateEpoch: Long = if (bde > 0) bde - daysInCycle else bde
    baseDateEpoch
  }

  def weekCycleOffset(weekOffset: Int, dayOfWeek: DayOfWeek) =
    weekOffset * DaysPerWeek + dayOfWeekOffset(dayOfWeek)

}

object DailyCycle {

  /** A tight daily cycle, every day is a scheduled day */
  val Daily: DailyCycle = DailyCycle(1, 0)

  /** The number of days between Thursday and the next Monday, which is 4.
   *  Monday is the first item of the `DayOfWeek` enumeration (day 0).
   *  Thursday is the day of week of "1970-01-01" which is the Epoch Day (day 0).
   *  This offset is used to adjust the weekly modulo calculations in the
   *  calendar package, which borrows both values from the underlying date
   *  representation `java.time`.
   */
  val DayOfWeekOffset = DaysPerWeek - EpochDayOfWeek.getValue

  /** The number of days in a cycle to assume when the value has not yet been defined.
   *  This function is passed to DailyCycle.Builder as its default and assumed to be overwritten
   *  via the builder's `sizeDefault` function.
   */
  def defaultNumDaysInCycle(): Int = throw new RuntimeException("Day cycle size not defined")

  def multipleWeekly(weeksInCycle: Int, startDayOfWeek: DayOfWeek, startCycleWeekOffset: Int): DailyCycle = {
    val daysInCycle = DaysPerWeek * weeksInCycle
    builder().cycleSize(daysInCycle)
      .offset((startCycleWeekOffset * DaysPerWeek + DayOfWeekOffset + startDayOfWeek.getValue) % daysInCycle)
      .build()
  }

  def builder(): Builder = new Builder(None, None, defaultNumDaysInCycle)

  /** Builder class adding convenience methods to accumulate each parameter individually or together. */
  class Builder(dayCycleOpt: Option[Int], dayOffsetOpt: Option[Int], dayCycleDefault: => Int)
    extends CycleBuilder[DailyCycle,DailyCycle.Builder](dayCycleOpt, dayOffsetOpt, dayCycleDefault)
  {
    override def builder(dayCycleOpt: Option[Int], dayOffsetOpt: Option[Int], dayCycleDefault: => Int): Builder =
      new Builder(dayCycleOpt, dayOffsetOpt, dayCycleDefault)

    override def make(cycleSize: Int, offset: Int): DailyCycle =
      DailyCycle(cycleSize, offset)

  }

}

package com.wapitia
package calendar

import java.time.LocalDate
import java.time.YearMonth

/** Defines a daily cycle in terms of number of days in the cycle, and an offset to start.
 *
 *  @param daysInCycle number of days in the cycle, must be a positive number.daysInCycle
 *  @param dayOffset offset of the start day in the cycle, must be non-negative and less than dayCycle.
 */
case class DailyCycle(daysInCycle: Int, dayOffset: Int) extends CycleTemplate(daysInCycle, dayOffset)

object DailyCycle {

  val Daily: DailyCycle = DailyCycle(1, 0)

  def defaultDayCycle(): Int = throw new RuntimeException("Day cycle size not defined")

  def builder(): Builder = new Builder(None, None, defaultDayCycle)

  /** Builder class adding convenience methods to accumulate each parameter individually or together. */
  class Builder(dayCycleOpt: Option[Int], dayOffsetOpt: Option[Int], dayCycleDefault: => Int)
    extends CycleTemplate.Builder[DailyCycle,DailyCycle.Builder](dayCycleOpt, dayOffsetOpt, dayCycleDefault)
  {
    override def builderConstructor(dayCycleOpt: Option[Int], dayOffsetOpt: Option[Int], dayCycleDefault: => Int): Builder =
      new Builder(dayCycleOpt, dayOffsetOpt, dayCycleDefault)

    override def objConstructor(cycleSize: Int, offset: Int): DailyCycle =
      DailyCycle(cycleSize, offset)
  }

}

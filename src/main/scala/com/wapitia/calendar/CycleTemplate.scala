package com.wapitia.calendar

/** Commonality between DailyCycle and MonthlyCycle encapsulated here */
class CycleTemplate(cycleSize: Int, offset: Int) {

  assert(cycleSize > 0)
  assert(offset >= 0 && offset < cycleSize)

}

object CycleTemplate {

  /** Builder class adding convenience methods to accumulate each parameter individually or together. */
  abstract class Builder[A <: CycleTemplate,B](monthCycleOpt: Option[Int], monthOffsetOpt: Option[Int], monthCycleDefault: => Int) {

    def builderConstructor(monthCycleOpt: Option[Int], monthOffsetOpt: Option[Int], monthCycleDefault: => Int): B

    def objConstructor(cycleSize: Int, offset: Int): A

    /** set the months-in-cycle component.
     *  Warning this will reduce the offset if previously set be one less than this monthCycle.
     *  Otherwise sets offset to 0.
     */
    def monthsInCycle(monthCycle: Int): B = builderConstructor(Some(monthCycle), monthOffsetOpt, monthCycleDefault)

    /** Set the month offset component.
     *  Warning this will set the months-in-cycle to be one more than this offset if not previously set
     *  or if currently set less than or equal to this offset.
     */
    def offset(monthOffset: Int): B = builderConstructor(monthCycleOpt, Some(monthOffset), monthCycleDefault)

    def set(obj: MonthlyCycle): B = builderConstructor(Some(obj.monthsInCycle), Some(obj.monthOffset), monthCycleDefault)

    /** Return the accumulated object, or `Monthly` by default. */
    def build(): A = {
      val monthCycle: Int = monthCycleOpt.getOrElse(monthCycleDefault)
      val monthOffset: Int = monthOffsetOpt.getOrElse(0)
      objConstructor(monthCycle,monthOffset)
    }
  }

}

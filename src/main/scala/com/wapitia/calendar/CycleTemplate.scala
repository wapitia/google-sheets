package com.wapitia.calendar

/** Commonality between DailyCycle and MonthlyCycle encapsulated here */
class CycleTemplate(val cycleSize: Int, val offset: Int) {

  assert(cycleSize > 0)
  assert(offset >= 0 && offset < cycleSize)
}

object CycleTemplate {

  /** Builder class adding convenience methods to accumulate each parameter individually or together. */
  abstract class Builder[A <: CycleTemplate,B <: Builder[A,B]](
      cycleSizeOpt: Option[Int], offsetOpt: Option[Int], cycleSizeDefault: => Int)
  {

    def builderConstructor(cycleSizeOpt: Option[Int], offsetOpt: Option[Int], cycleSizeDefault: => Int): B

    def objConstructor(cycleSize: Int, offset: Int): A

    /** set the months-in-cycle component.
     *  Warning this will reduce the offset if previously set be one less than this cycleSize.
     *  Otherwise sets offset to 0.
     */
    def monthsInCycle(cycleSize: Int): B = builderConstructor(Some(cycleSize), offsetOpt, cycleSizeDefault)

    /** Set the month offset component.
     *  Warning this will set the months-in-cycle to be one more than this offset if not previously set
     *  or if currently set less than or equal to this offset.
     */
    def offset(offset: Int): B = builderConstructor(cycleSizeOpt, Some(offset), cycleSizeDefault)

    def set(obj: CycleTemplate): B = builderConstructor(Some(obj.cycleSize), Some(obj.offset), cycleSizeDefault)

    /** Return the accumulated object, or `Monthly` by default. */
    def build(): A = {
      val cycleSize: Int = cycleSizeOpt.getOrElse(cycleSizeDefault)
      val offset: Int = offsetOpt.getOrElse(0)
      objConstructor(cycleSize,offset)
    }
  }

}

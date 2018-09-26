package com.wapitia
package calendar

/** Commonality between DailyCycle and MonthlyCycle encapsulated here */
class CycleTemplate(val cycleSize: Int, val offset: Int) {

  require(cycleSize > 0)
  require(offset >= 0 && offset < cycleSize)
}

/** Abstract Builder accumulates each CycleTemplate parameter individually */
abstract class CycleBuilder[A <: CycleTemplate,B <: CycleBuilder[A,B]](
    cycleSizeOpt: Option[Int], offsetOpt: Option[Int], cycleSizeDefault: => Int)
{

  // abstract
  def builder(cycleSizeOpt: Option[Int], offsetOpt: Option[Int], cycleSizeDefault: => Int): B

  // abstract
  def make(cycleSize: Int, offset: Int): A

  /** set the months-in-cycle component.
   *  Warning this will reduce the offset if previously set be one less than this cycleSize.
   *  Otherwise sets offset to 0.
   */
  def cycleSize(size: Int): B = builder(Some(size), offsetOpt, cycleSizeDefault)

  /** Set the month offset component.
   *  Warning this will set the months-in-cycle to be one more than this offset if not previously set
   *  or if currently set less than or equal to this offset.
   */
  def offset(offset: Int): B = builder(cycleSizeOpt, Some(offset), cycleSizeDefault)

  /** Overwrite both the cycleSize and offset options with the values of the given other cycle.
   *  The cycleSizeDefault is not changed.
   */
  def cycle(cycle: A): B = builder(Some(cycle.cycleSize), Some(cycle.offset), cycleSizeDefault)

  /** Set the cycleSizeDefault */
  def sizeDefault(szDef: => Int) = builder(cycleSizeOpt, offsetOpt, szDef)

  /** Make and return a `Cycle` instance. If the `cycleSize` is not defined, `cycleSizeDefault` is assumed.
   *  If `offset` is not defined, assume zero.
   */
  def build(): A = {
    val cycleSize: Int = cycleSizeOpt.getOrElse(cycleSizeDefault)
    val offset: Int = offsetOpt.getOrElse(0)
    make(cycleSize,offset)
  }
}

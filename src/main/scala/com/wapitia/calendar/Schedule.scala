package com.wapitia
package calendar

import java.time.LocalDate

/** A calendar schedule (partitioned by day) not pinned to a particular date.
 *  Produces a stream of schedule dates relative to some given date.
 *  For those streams that may be exhausted (i.e. the schedule ends),
 *  The stream will produce LocalDate.max, which should be treated as a special
 *  invalid value by the client code.
 */
trait Schedule {

  def onOrAfter(date: LocalDate): Stream[LocalDate]
}

object Schedule {

  /** Monthly on the given day with no adjustment */
  def monthly(day1: Int): Schedule = MonthlySchedule.monthly(day1)
}

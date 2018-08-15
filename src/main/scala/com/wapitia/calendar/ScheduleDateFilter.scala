package com.wapitia
package calendar

import java.time.LocalDate

/** The ScheduleDateFilter keeps ranges of passable dates, such as work days
 *  @FunctionalInterface
 */
trait ScheduleDateFilter {

  /** Test if the given date passes, so is a valid date. Return true iff valid. */
  def test(date: LocalDate): Boolean
}

package com.wapitia
package calendar

import com.wapitia.common.{Enum,EValue}

/** Policy of how to treat the condition where a particular day fails its test.
 *
 *  {{{
 *    enum WorkingSchedulePolicy {
 *      case SkipDay, NextAvailableDayNoSkip, NextAvailableDaySkipOnOverlap
 *    }
 *  }}}
 */
sealed trait WorkingSchedulePolicy extends WorkingSchedulePolicy.Value with EValue[WorkingSchedulePolicy]

object WorkingSchedulePolicy extends Enum[WorkingSchedulePolicy] {

  /** When a WorkingSchedule fails a test for a given day, skip that
   *  scheduled day completely.
   */
  case object SkipDay extends WorkingSchedulePolicy

  /** When a WorkingSchedule fails a test for a given day, advance the
   *  schedule to the next candidate working schedule day.
   */
  case object NextAvailableDayNoSkip extends WorkingSchedulePolicy

  /** When a WorkingSchedule fails a test for a given day, advance the
   *  schedule to the next candidate working schedule day, so long as
   *  that day doesn't overtake another scheduled day.
   */
  case object NextAvailableDaySkipOnOverlap extends WorkingSchedulePolicy

  val enumValues = List(SkipDay, NextAvailableDayNoSkip, NextAvailableDaySkipOnOverlap)
}

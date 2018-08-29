package com.wapitia

import java.time.{LocalDate, DayOfWeek, Month}
import scala.util.matching.Regex

/** Calendar and Schedule global constants.
 *  Wapitia's calendar system is backed by [[java.time._]], and so
 *  these constants are derived from that system.
 *  The asserts are here to ensure [[java.time]] values remain as is, though it would be
 *  surprising (and broken) if these changed.
 */
package object calendar {

  /** Wapitia's Epoch is the basis date 0 from which module date cycle
   *  offsets are relative.
   *  Chosen to match [[java.time.LocalDate]].ofEpochDay(0), which derives from
   *  POSIX time:
   *  [[https://en.wikipedia.org/wiki/Epoch_(reference_date)#1970-01-01]]
   */
  val Epoch = LocalDate.of(1970, 1, 1)
  assert(Epoch == LocalDate.ofEpochDay(0L))

  /** A Thursday */
  val EpochDayOfWeek = Epoch.getDayOfWeek

  /** Number of days per week which is 7 */
  val DaysPerWeek = DayOfWeek.values().length
  assume(DaysPerWeek == 7)

  /** Number of months per year which is 12 */
  val MonthsPerYear = Month.values().length
  assume(MonthsPerYear == 12)

  /** Number of days in a 400 year cycle.
   *  Matches the (private) LocalDate.DAYS_PER_CYCLE
   */
  val DaysPer400Years = 146097

  /** An "exact" average number of days per year.
   *  Accounts for leap years according to the
   *  [[http://www.wolframalpha.com/input/?i=leap+year+criteria leap year calculation]].
   *  The year is divisible by 4 and not divisible by 100 or the year is divisible by 400
   *  Works out to be 365.2425
   */
  val DaysPerYear: Double = DaysPer400Years.toDouble / 400.0

  /** An "exact" average number of days per month. Works out to be 30.436875 */
  val DaysPerMonth: Double = DaysPer400Years.toDouble / (400.0 * MonthsPerYear.toDouble)

  // verify that the underlying date holder `java.time.LocalDate` can handle 9 digit years
  private val SupportedYearDigits = Math.log10((java.time.Year.MAX_VALUE+1).toDouble).toInt
  assume(SupportedYearDigits >= 9)

  /** Only strict canonical date string patterns of the form "yyyy-mm-dd"
   *  are allowed, where "yyyy" is a year in the range "0000" to "999999999".
   */
  val LocalDatePattern: Regex = "^([0-9]{4,9})\\-(1[0-2]|0[1-9])\\-(3[01]|[12][0-9]|0[1-9])$"
    .r("year","month","day")

  import scala.language.implicitConversions

  /** Convert date string of the form "yyyy-mm-dd" (year-month-day), such as "2018-03-28" into a LocalDate.
   *  This is made implicit since local data conversions are common in the `com.wapitia.calendar` client code,
   *  and shouldn't be confused with other strings in the code.
   */
  implicit def toDate(s: String) = {
    require(LocalDatePattern.findFirstIn(s).isDefined, s"Not a valid date string: '$s'")
    LocalDate.parse(s)
  }

}

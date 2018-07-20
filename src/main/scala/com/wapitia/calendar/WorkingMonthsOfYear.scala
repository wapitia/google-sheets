package com.wapitia
package calendar

import java.time.LocalDate

/** Return a set of the working months of the year for a given year */
trait WorkingMonthsOfYear {

  /** An ordered list of the month numbers in the valid set for this year.
   *  From 1 (January) to 12 (December).
   *  List is monotonically increasing. May be empty. 
   */
  def inYear(date: LocalDate): List[Int]
  
}
package com.wapitia
package calendar

import java.time.LocalDate
import scala.collection.BitSet

trait GenericScheduleBuilder {

  /** offset in days from normal cycle anchor date */
  def offset(offs: Int): GenericScheduleBuilder

  def build(): Schedule
}

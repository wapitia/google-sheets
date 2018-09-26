package com.wapitia.calendar

import java.time.{DayOfWeek, LocalDate}
import java.time.DayOfWeek._
import org.junit.{Test, Ignore}
import org.junit.Assert._

class SandboxTest {

  /*
   * For WeeklySchedule.multipleWeekly(2, SUNDAY, 0).withWeekDayOffsetsInSchedule((1,THURSDAY))
   * a schedule day is 2018-08-30
   *
   * For WeeklySchedule.multipleWeekly(2, SUNDAY, 0).withWeekDayOffsetsInSchedule((0,THURSDAY))
   * a schedule day is 2018-09-06
   *
   * This is the benchmark for the expecteds in these offset tests
   */
  @Test
  def testMultiWeeklyOffset1() {

    val initialStartingDate: LocalDate =  "2018-09-06"
    for (dayDiff: Int <- 0 to 20) {
      val startingDate = initialStartingDate.plusDays(dayDiff.toLong)
      val offset = WeeklySchedule.multiWeeklyOffset(2, startingDate)
      println(s"for date $startingDate, offset = $offset")
//      val dateStrm: Stream[LocalDate] = WeeklySchedule.biweeklyStarting(startingDate)
//      val expecteds = initalExpecteds.map(d => d.plusDays(dayDiff.toLong))
//      (expecteds zip dateStrm).foreach {
//        case (exp, act) => assertEquals(s"Starting with date $startingDate, expected $exp, but got $act", exp, act)
//      }
    }

  }

}

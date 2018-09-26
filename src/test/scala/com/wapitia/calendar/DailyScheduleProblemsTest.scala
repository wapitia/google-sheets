package com.wapitia.calendar

import java.time.{DayOfWeek, LocalDate}
import java.time.DayOfWeek._
import org.junit.{Test, Ignore}
import org.junit.Assert._

class DailyScheduleProblemsTest {

  @Test @Ignore
  def testBiWeeklyScheduleMw2b() {

//    val biwSched: Schedule = WeeklySchedule.multipleWeekly(2, SUNDAY, 0)
//      .withWeekDayOffsetsInSchedule((0,THURSDAY))
//      .build()

    val initalExpecteds = Stream[LocalDate](
      "2018-09-06",
      "2018-09-20",
      "2018-10-04",
      "2018-10-18",
      "2018-11-01",
      "2018-11-15")
    val initialStartingDate: LocalDate =  "2018-09-06"
    for (dayDiff: Int <- 0 to 100) {
      val startingDate = initialStartingDate.plusDays(dayDiff.toLong)
      val dateStrm: Stream[LocalDate] = WeeklySchedule.biweeklyStarting(startingDate)
      val expecteds = initalExpecteds.map(d => d.plusDays(dayDiff.toLong))
      (expecteds zip dateStrm).foreach {
        case (exp, act) => assertEquals(s"Starting with date $startingDate, expected $exp, but got $act", exp, act)
      }
    }

  }

}
